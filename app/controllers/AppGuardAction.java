package controllers;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import components.dao.ApplicationDao;
import components.dao.CaseDetailsDao;
import components.exceptions.UnknownParameterException;
import components.service.UserPermissionService;
import components.service.UserService;
import models.Application;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.mvc.Action;
import play.mvc.Http.Context;
import play.mvc.Result;

import java.util.Optional;
import java.util.concurrent.CompletionStage;

public class AppGuardAction extends Action.Simple {

  private static final Logger LOGGER = LoggerFactory.getLogger(AppGuardAction.class);

  private static final String PATH_START = "/application/";
  private static final String PATTERN_START = "/application/$appId<[^/]+>/";

  private final ApplicationDao applicationDao;
  private final CaseDetailsDao caseDetailsDao;
  private final UserService userService;
  private final UserPermissionService userPermissionService;
  private final boolean ogelOnly;

  @Inject
  public AppGuardAction(ApplicationDao applicationDao, CaseDetailsDao caseDetailsDao, UserService userService,
                        UserPermissionService userPermissionService, @Named("ogelOnly") boolean ogelOnly) {
    this.applicationDao = applicationDao;
    this.caseDetailsDao = caseDetailsDao;
    this.userService = userService;
    this.userPermissionService = userPermissionService;
    this.ogelOnly = ogelOnly;
  }

  @Override
  public CompletionStage<Result> call(Context ctx) {
    if (ogelOnly) {
      throw UnknownParameterException.unknownPath();
    } else {
      String path = ctx.request().path();
      String routePattern = (String) ctx.args.get("ROUTE_PATTERN");
      Optional<String> appIdOptional = getAppId(path, routePattern);
      if (appIdOptional.isPresent()) {
        String appId = appIdOptional.get();
        Application application = applicationDao.getApplication(appId);
        if (application == null) {
          LOGGER.error("Unknown application id {}", appId);
          throw UnknownParameterException.unknownPath();
        } else {
          String currentUserId = userService.getCurrentUserId();
          boolean allowed = userPermissionService.canViewApplication(currentUserId, application);
          if (allowed) {
            boolean hasCase = caseDetailsDao.hasCase(appId);
            if (hasCase) {
              return delegate.call(ctx);
            } else {
              LOGGER.error("Application {} has no case", appId);
              throw UnknownParameterException.unknownPath();
            }
          } else {
            LOGGER.error("User {} has no access to application {}", currentUserId, appId);
            throw UnknownParameterException.unknownPath();
          }
        }
      } else {
        LOGGER.error("Path {} or pattern {} not valid ", path, routePattern);
        throw UnknownParameterException.unknownPath();
      }
    }
  }

  private Optional<String> getAppId(String path, String routePattern) {
    if (StringUtils.startsWith(path, PATH_START) && StringUtils.startsWith(routePattern, PATTERN_START)) {
      String appIdStart = StringUtils.removeStart(path, PATH_START);
      int index = appIdStart.indexOf('/');
      if (index != -1) {
        return Optional.of(appIdStart.substring(0, index));
      }
    }
    return Optional.empty();
  }

}
