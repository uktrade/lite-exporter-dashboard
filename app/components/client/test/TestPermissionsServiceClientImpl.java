package components.client.test;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import components.client.PermissionsServiceClient;
import components.client.PermissionsServiceClientImpl;
import components.service.UserService;
import components.service.test.TestDataServiceImpl;
import play.libs.concurrent.HttpExecutionContext;
import play.libs.ws.WSClient;
import uk.gov.bis.lite.permissions.api.view.OgelRegistrationView;

import java.util.ArrayList;
import java.util.List;

public class TestPermissionsServiceClientImpl implements PermissionsServiceClient {

  private final PermissionsServiceClientImpl permissionsServiceClientImpl;
  private final UserService userService;

  @Inject
  public TestPermissionsServiceClientImpl(HttpExecutionContext httpExecutionContext,
                                          WSClient wsClient,
                                          @Named("permissionsServiceAddress") String address,
                                          @Named("permissionsServiceTimeout") int timeout,
                                          UserService userService) {
    this.permissionsServiceClientImpl = new PermissionsServiceClientImpl(httpExecutionContext, wsClient, address, timeout);
    this.userService = userService;
  }

  // Siel Ogel
  // Admin: N N
  // Applicant11: Y N
  // Applicant2: Y Y
  // Applicant3: N Y
  @Override
  public List<OgelRegistrationView> getOgelRegistrations(String userId) {
    if ("1".equals(userId) || TestDataServiceImpl.APPLICANT_ID.equals(userId)) {
      return new ArrayList<>();
    } else {
      List<OgelRegistrationView> ogelRegistrationViews = permissionsServiceClientImpl.getOgelRegistrations(TestDataServiceImpl.APPLICANT_ID);
      ogelRegistrationViews.forEach(ogelRegistrationView -> {
        String wrapCustomerId = TestDataServiceImpl.wrapCustomerId(userService.getCurrentUserId(), ogelRegistrationView.getCustomerId());
        ogelRegistrationView.setCustomerId(wrapCustomerId);
      });
      return ogelRegistrationViews;
    }
  }

}
