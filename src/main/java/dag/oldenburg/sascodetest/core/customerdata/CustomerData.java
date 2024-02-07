package dag.oldenburg.sascodetest.core.customerdata;

import com.fasterxml.jackson.annotation.JsonIgnore;
import generated.Customer;
import generated.CustomerAccount;
import java.math.BigDecimal;

public record   CustomerData(String customerId, String name, String emailAddress, String accountId, String accountType, BigDecimal balance) {

  public static CustomerData from(Customer customer, CustomerAccount customerAccount) {
    return new CustomerData(customer.getCustomerId(), customer.getName(), customer.getEmail(), customerAccount.getAccountId(),
        customerAccount.getAccountType(), customerAccount.getBalance());
  }

  @JsonIgnore
  public boolean isMissingAccountInformation() {
    return accountId == null && accountType == null && balance == null;
  }
}
