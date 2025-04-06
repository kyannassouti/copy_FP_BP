package mie.ether_example;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;

import java.util.Arrays;
import java.util.List;

public class InitializeInvoiceRoles implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) {
        List<String> roles = Arrays.asList("Farmer", "Carrier", "Inspector");
        execution.setVariable("roles", roles);
    }
}
