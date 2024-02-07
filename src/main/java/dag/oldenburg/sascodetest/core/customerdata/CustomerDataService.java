package dag.oldenburg.sascodetest.core.customerdata;

import dag.oldenburg.sascodetest.core.NotFoundException;
import generated.Customer;
import generated.CustomerAccount;
import generated.GetCustomer;
import generated.GetCustomerAccount;
import java.util.concurrent.CompletableFuture;
import org.springframework.stereotype.Service;
import org.springframework.ws.client.core.WebServiceTemplate;

@Service
public class CustomerDataService {

  private final WebServiceTemplate webServiceTemplate;

  public CustomerDataService(WebServiceTemplate webServiceTemplate) {
    this.webServiceTemplate = webServiceTemplate;
  }

  private static CustomerAccount handleCustomerAccountExceptions(Throwable throwable) {
    if (throwable.getMessage().contains("Not Found [404]")) {
      return new CustomerAccount();
    } else {
      throw new RuntimeException(throwable);
    }
  }

  private static Customer handleCustomerExceptions(Throwable throwable) {
    if (throwable.getMessage().contains("Not Found [404]")) {
      throw new NotFoundException(throwable.getMessage());
    } else {
      throw new RuntimeException(throwable);
    }
  }

  public CustomerData getCustomerData(String customerId) {
    GetCustomer getCustomer = new GetCustomer();
    GetCustomerAccount getCustomerAccount = new GetCustomerAccount();
    getCustomer.setCustomerId(customerId);
    getCustomerAccount.setCustomerId(customerId);

    CompletableFuture<Customer> customerFuture = CompletableFuture.supplyAsync(
            () -> (Customer) webServiceTemplate.marshalSendAndReceive(getCustomer))
        .exceptionally(CustomerDataService::handleCustomerExceptions);

    CompletableFuture<CustomerAccount> customerAccountFuture = CompletableFuture.supplyAsync(
            () -> (CustomerAccount) webServiceTemplate.marshalSendAndReceive(getCustomerAccount))
        .exceptionally(CustomerDataService::handleCustomerAccountExceptions);

    return customerFuture.thenCombine(customerAccountFuture, CustomerData::from)
        .join();
  }

}