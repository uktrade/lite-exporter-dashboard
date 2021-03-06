package controllers;

import static java.util.concurrent.CompletableFuture.completedFuture;

import com.google.inject.Inject;
import components.common.upload.FileService;
import components.common.upload.FileUploadResponse;
import components.common.upload.FileUploadResponseItem;
import components.common.upload.FileUtil;
import components.common.upload.UploadMultipartParser;
import components.common.upload.UploadResult;
import components.dao.DraftFileDao;
import components.service.AppDataService;
import components.service.DraftFileService;
import components.service.UserPermissionService;
import components.service.UserService;
import components.util.EnumUtil;
import models.AppData;
import models.DraftFile;
import models.enums.DraftType;
import play.libs.Json;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.BodyParser;
import play.mvc.Result;
import play.mvc.With;

import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

@With(AppGuardAction.class)
public class UploadController extends SamlController {

  private final AppDataService appDataService;
  private final UserService userService;
  private final DraftFileDao draftFileDao;
  private final UserPermissionService userPermissionService;
  private final FileService fileService;
  private final DraftFileService draftFileService;
  private final HttpExecutionContext context;

  @Inject
  public UploadController(AppDataService appDataService,
                          UserService userService,
                          DraftFileDao draftFileDao,
                          UserPermissionService userPermissionService,
                          FileService fileService,
                          DraftFileService draftFileService,
                          HttpExecutionContext httpExecutionContext1) {
    this.appDataService = appDataService;
    this.userService = userService;
    this.draftFileDao = draftFileDao;
    this.userPermissionService = userPermissionService;
    this.fileService = fileService;
    this.draftFileService = draftFileService;
    this.context = httpExecutionContext1;
  }

  @BodyParser.Of(UploadMultipartParser.class)
  public CompletionStage<Result> submitFiles(String appId, String draftTypeStr, String relatedId) {
    String userId = userService.getCurrentUserId();
    DraftType draftType = EnumUtil.parse(draftTypeStr, DraftType.class, null);
    if (draftType == null) {
      return completedFuture(badRequest("Unknown draftType " + draftTypeStr));
    } else if (!canAddOrDeleteFile(userId, appId, draftType, relatedId)) {
      return completedFuture(notFound("Unknown relatedId " + relatedId));
    } else {
      return fileService.processUpload(appId, request())
          .thenApplyAsync(uploadResults -> createFileUploadResponse(uploadResults, appId, relatedId, draftType), context.current())
          .thenApply(fileUploadResponse -> ok(Json.toJson(fileUploadResponse)));
    }
  }

  public Result deleteFile(String appId, String fileId) {
    String userId = userService.getCurrentUserId();
    DraftFile draftFile = draftFileDao.getDraftFile(fileId);
    if (draftFile == null || !canAddOrDeleteFile(userId, appId, draftFile.getDraftType(), draftFile.getRelatedId())) {
      return notFound("Unknown draft file id " + fileId);
    } else {
      draftFileService.deleteDraftFile(draftFile.getId(), draftFile.getRelatedId(), draftFile.getDraftType());
      return ok();
    }
  }

  private boolean canAddOrDeleteFile(String userId, String appId, DraftType draftType, String relatedId) {
    AppData appData = appDataService.getAppData(appId);
    if (draftType == DraftType.AMENDMENT_OR_WITHDRAWAL) {
      return appId.equals(relatedId) && userPermissionService.canAddAmendmentOrWithdrawalRequest(userId, appData);
    } else if (draftType == DraftType.RFI_REPLY) {
      return userPermissionService.canAddRfiReply(userId, relatedId, appData);
    } else {
      return false;
    }
  }

  private FileUploadResponse createFileUploadResponse(List<UploadResult> uploadResults, String appId, String relatedId, DraftType draftType) {
    List<FileUploadResponseItem> fileUploadResponseItems = uploadResults.stream()
        .map(uploadResult -> createFileUploadResponseItem(uploadResult, appId, relatedId, draftType))
        .collect(Collectors.toList());
    return new FileUploadResponse(fileUploadResponseItems);
  }

  private FileUploadResponseItem createFileUploadResponseItem(UploadResult uploadResult, String appId, String relatedId, DraftType draftType) {
    if (uploadResult.isValid()) {
      draftFileDao.addDraftFile(uploadResult, relatedId, draftType);
      String link = getLink(appId, relatedId, uploadResult.getId(), draftType);
      String size = FileUtil.getReadableFileSize(uploadResult.getSize());
      String jsDeleteLink = routes.UploadController.deleteFile(appId, uploadResult.getId()).toString();
      return new FileUploadResponseItem(uploadResult.getFilename(), link, size, jsDeleteLink, null);
    } else {
      return new FileUploadResponseItem(uploadResult.getFilename(), null, null, null, uploadResult.getError());
    }
  }

  private String getLink(String appId, String relatedId, String fileId, DraftType draftType) {
    if (draftType == DraftType.RFI_REPLY) {
      return routes.DownloadController.getRfiReplyAttachment(appId, relatedId, fileId).toString();
    } else {
      return routes.DownloadController.getAmendmentOrWithdrawalAttachment(relatedId, fileId).toString();
    }
  }

}
