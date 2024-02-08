package dag.oldenburg.sascodetest.core.customerdata;

import dag.oldenburg.sascodetest.core.NotFoundException;
import generated.Customer;
import generated.CustomerAccount;
import generated.GetCustomer;
import generated.GetCustomerAccount;
import java.util.concurrent.StructuredTaskScope;
import java.util.concurrent.StructuredTaskScope.Subtask;
import java.util.concurrent.StructuredTaskScope.Subtask.State;
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

    try (StructuredTaskScope scope = new StructuredTaskScope.ShutdownOnFailure()) {
      Subtask<Customer> customerSubtask = scope.fork(() -> webServiceTemplate.marshalSendAndReceive(getCustomer));
      Subtask<CustomerAccount> customerAccountSubtask = scope.fork(() -> webServiceTemplate.marshalSendAndReceive(getCustomerAccount));

      scope.join();

      State customerState = customerSubtask.state();
      State customerAccountState = customerAccountSubtask.state();
      
      if (customerState == State.SUCCESS && customerAccountState == State.SUCCESS) {
        return CustomerData.from(customerSubtask.get(),  customerAccountSubtask.get());
      } else if (customerState == State.SUCCESS && !(customerAccountState == State.SUCCESS)) {
        return CustomerData.from(customerSubtask.get(),  new CustomerAccount());
      } else {
        throw new NotFoundException(customerSubtask.exception().getMessage());
      }
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

}