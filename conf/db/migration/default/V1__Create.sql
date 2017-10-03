CREATE TABLE STATUS_UPDATE (
  ID                INTEGER PRIMARY KEY,
  APP_ID            TEXT    NOT NULL,
  STATUS_TYPE       TEXT    NOT NULL,
  CREATED_TIMESTAMP INTEGER NOT NULL
);

CREATE TABLE RFI (
  ID                 INTEGER PRIMARY KEY,
  RFI_ID             TEXT    NOT NULL,
  APP_ID             TEXT    NOT NULL,
  STATUS             TEXT    NOT NULL,
  RECEIVED_TIMESTAMP INTEGER NOT NULL,
  DUE_TIMESTAMP      INTEGER,
  SENT_BY            TEXT,
  MESSAGE            TEXT
);

CREATE TABLE RFI_REPLY (
  INTEGER_ID         INTEGER PRIMARY KEY,
  ID                 TEXT    NOT NULL UNIQUE,
  RFI_ID             TEXT    NOT NULL,
  CREATED_BY_USER_ID TEXT    NOT NULL,
  CREATED_TIMESTAMP  INTEGER NOT NULL,
  MESSAGE            TEXT    NOT NULL,
  ATTACHMENTS        TEXT    NOT NULL,
  FOREIGN KEY (RFI_ID) REFERENCES RFI (RFI_ID)
);

CREATE TABLE RFI_WITHDRAWAL (
  INTEGER_ID         INTEGER PRIMARY KEY,
  ID                 TEXT    NOT NULL UNIQUE,
  RFI_ID             TEXT    NOT NULL,
  CREATED_BY_USER_ID TEXT    NOT NULL,
  CREATED_TIMESTAMP  INTEGER NOT NULL,
  RECIPIENT_USER_IDS TEXT    NOT NULL,
  MESSAGE            TEXT    NOT NULL,
  FOREIGN KEY (RFI_ID) REFERENCES RFI (RFI_ID)
);

CREATE TABLE APPLICATION (
  ID                  INTEGER PRIMARY KEY,
  APP_ID              TEXT    NOT NULL UNIQUE,
  COMPANY_ID          TEXT    NOT NULL,
  CREATED_BY          TEXT    NOT NULL,
  CREATED_TIMESTAMP   INTEGER NOT NULL,
  SUBMITTED_TIMESTAMP INTEGER,
  DESTINATION_LIST    TEXT,
  APPLICANT_REFERENCE TEXT    NOT NULL,
  CASE_REFERENCE      TEXT,
  CASE_OFFICER_ID     TEXT
);

CREATE TABLE AMENDMENT (
  INTEGER_ID         INTEGER PRIMARY KEY,
  ID                 TEXT    NOT NULL UNIQUE,
  APP_ID             TEXT    NOT NULL,
  CREATED_BY_USER_ID TEXT    NOT NULL,
  CREATED_TIMESTAMP  INTEGER NOT NULL,
  MESSAGE            TEXT    NOT NULL,
  ATTACHMENTS        TEXT    NOT NULL
);

CREATE TABLE WITHDRAWAL_REQUEST (
  INTEGER_ID         INTEGER PRIMARY KEY,
  ID                 TEXT    NOT NULL UNIQUE,
  APP_ID             TEXT    NOT NULL,
  CREATED_BY_USER_ID TEXT    NOT NULL,
  CREATED_TIMESTAMP  INTEGER NOT NULL,
  MESSAGE            TEXT    NOT NULL,
  ATTACHMENTS        TEXT    NOT NULL
);

CREATE TABLE WITHDRAWAL_REJECTION (
  INTEGER_ID         INTEGER PRIMARY KEY,
  ID                 TEXT    NOT NULL UNIQUE,
  APP_ID             TEXT    NOT NULL,
  CREATED_BY_USER_ID TEXT    NOT NULL,
  CREATED_TIMESTAMP  INTEGER NOT NULL,
  MESSAGE            TEXT    NOT NULL
);

CREATE TABLE WITHDRAWAL_APPROVAL (
  INTEGER_ID         INTEGER PRIMARY KEY,
  ID                 TEXT    NOT NULL UNIQUE,
  APP_ID             TEXT    NOT NULL,
  CREATED_BY_USER_ID TEXT    NOT NULL,
  CREATED_TIMESTAMP  INTEGER NOT NULL,
  MESSAGE            TEXT    NOT NULL
);

CREATE TABLE DRAFT (
  ID          INTEGER PRIMARY KEY,
  RELATED_ID  TEXT NOT NULL,
  DRAFT_TYPE  TEXT NOT NULL,
  ATTACHMENTS TEXT NOT NULL
);

CREATE TABLE SIEL (
  ID                  INTEGER PRIMARY KEY,
  SIEL_ID             TEXT    NOT NULL,
  COMPANY_ID          TEXT    NOT NULL,
  APPLICANT_REFERENCE TEXT    NOT NULL,
  CASE_REFERENCE      TEXT    NOT NULL,
  ISSUE_TIMESTAMP     INTEGER NOT NULL,
  EXPIRY_TIMESTAMP    INTEGER NOT NULL,
  STATUS              TEXT    NOT NULL,
  SITE_ID             TEXT    NOT NULL,
  DESTINATION_LIST    TEXT    NOT NULL
);

CREATE TABLE OUTCOME (
  INTEGER_ID         INTEGER PRIMARY KEY,
  ID                 TEXT    NOT NULL,
  APP_ID             TEXT    NOT NULL,
  CREATED_BY_USER_ID TEXT    NOT NULL,
  RECIPIENT_USER_IDS TEXT    NOT NULL,
  CREATED_TIMESTAMP  INTEGER NOT NULL,
  DOCUMENTS          TEXT    NOT NULL
);

CREATE TABLE NOTIFICATION (
  INTEGER_ID         INTEGER PRIMARY KEY,
  ID                 TEXT    NOT NULL,
  APP_ID             TEXT    NOT NULL,
  NOTIFICATION_TYPE  TEXT    NOT NULL,
  CREATED_BY_USER_ID TEXT    NOT NULL,
  CREATED_TIMESTAMP  INTEGER NOT NULL,
  RECIPIENT_USER_IDS TEXT    NOT NULL,
  MESSAGE            TEXT    NOT NULL,
  DOCUMENT           TEXT    NOT NULL
);
