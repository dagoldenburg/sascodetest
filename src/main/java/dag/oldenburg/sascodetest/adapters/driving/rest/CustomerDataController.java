package dag.oldenburg.sascodetest.adapters.driving.rest;

import dag.oldenburg.sascodetest.core.CustomerData;
import dag.oldenburg.sascodetest.core.CustomerDataService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CustomerDataController {

  private final CustomerDataService customerDataService;

  public CustomerDataController(CustomerDataService customerDataService) {
    this.customerDataService = customerDataService;
  }

  @GetMapping("/customer-data/{customerId}")
  public ResponseEntity<CustomerData> getCustomerData(@PathVariable("customerId") String customerId) {
    CustomerData customerData = customerDataService.getCustomerData(customerId);
    if (customerData.isMissingAccountInformation()) {
      return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT).body(customerData);
    } else {
      return ResponseEntity.ok(customerData);
    }
  }

}
