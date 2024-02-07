package dag.oldenburg.sascodetest.adapters.driving.rest;


import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import dag.oldenburg.sascodetest.core.customerdata.CustomerData;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class CustomerDataControllerTest {

  @RegisterExtension
  static WireMockExtension wireMock = WireMockExtension.newInstance()
      .options(WireMockConfiguration.wireMockConfig().dynamicPort()
          .usingFilesUnderDirectory("wiremock"))
      .build();

  @Autowired
  private TestRestTemplate testRestTemplate;

  @LocalServerPort
  private int port;

  private String customerDataEndpoint;

  @DynamicPropertySource
  static void wireMockProperties(DynamicPropertyRegistry registry) {
    registry.add("mock-soap-service.url", () -> "http://localhost:" + wireMock.getPort() + "/mock-soap-service");
  }

  @BeforeEach
  void setUp() {
    customerDataEndpoint = "http://localhost:%d/customer-data/".formatted(port);
  }

  @Test
  public void givenThatCustomerAndCustomerAccountExists_whenCallingGetCustomerData_thenReturnCombinedCustomerData() {
    ResponseEntity<CustomerData> response = getCustomerDataForId("1");
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(new CustomerData("1", "Dag Oldenburg", "dagoldenburg@test.email", "1", "TestAccount", new BigDecimal("100.0")),
        response.getBody());
  }

  @Test
  public void givenThatCustomerButNoCustomerAccountExists_whenCallingGetCustomerData_thenReturnPartialResponse() {
    ResponseEntity<CustomerData> response = getCustomerDataForId("2");
    assertEquals(HttpStatus.PARTIAL_CONTENT, response.getStatusCode());
    assertEquals(new CustomerData("2", "Test Testsson", "test@test.email", null, null, null),
        response.getBody());
  }

  @Test
  public void givenThatNoCustomerOrCustomerAccountExists_whenCallingGetCustomerData_thenReturnNotFound() {
    ResponseEntity<CustomerData> response = getCustomerDataForId("3");
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }

  private ResponseEntity<CustomerData> getCustomerDataForId(String customerId) {
    return testRestTemplate.getForEntity(customerDataEndpoint + customerId, CustomerData.class);
  }
}