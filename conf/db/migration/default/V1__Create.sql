CREATE TABLE STATUS_UPDATE (
  INTEGER_ID        INTEGER PRIMARY KEY,
  ID                TEXT    NOT NULL UNIQUE,
  APP_ID            TEXT    NOT NULL,
  STATUS_TYPE       TEXT    NOT NULL,
  CREATED_TIMESTAMP INTEGER NOT NULL,
  FOREIGN KEY(APP_ID) REFERENCES APPLICATION(ID),
  UNIQUE (APP_ID, STATUS_TYPE)
);

CREATE TABLE RFI (
  INTEGER_ID         INTEGER PRIMARY KEY,
  ID                 TEXT    NOT NULL UNIQUE,
  CASE_REFERENCE     TEXT    NOT NULL,
  CREATED_TIMESTAMP  INTEGER NOT NULL,
  DUE_TIMESTAMP      INTEGER,
  CREATED_BY_USER_ID TEXT    NOT NULL,
  RECIPIENT_USER_IDS TEXT    NOT NULL,
  MESSAGE            TEXT,
  FOREIGN KEY(CASE_REFERENCE) REFERENCES CASE_DETAILS(CASE_REFERENCE)
);

CREATE TABLE RFI_REPLY (
  INTEGER_ID         INTEGER PRIMARY KEY,
  ID                 TEXT    NOT NULL UNIQUE,
  RFI_ID             TEXT    NOT NULL,
  CREATED_BY_USER_ID TEXT    NOT NULL,
  CREATED_TIMESTAMP  INTEGER NOT NULL,
  MESSAGE            TEXT    NOT NULL,
  ATTACHMENTS        TEXT    NOT NULL,
  FOREIGN KEY (RFI_ID) REFERENCES RFI(ID)
);

CREATE TABLE RFI_WITHDRAWAL (
  INTEGER_ID         INTEGER PRIMARY KEY,
  ID                 TEXT    NOT NULL UNIQUE,
  RFI_ID             TEXT    NOT NULL UNIQUE,
  CREATED_BY_USER_ID TEXT    NOT NULL,
  CREATED_TIMESTAMP  INTEGER NOT NULL,
  RECIPIENT_USER_IDS TEXT    NOT NULL,
  MESSAGE            TEXT    NOT NULL,
  FOREIGN KEY (RFI_ID) REFERENCES RFI(ID)
);

CREATE TABLE APPLICATION (
  INTEGER_ID          INTEGER PRIMARY KEY,
  ID                  TEXT    NOT NULL UNIQUE,
  CUSTOMER_ID         TEXT,
  CREATED_BY_USER_ID  TEXT,
  CREATED_TIMESTAMP   INTEGER NOT NULL,
  SUBMITTED_TIMESTAMP INTEGER,
  CONSIGNEE_COUNTRIES TEXT NOT NULL,
  END_USER_COUNTRIES  TEXT NOT NULL,
  APPLICANT_REFERENCE TEXT,
  CASE_OFFICER_ID     TEXT,
  SITE_ID             TEXT
);

CREATE TABLE CASE_DETAILS (
  INTEGER_ID           INTEGER PRIMARY KEY,
  APP_ID               TEXT NOT NULL,
  CASE_REFERENCE       TEXT NOT NULL UNIQUE,
  CREATED_BY_USER_ID   TEXT,
  CREATED_TIMESTAMP    INTEGER NOT NULL,
  FOREIGN KEY (APP_ID) REFERENCES APPLICATION(ID)
);

CREATE TABLE AMENDMENT (
  INTEGER_ID         INTEGER PRIMARY KEY,
  ID                 TEXT    NOT NULL UNIQUE,
  APP_ID             TEXT    NOT NULL,
  CREATED_BY_USER_ID TEXT    NOT NULL,
  CREATED_TIMESTAMP  INTEGER NOT NULL,
  MESSAGE            TEXT    NOT NULL,
  ATTACHMENTS        TEXT    NOT NULL,
  FOREIGN KEY(APP_ID) REFERENCES APPLICATION(ID)
);

