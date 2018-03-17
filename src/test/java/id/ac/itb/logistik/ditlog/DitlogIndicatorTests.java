package id.ac.itb.logistik.ditlog;

import com.fasterxml.jackson.core.JsonProcessingException;
import id.ac.itb.logistik.ditlog.model.Indicator;
import id.ac.itb.logistik.ditlog.model.User;
import id.ac.itb.logistik.ditlog.repository.IndicatorRepository;
import id.ac.itb.logistik.ditlog.service.TokenAuthenticationService;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class DitlogIndicatorTests extends BaseTest {
  @Autowired
  IndicatorRepository indicatorRepository;
  private static User testUser;
  private static String bearerAuth;
  private static Indicator testIndicator;
  private static String jsonIndicator;
  private static boolean setUpIsDone = false;

  @Before
  public void setUp() throws JsonProcessingException {
    if (setUpIsDone) {
      return;
    }
    testIndicator = new Indicator();
    testIndicator.setName("test indicator");
    jsonIndicator = mapper.writeValueAsString(testIndicator);
    testUser = new User("john",422L);
    bearerAuth = TokenAuthenticationService.TOKEN_PREFIX + " " + TokenAuthenticationService.getJWT(testUser);
    setUpIsDone = true;
  }

  @Before
  public void setUpHeader() {
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.set(
            TokenAuthenticationService.HEADER_STRING,
            bearerAuth
    );
  }

  @Test
  public void interactionAdd() throws JSONException {
    HttpEntity<Indicator> entity = new HttpEntity<>(testIndicator, headers);
    ResponseEntity<String> response =
        restTemplate.postForEntity(createURLWithPort("/indicators"), entity, String.class);

    JSONObject result = new JSONObject(response.getBody());

    // Assertion
    Assert.assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
    JSONAssert.assertEquals(jsonIndicator, result.getJSONObject("payload"), false);
  }

  @Test
  public void interactionLoads() throws JSONException {
    HttpEntity<String> entity = new HttpEntity<>(null, headers);
    ResponseEntity<String> response =
        restTemplate.exchange(createURLWithPort("/indicators"), HttpMethod.GET, entity,  String.class);

    JSONObject result = new JSONObject(response.getBody());

    // Assertion
    Assert.assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
    JSONAssert.assertEquals(
        jsonIndicator,
        result.getJSONObject("payload").getJSONArray("content").getJSONObject(0),
        false);
  }
}
