package models;

import java.util.List;

public class Application {

  private final String id;
  private final String customerId;
  private final String createdByUserId;
  private final Long createdTimestamp;
  private final List<String> consigneeCountries;
  private final List<String> endUserCountries;
  private final String applicantReference;
  private final String caseOfficerId;
  private final String siteId;

  public Application(String id,
                     String customerId,
                     String createdByUserId,
                     Long createdTimestamp,
                     List<String> consigneeCountries,
                     List<String> endUserCountries,
                     String applicantReference,
                     String caseOfficerId,
                     String siteId) {
    this.id = id;
    this.customerId = customerId;
    this.createdByUserId = createdByUserId;
    this.createdTimestamp = createdTimestamp;
    this.consigneeCountries = consigneeCountries;
    this.endUserCountries = endUserCountries;
    this.applicantReference = applicantReference;
    this.caseOfficerId = caseOfficerId;
    this.siteId = siteId;
  }

  public String getId() {
    return id;
  }

  public String getCustomerId() {
    return customerId;
  }

  public String getCreatedByUserId() {
    return createdByUserId;
  }

  public Long getCreatedTimestamp() {
    return createdTimestamp;
  }

  public List<String> getConsigneeCountries() {
    return consigneeCountries;
  }

  public List<String> getEndUserCountries() {
    return endUserCountries;
  }

  public String getApplicantReference() {
    return applicantReference;
  }

  public String getCaseOfficerId() {
    return caseOfficerId;
  }

  public String getSiteId() {
    return siteId;
  }

}
