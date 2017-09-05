package models;

import com.fasterxml.jackson.annotation.JsonProperty;
import models.enums.RfiStatus;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

public class Rfi {
  @NotBlank
  private final String rfiId;
  @NotBlank
  private final String appId;
  @NotNull
  private final RfiStatus rfiStatus;
  @NotNull
  private final Long receivedTimestamp;
  @NotNull
  private final Long dueTimestamp;
  @NotBlank
  private final String sentBy;
  @NotBlank
  private final String message;

  public Rfi(@JsonProperty("rfiId") String rfiId,
             @JsonProperty("appId") String appId,
             @JsonProperty("rfiStatus") RfiStatus rfiStatus,
             @JsonProperty("receivedTimestamp") Long receivedTimestamp,
             @JsonProperty("dueTimestamp") Long dueTimestamp,
             @JsonProperty("sentBy") String sentBy,
             @JsonProperty("message") String message) {
    this.rfiId = rfiId;
    this.appId = appId;
    this.rfiStatus = rfiStatus;
    this.receivedTimestamp = receivedTimestamp;
    this.dueTimestamp = dueTimestamp;
    this.sentBy = sentBy;
    this.message = message;
  }

  public String getRfiId() {
    return rfiId;
  }

  public String getAppId() {
    return appId;
  }

  public RfiStatus getRfiStatus() {
    return rfiStatus;
  }

  public Long getReceivedTimestamp() {
    return receivedTimestamp;
  }

  public Long getDueTimestamp() {
    return dueTimestamp;
  }

  public String getSentBy() {
    return sentBy;
  }

  public String getMessage() {
    return message;
  }

}
