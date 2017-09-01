package controllers;

import com.google.inject.Inject;
import components.dao.RfiResponseDao;
import components.service.ApplicationService;
import components.service.ApplicationSummaryViewService;
import components.service.RfiResponseService;
import components.service.RfiViewService;
import models.view.AddRfiResponseView;
import models.view.ApplicationSummaryView;
import models.view.RfiView;
import models.view.form.RfiResponseForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.rfiListTab;

import java.util.List;

public class RfiTabController extends Controller {

  private static final Logger LOGGER = LoggerFactory.getLogger(RfiTabController.class);

  private final String licenceApplicationAddress;
  private final FormFactory formFactory;
  private final ApplicationSummaryViewService applicationSummaryViewService;
  private final RfiViewService rfiViewService;
  private final RfiResponseDao rfiResponseDao;
  private final ApplicationService applicationService;
  private final RfiResponseService rfiResponseService;

  @Inject
  public RfiTabController(String licenceApplicationAddress,
                          FormFactory formFactory,
                          ApplicationSummaryViewService applicationSummaryViewService,
                          RfiViewService rfiViewService,
                          RfiResponseDao rfiResponseDao,
                          ApplicationService applicationService,
                          RfiResponseService rfiResponseService) {
    this.licenceApplicationAddress = licenceApplicationAddress;
    this.formFactory = formFactory;
    this.applicationSummaryViewService = applicationSummaryViewService;
    this.rfiViewService = rfiViewService;
    this.rfiResponseDao = rfiResponseDao;
    this.applicationService = applicationService;
    this.rfiResponseService = rfiResponseService;
  }

  public Result submitResponse(String appId) {
    Form<RfiResponseForm> rfiResponseForm = formFactory.form(RfiResponseForm.class).bindFromRequest();
    String rfiId = rfiResponseForm.data().get("rfiId");
    if (alreadyHasResponse(rfiId)) {
      LOGGER.error("Response to rfiId {} and appId {} not possible since a response already exists", rfiId, appId);
      return showRfiTab(appId);
    } else if (!allowResponses(appId)) {
      LOGGER.error("Response to rfiId {} and appId {} not possible since application is complete.", rfiId, appId);
      return showRfiTab(appId);
    } else if (rfiResponseForm.hasErrors()) {
      return respond(appId, rfiId, rfiResponseForm);
    } else {
      String responseMessage = rfiResponseForm.get().responseMessage;
      rfiResponseService.insertRfiResponse(rfiId, responseMessage);
      flash("success", "Your message has been sent.");
      return redirect(controllers.routes.RfiTabController.showRfiTab(appId));
    }
  }

  public Result respond(String appId, String rfiId) {
    if (alreadyHasResponse(rfiId)) {
      LOGGER.error("Response to rfiId {} and appId {} not possible since a response already exists", rfiId);
      return showRfiTab(appId);
    } else if (!allowResponses(appId)) {
      LOGGER.error("Response to rfiId {} and appId {} not possible since application is complete.", rfiId, appId);
      return showRfiTab(appId);
    } else {
      RfiResponseForm rfiResponseForm = new RfiResponseForm();
      rfiResponseForm.rfiId = rfiId;
      Form<RfiResponseForm> form = formFactory.form(RfiResponseForm.class).fill(rfiResponseForm);
      return respond(appId, rfiId, form);
    }
  }

  private Result respond(String appId, String rfiId, Form<RfiResponseForm> rfiResponseForm) {
    ApplicationSummaryView applicationSummaryView = applicationSummaryViewService.getApplicationSummaryView(appId);
    List<RfiView> rfiViews = rfiViewService.getRfiViews(appId);
    AddRfiResponseView addRfiResponseView = rfiViewService.getAddRfiResponseView(rfiId);
    return ok(rfiListTab.render(licenceApplicationAddress, applicationSummaryView, rfiViews, allowResponses(appId), rfiResponseForm, addRfiResponseView));
  }

  public Result showRfiTab(String appId) {
    ApplicationSummaryView applicationSummaryView = applicationSummaryViewService.getApplicationSummaryView(appId);
    List<RfiView> rfiViews = rfiViewService.getRfiViews(appId);
    return ok(rfiListTab.render(licenceApplicationAddress, applicationSummaryView, rfiViews, allowResponses(appId), null, null));
  }

  private boolean alreadyHasResponse(String rfiId) {
    return rfiResponseDao.getRfiResponse(rfiId) != null;
  }

  private boolean allowResponses(String appId) {
    return applicationService.isApplicationInProgress(appId);
  }

}