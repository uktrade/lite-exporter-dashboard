package components.service.test;

import com.google.inject.Inject;
import components.common.auth.AuthInfo;
import components.common.auth.SpireAuthManager;
import components.exceptions.ServiceException;
import components.service.UserService;
import models.User;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class TestUserServiceImpl implements UserService {

  private static final Map<String, User> USERS = new HashMap<>();
  private final SpireAuthManager spireAuthManager;

  @Inject
  public TestUserServiceImpl(SpireAuthManager spireAuthManager) {
    this.spireAuthManager = spireAuthManager;
  }

  static {
    USERS.put(TestDataServiceImpl.OFFICER_ID, new User(TestDataServiceImpl.OFFICER_ID,
        "Jerry",
        "McGuire",
        "j.mcguire@trade.gov.uk",
        "01234 567890"));
    USERS.put(TestDataServiceImpl.OTHER_OFFICER_ID, new User(TestDataServiceImpl.OTHER_OFFICER_ID,
        "Tom",
        "Baker",
        "t.baker@trade.gov.uk",
        "+44987654321"));
  }

  @Override
  public String getUsername(String userId) {
    User user = getUser(userId);
    return user.getFirstName() + " " + user.getLastName();
  }

  @Override
  public User getUser(String userId) {
    if (StringUtils.isBlank(userId)) {
      throw new ServiceException("UserId is blank. Unable to get user.");
    } else {
      AuthInfo authInfo = spireAuthManager.getAuthInfoFromContext();
      if (authInfo.isAuthenticated() && userId.equals(authInfo.getId())) {
        return new User(authInfo.getId(), authInfo.getForename(), authInfo.getSurname(), authInfo.getEmail(), null);
      } else {
        User user = USERS.get(userId);
        if (user != null) {
          return user;
        } else {
          return new User(userId, "test", "user" + userId, "testuser" + userId + "@test.com", "+44" + userId);
        }
      }
    }
  }

  @Override
  public String getCurrentUserId() {
    AuthInfo authInfo = spireAuthManager.getAuthInfoFromContext();
    if (!authInfo.isAuthenticated()) {
      throw new ServiceException("No user logged in. Unable to get current userId.");
    } else {
      return authInfo.getId();
    }
  }

}
