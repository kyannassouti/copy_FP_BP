package mie.ether_example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import java.util.Map;

import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;

import edu.toronto.dbservice.config.MIE354DBHelper;
import edu.toronto.dbservice.types.EtherAccount;
import edu.toronto.dbservice.types.ShipmentInfo;


import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;

public class InvoiceRegistration implements JavaDelegate {

    Connection dbCon = null;

    public InvoiceRegistration() {
        dbCon = MIE354DBHelper.getDBConnection();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void execute(DelegateExecution execution) {
        // Retrieve the invoice from the current element of the collection
        Map<String, Object> invoice = (Map<String, Object>) execution.getVariable("invoice");

        // Extract relevant details from the invoice
        Integer shipmentId = (Integer) invoice.get("shipmentId");
        String role = (String) invoice.get("role");
        int accountId = (int) invoice.get("account");

     
        Web3j web3 = Web3j.build(new HttpService());
        String contractAddress = (String) execution.getVariable("contractAddress");
        HashMap<Integer, EtherAccount> accounts = (HashMap<Integer, EtherAccount>) execution.getVariable("accounts");

        // Check if account exists in the accounts map
        if (!accounts.containsKey(accountId)) {
            throw new RuntimeException("No account found for role: " + role + ", accountId: " + accountId);
        }

        try {
            // Register the invoice on the blockchain
            registerInvoice(execution, web3, contractAddress, accounts, role, invoice, accountId);
        } catch (Exception e) {
            throw new RuntimeException("Error during invoice blockchain registration: " + e.getMessage(), e);
        }
    }

    private void registerInvoice(DelegateExecution execution, Web3j web3, String contractAddress,
                                 HashMap<Integer, EtherAccount> accounts, String role, Map<String, Object> invoiceDetails,
                                 int accountId) throws Exception {

        // Construct the invoice key for blockchain registration
        String itemKey = "INV-" + role + "-" + invoiceDetails.get("shipmentId");

        // Load the registry contract
        Registry registry = Registry.load(
            contractAddress,
            web3,
            accounts.get(accountId).getCredentials(),
            EtherUtils.GAS_PRICE,
            EtherUtils.GAS_LIMIT_CONTRACT_TX
        );

        // Encode the item key and register the invoice on the blockchain
        Utf8String encodedKey = new Utf8String(itemKey);
        TransactionReceipt receipt = registry.register(encodedKey).get();
        EtherUtils.reportTransaction("Invoice registered: " + itemKey, receipt);

        // Log the registration in the database (same as before)
        Address owner = registry.getOwner(encodedKey).get();
        Uint256 time = registry.getTime(encodedKey).get();
        String strOwner = owner.toString();
        Date decodedTime = new Date(time.getValue().longValue() * 1000L);
        SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String strTime = fmt.format(decodedTime);

    }

}
