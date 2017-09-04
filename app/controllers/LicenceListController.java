package controllers;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import components.cache.SessionCache;
import components.client.CustomerServiceClient;
import components.dao.ApplicationDao;
import components.service.OgelDetailsViewService;
import components.service.OgelRegistrationItemViewService;
import components.service.UserService;
import components.util.EnumUtil;
import components.util.PageUtil;
import components.util.SortUtil;
import models.LicenceListState;
import models.Page;
import models.User;
import models.enums.LicenceListTab;
import models.enums.LicenceSortType;
import models.enums.SortDirection;
import models.view.OgelDetailsView;
import models.view.OgelRegistrationItemView;
import models.view.OgelRegistrationListView;
import play.mvc.Controller;
import play.mvc.Result;
import uk.gov.bis.lite.customer.api.view.CustomerView;
import views.html.licenceDetails;
import views.html.licenceList;
import views.html.ogelDetails;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class LicenceListController extends Controller {

  private final OgelRegistrationItemViewService ogelRegistrationItemViewService;
  private final OgelDetailsViewService ogelDetailsViewService;
  private final UserService userService;
  private final String licenceApplicationAddress;
  private final CustomerServiceClient customerServiceClient;
  private final ApplicationDao applicationDao;

  @Inject
  public LicenceListController(OgelRegistrationItemViewService ogelRegistrationItemViewService,
                               OgelDetailsViewService ogelDetailsViewService,
                               UserService userService,
                               @Named("licenceApplicationAddress") String licenceApplicationAddress,
                               CustomerServiceClient customerServiceClient,
                               ApplicationDao applicationDao) {
    this.ogelRegistrationItemViewService = ogelRegistrationItemViewService;
    this.ogelDetailsViewService = ogelDetailsViewService;
    this.userService = userService;
    this.licenceApplicationAddress = licenceApplicationAddress;
    this.customerServiceClient = customerServiceClient;
    this.applicationDao = applicationDao;
  }

  public Result licenceList(String tab, String sort, String direction, Integer page) {
    User currentUser = userService.getCurrentUser();

    LicenceListState state = SessionCache.getLicenseListState(tab, sort, direction, page);
    SortDirection sortDirection = EnumUtil.parse(state.getDirection(), SortDirection.class, SortDirection.DESC);
    LicenceSortType licenceSortType = EnumUtil.parse(state.getSort(), LicenceSortType.class, LicenceSortType.REFERENCE);
    LicenceListTab licenceListTab = EnumUtil.parse(state.getTab(), LicenceListTab.class, LicenceListTab.OGELS);

    Page<OgelRegistrationItemView> pageData = null;
    if (licenceListTab == LicenceListTab.OGELS) {
      List<OgelRegistrationItemView> ogelRegistrationItemViews;
      // TODO This is a hack for testing. We only show OGELS if there is at least one application.
      if (isShowOgels()) {
        ogelRegistrationItemViews = ogelRegistrationItemViewService.getOgelRegistrationItemViews(currentUser.getId(), licenceSortType, sortDirection);
      } else {
        ogelRegistrationItemViews = new ArrayList<>();
      }
      SortUtil.sort(ogelRegistrationItemViews, licenceSortType, sortDirection);
      pageData = PageUtil.getPage(state.getPage(), ogelRegistrationItemViews);
    }

    OgelRegistrationListView ogelRegistrationListView = new OgelRegistrationListView(licenceSortType, sortDirection, pageData);

    return ok(licenceList.render(licenceApplicationAddress, licenceListTab, ogelRegistrationListView));
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

  private boolean isShowOgels() {
    User currentUser = userService.getCurrentUser();
    List<String> customerViews = customerServiceClient.getCustomers(currentUser.getId()).stream()
        .map(CustomerView::getCustomerId)
        .collect(Collectors.toList());
    return !applicationDao.getApplications(customerViews).isEmpty();
  }

}
