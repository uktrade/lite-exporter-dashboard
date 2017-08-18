package controllers;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import components.service.CacheService;
import components.service.OgelDetailsViewService;
import components.service.OgelRegistrationItemViewService;
import components.service.PageService;
import components.service.SortDirectionService;
import components.service.UserService;
import models.LicenseListState;
import models.Page;
import models.User;
import models.enums.SortDirection;
import models.view.OgelDetailsView;
import models.view.OgelRegistrationItemView;
import models.view.OgelRegistrationListView;
import play.mvc.Controller;
import play.mvc.Result;
import scala.Option;
import views.html.licenceDetails;
import views.html.licenceList;
import views.html.ogelDetails;

import java.util.List;

public class LicenseListController extends Controller {

  private final CacheService cacheService;
  private final OgelRegistrationItemViewService ogelRegistrationItemViewService;
  private final SortDirectionService sortDirectionService;
  private final PageService pageService;
  private final OgelDetailsViewService ogelDetailsViewService;
  private final String licenceApplicationAddress;
  private final UserService userService;

  @Inject
  public LicenseListController(CacheService cacheService,
                               OgelRegistrationItemViewService ogelRegistrationItemViewService,
                               SortDirectionService sortDirectionService,
                               PageService pageService, OgelDetailsViewService ogelDetailsViewService,
                               @Named("licenceApplicationAddress") String licenceApplicationAddress,
                               UserService userService) {
    this.cacheService = cacheService;
    this.ogelRegistrationItemViewService = ogelRegistrationItemViewService;
    this.sortDirectionService = sortDirectionService;
    this.pageService = pageService;
    this.ogelDetailsViewService = ogelDetailsViewService;
    this.licenceApplicationAddress = licenceApplicationAddress;
    this.userService = userService;
  }


  public Result licenceList(Option<String> tab, Option<String> reference, Option<String> licensee, Option<String> site, Option<String> date, Option<Integer> page) {
    User currentUser = userService.getCurrentUser();

    LicenseListState state = cacheService.getLicenseListState(tab, reference, licensee, site, date, page);

    SortDirection referenceSortDirection = sortDirectionService.parse(state.getReference());
    SortDirection licenseeSortDirection = sortDirectionService.parse(state.getLicensee());
    SortDirection siteSortDirection = sortDirectionService.parse(state.getSite());
    SortDirection dateSortDirection = sortDirectionService.parse(state.getDate());

    long definedCount = sortDirectionService.definedCount(referenceSortDirection, licenseeSortDirection, siteSortDirection, dateSortDirection);
    if (definedCount != 1) {
      referenceSortDirection = SortDirection.DESC;
      licenseeSortDirection = null;
      siteSortDirection = null;
      dateSortDirection = null;
    }

    String activeTab = "siels".equals(state.getTab()) ? "siels" : "ogels";

    Page<OgelRegistrationItemView> pageData = null;
    if (activeTab.equals("ogels")) {
      List<OgelRegistrationItemView> ogelRegistrationItemViews = ogelRegistrationItemViewService.getOgelRegistrationItemViews(currentUser.getId(), referenceSortDirection, licenseeSortDirection, siteSortDirection, dateSortDirection);
      pageData = pageService.getPage(state.getPage(), ogelRegistrationItemViews);
    }
    OgelRegistrationListView ogelRegistrationListView = new OgelRegistrationListView(pageData,
        activeTab,
        sortDirectionService.toParam(referenceSortDirection),
        sortDirectionService.toParam(licenseeSortDirection),
        sortDirectionService.toParam(siteSortDirection),
        sortDirectionService.toParam(dateSortDirection));
    return ok(licenceList.render(licenceApplicationAddress, activeTab, ogelRegistrationListView));
  }

  public Result licenceDetails(String licenceRef) {
    User currentUser = userService.getCurrentUser();
    if (licenceRef != null && licenceRef.startsWith("GBSIE")) {
      return ok(licenceDetails.render(licenceApplicationAddress, licenceRef));
    } else {
      OgelDetailsView ogelDetailsView = ogelDetailsViewService.getOgelDetailsView(currentUser.getId(), licenceRef);
      return ok(ogelDetails.render(licenceApplicationAddress, ogelDetailsView));
    }
  }

}