CREATE TABLE WITHDRAWAL_REQUEST (
  INTEGER_ID         INTEGER PRIMARY KEY,
  ID                 TEXT    NOT NULL UNIQUE,
  APP_ID             TEXT    NOT NULL,
  CREATED_BY_USER_ID TEXT    NOT NULL,
  CREATED_TIMESTAMP  INTEGER NOT NULL,
  MESSAGE            TEXT    NOT NULL,
  ATTACHMENTS        TEXT    NOT NULL,
  FOREIGN KEY(APP_ID) REFERENCES APPLICATION(ID)
);

CREATE TABLE WITHDRAWAL_REJECTION (
  INTEGER_ID         INTEGER PRIMARY KEY,
  ID                 TEXT    NOT NULL UNIQUE,
  APP_ID             TEXT    NOT NULL,
  CREATED_BY_USER_ID TEXT    NOT NULL,
  CREATED_TIMESTAMP  INTEGER NOT NULL,
  RECIPIENT_USER_IDS TEXT    NOT NULL,
  MESSAGE            TEXT    NOT NULL,
  FOREIGN KEY(APP_ID) REFERENCES APPLICATION(ID)
);

CREATE TABLE WITHDRAWAL_APPROVAL (
  INTEGER_ID         INTEGER PRIMARY KEY,
  ID                 TEXT    NOT NULL UNIQUE,
  APP_ID             TEXT    NOT NULL,
  CREATED_BY_USER_ID TEXT    NOT NULL,
  CREATED_TIMESTAMP  INTEGER NOT NULL,
  RECIPIENT_USER_IDS TEXT    NOT NULL,
  MESSAGE            TEXT    NOT NULL,
  FOREIGN KEY(APP_ID) REFERENCES APPLICATION(ID)
);

CREATE TABLE DRAFT (
  INTEGER_ID  INTEGER PRIMARY KEY,
  ID          TEXT    NOT NULL UNIQUE,
  RELATED_ID  TEXT    NOT NULL,
  DRAFT_TYPE  TEXT    NOT NULL,
  ATTACHMENTS TEXT    NOT NULL
);

CREATE TABLE OUTCOME (
  INTEGER_ID         INTEGER PRIMARY KEY,
  ID                 TEXT    NOT NULL UNIQUE,
  CASE_REFERENCE     TEXT    NOT NULL,
  CREATED_BY_USER_ID TEXT    NOT NULL,
  RECIPIENT_USER_IDS TEXT    NOT NULL,
  CREATED_TIMESTAMP  INTEGER NOT NULL,
  DOCUMENTS          TEXT    NOT NULL,
  FOREIGN KEY(CASE_REFERENCE) REFERENCES CASE_DETAILS(CASE_REFERENCE)
);

CREATE TABLE NOTIFICATION (
  INTEGER_ID         INTEGER PRIMARY KEY,
  ID                 TEXT    NOT NULL UNIQUE,
  CASE_REFERENCE     TEXT    NOT NULL,
  NOTIFICATION_TYPE  TEXT    NOT NULL,
  CREATED_BY_USER_ID TEXT,
  CREATED_TIMESTAMP  INTEGER NOT NULL,
  RECIPIENT_USER_IDS TEXT    NOT NULL,
  MESSAGE            TEXT,
  DOCUMENT           TEXT,
  FOREIGN KEY(CASE_REFERENCE) REFERENCES CASE_DETAILS(CASE_REFERENCE)
);

CREATE TABLE READ (
  INTEGER_ID         INTEGER PRIMARY KEY,
  ID                 TEXT    NOT NULL UNIQUE,
  RELATED_ID         TEXT    NOT NULL,
  READ_TYPE          TEXT    NOT NULL,
  CREATED_BY_USER_ID TEXT    NOT NULL
);
