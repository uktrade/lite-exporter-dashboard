package pact.consumer;

import static org.assertj.core.api.Assertions.assertThat;

import au.com.dius.pact.consumer.Pact;
import au.com.dius.pact.consumer.PactProviderRule;
import au.com.dius.pact.consumer.PactVerification;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.dsl.PactDslJsonRootValue;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.model.PactFragment;
import com.google.common.collect.ImmutableMap;
import components.client.OgelServiceClient;
import components.client.OgelServiceClientImpl;
import components.exceptions.ServiceException;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import play.libs.concurrent.HttpExecutionContext;
import play.libs.ws.WS;
import play.libs.ws.WSClient;
import uk.gov.bis.lite.ogel.api.view.OgelFullView;
import uk.gov.bis.lite.ogel.api.view.OgelFullView.OgelConditionSummary;

import java.util.HashMap;
import java.util.Map;

public class OgelServiceConsumerPact {

  private static final String PROVIDER = "lite-ogel-service";
  private static final String CONSUMER = "lite-exporter-dashboard";

  private OgelServiceClient client;
  private WSClient ws;

  // service:password
  private static final Map<String, String> AUTH_HEADERS = ImmutableMap.of("Authorization", "Basic c2VydmljZTpwYXNzd29yZA==");
  private static final Map<String, String> CONTENT_TYPE_HEADERS = ImmutableMap.of("Content-Type", "application/json");

  private static final String OGEL_ID = "OGL1";
  private static final String OGEL_NAME = "name";
  private static final String OGEL_LINK = "http://example.org";
  private static final String OGEL_CAN = "can";
  private static final String OGEL_CANT = "can't";
  private static final String OGEL_MUST = "must";
  private static final String OGEL_HOW_TO_USE = "how to use";

  @Rule
  public PactProviderRule mockProvider = new PactProviderRule(PROVIDER, this);

  @Before
  public void setUp() throws Exception {
    ws = WS.newClient(mockProvider.getConfig().getPort());
    client = new OgelServiceClientImpl(new HttpExecutionContext(Runnable::run),
        ws,
        mockProvider.getConfig().url(),
        10000,
        "service:password");
  }

  @After
  public void tearDown() throws Exception {
    ws.close();
  }

  @Pact(provider = PROVIDER, consumer = CONSUMER)
  public PactFragment ogelExists(PactDslWithProvider builder) {
    PactDslJsonBody body = new PactDslJsonBody()
        .stringType("id", OGEL_ID)
        .stringType("name", OGEL_NAME)
        .stringType("link", OGEL_LINK)
        .object("summary")
          .minArrayLike("canList", 0, PactDslJsonRootValue.stringType(OGEL_CAN),3)
          .minArrayLike("cantList", 0, PactDslJsonRootValue.stringType(OGEL_CANT), 3)
          .minArrayLike("mustList", 0, PactDslJsonRootValue.stringType(OGEL_MUST), 3)
          .minArrayLike("howToUseList", 0, PactDslJsonRootValue.stringType(OGEL_HOW_TO_USE), 3)
        .closeObject()
        .asBody();

    return builder
        .given("provided OGEL exists")
        .uponReceiving("a request for a given ogel id")
          .headers(AUTH_HEADERS)
          .path("/ogels/" + OGEL_ID)
          .method("GET")
        .willRespondWith()
          .status(200)
          .headers(CONTENT_TYPE_HEADERS)
          .body(body)
        .toFragment();
  }

  @Pact(provider = PROVIDER, consumer = CONSUMER)
  public PactFragment ogelDoesNotExist(PactDslWithProvider builder) {
    PactDslJsonBody body = new PactDslJsonBody()
        .integerType("code", 404)
        .stringType("message", "No Ogel Found With Given Ogel ID: " + OGEL_ID)
        .asBody();

    return builder
        .given("provided OGEL does not exist")
        .uponReceiving("a request for a given ogel id")
          .headers(AUTH_HEADERS)
          .path("/ogels/" + OGEL_ID)
          .method("GET")
        .willRespondWith()
          .status(404)
          .headers(CONTENT_TYPE_HEADERS)
          .body(body)
        .toFragment();
  }

  @Test
  @PactVerification(value = PROVIDER, fragment = "ogelExists")
  public void ogelExistsTest() throws Exception {
    OgelFullView result = client.getOgel(OGEL_ID);
    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(OGEL_ID);
    assertThat(result.getName()).isEqualTo(OGEL_NAME);
    assertThat(result.getLink()).isEqualTo(OGEL_LINK);
    OgelConditionSummary summary = result.getSummary();
    assertThat(summary).isNotNull();
    assertThat(summary.getCanList().size()).isEqualTo(3);
    assertThat(summary.getCanList().get(0)).isEqualTo(OGEL_CAN);
    assertThat(summary.getCantList().size()).isEqualTo(3);
    assertThat(summary.getCantList().get(0)).isEqualTo(OGEL_CANT);
    assertThat(summary.getMustList().size()).isEqualTo(3);
    assertThat(summary.getMustList().get(0)).isEqualTo(OGEL_MUST);
    assertThat(summary.getHowToUseList().size()).isEqualTo(3);
    assertThat(summary.getHowToUseList().get(0)).isEqualTo(OGEL_HOW_TO_USE);
  }

  @Test
  @PactVerification(value = PROVIDER, fragment = "ogelDoesNotExist")
  public void ogelDoesNotExistTest() throws Exception {
    OgelFullView result = null;
    try {
      result = client.getOgel(OGEL_ID);
    }
    catch (Exception exception) {
      assertThat(exception).isInstanceOf(ServiceException.class);
    }
    assertThat(result).isNull();
  }
}

