package mie.ether_example;

//import mie.smartcontracts.Registry;
//import mie.utils.EtherUtils;
import edu.toronto.dbservice.types.EtherAccount;
import edu.toronto.dbservice.types.ShipmentInfo;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;

import java.util.HashMap;
import java.util.concurrent.ExecutionException;

public class RegisterShipmentBlockchainTask implements JavaDelegate {

    @SuppressWarnings("unchecked")
    @Override
    public void execute(DelegateExecution execution) {

        try {
            // Connect to blockchain
            Web3j web3 = Web3j.build(new HttpService());

            // Get process variables
            String contractAddress = (String) execution.getVariable("contractAddress");
            ShipmentInfo shipment = (ShipmentInfo) execution.getVariable("shipmentInfo");
            HashMap<Integer, EtherAccount> accounts = (HashMap<Integer, EtherAccount>) execution.getVariable("accounts");

            // Use the farmer's Ether account (can be modified if needed)
            EtherAccount farmerAccount = accounts.get(shipment.getFarmerAccount());
            if (farmerAccount == null) {
                throw new RuntimeException("No EtherAccount found for farmer account: " + shipment.getFarmerAccount());
            }

            // Load the Registry contract
            Registry registry = Registry.load(
                contractAddress,
                web3,
                farmerAccount.getCredentials(),
                EtherUtils.GAS_PRICE,
                EtherUtils.GAS_LIMIT_CONTRACT_TX
            );

            // Create the shipment data string
            String shipmentData = String.format(
                "shipmentId=%d|farmerAccount=%d|carrierAccount=%d|status=%s|warehouseId=%s|rackLocation=%s",
                shipment.getShipmentId(),
                shipment.getFarmerAccount(),
                shipment.getCarrierAccount(),
                shipment.getStatus(),
                shipment.getWarehouseId() != null ? shipment.getWarehouseId().toString() : "null",
                shipment.getRackLocation() != null ? shipment.getRackLocation() : "null"
            );

            System.out.println("Registering shipment on blockchain: " + shipmentData);

            // Call contract's register method
            Utf8String shipmentKey = new Utf8String(shipmentData);
            TransactionReceipt receipt = registry.register(shipmentKey).get();

            // Report transaction
            EtherUtils.reportTransaction("Shipment registered on blockchain", receipt);

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to register shipment on blockchain", e);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Unexpected error during shipment registration", e);
        }
    }
}
