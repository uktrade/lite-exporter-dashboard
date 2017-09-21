package components.service;

import com.google.inject.Inject;
import components.dao.AmendmentDao;
import components.dao.DraftDao;
import components.message.MessagePublisher;
import components.upload.UploadFile;
import components.util.FileUtil;
import components.util.RandomUtil;
import models.enums.DraftType;
import models.enums.RoutingKey;
import uk.gov.bis.lite.exporterdashboard.api.Amendment;
import uk.gov.bis.lite.exporterdashboard.api.File;

import java.time.Instant;
import java.util.List;

public class AmendmentServiceImpl implements AmendmentService {

  private final AmendmentDao amendmentDao;
  private final MessagePublisher messagePublisher;
  private final DraftDao draftDao;

  @Inject
  public AmendmentServiceImpl(AmendmentDao amendmentDao, MessagePublisher messagePublisher, DraftDao draftDao) {
    this.amendmentDao = amendmentDao;
    this.messagePublisher = messagePublisher;
    this.draftDao = draftDao;
  }

  @Override
  public void insertAmendment(String createdByUserId, String appId, String message, List<UploadFile> files) {
    List<File> attachments = getAttachments(appId, files);

    Amendment amendment = new Amendment();
    amendment.setId(RandomUtil.random("AME"));
    amendment.setAppId(appId);
    amendment.setCreatedByUserId(createdByUserId);
    amendment.setCreatedTimestamp(Instant.now().toEpochMilli());
    amendment.setMessage(message);
    amendment.setAttachments(attachments);

    amendmentDao.insertAmendment(amendment);
    draftDao.deleteDraft(appId, DraftType.AMENDMENT);
    messagePublisher.sendMessage(RoutingKey.AMEND_CREATE, amendment);
  }

  private List<File> getAttachments(String appId, List<UploadFile> uploadFiles) {
    List<File> files = FileUtil.toFiles(uploadFiles);
    List<File> draftAttachments = draftDao.getDraftAttachments(appId, DraftType.AMENDMENT);
    files.addAll(draftAttachments);
    return files;
  }

}
