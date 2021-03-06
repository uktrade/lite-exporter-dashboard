package components.dao;

import components.common.upload.UploadResult;
import models.Attachment;
import models.DraftFile;
import models.enums.DraftType;

import java.util.List;

public interface DraftFileDao {

  List<Attachment> getAttachments(String relatedId, DraftType draftType);

  DraftFile getDraftFile(String id);

  void deleteDraftFiles(String relatedId, DraftType draftType);

  void addDraftFile(UploadResult uploadResult, String relatedId, DraftType draftType);

  void deleteDraftFile(String fileId, String relatedId, DraftType draftType);

  void deleteAllDraftFiles();

}
