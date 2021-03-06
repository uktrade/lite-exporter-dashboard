package models;

import models.enums.DraftType;

public class DraftFile {

  private final String id;
  private final String filename;
  private final String bucket;
  private final String folder;
  private final Long size;
  private final String relatedId;
  private final DraftType draftType;

  public DraftFile(String id, String filename, String bucket, String folder, Long size, String relatedId, DraftType draftType) {
    this.id = id;
    this.filename = filename;
    this.bucket = bucket;
    this.folder = folder;
    this.size = size;
    this.relatedId = relatedId;
    this.draftType = draftType;
  }

  public String getId() {
    return id;
  }

  public String getFilename() {
    return filename;
  }

  public String getBucket() {
    return bucket;
  }

  public String getFolder() {
    return folder;
  }

  public Long getSize() {
    return size;
  }

  public String getRelatedId() {
    return relatedId;
  }

  public DraftType getDraftType() {
    return draftType;
  }

}
