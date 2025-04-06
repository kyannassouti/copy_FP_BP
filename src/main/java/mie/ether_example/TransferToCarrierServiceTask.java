package mie.ether_example;

//import mie.smartcontracts.Registry;
//import mie.utils.EtherUtils;
import edu.toronto.dbservice.types.EtherAccount;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.Utf8String;
import edu.toronto.dbservice.types.ShipmentInfo;


import java.util.HashMap;
import java.util.concurrent.ExecutionException;

public class TransferToCarrierServiceTask implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) {

        try {
            Web3j web3 = Web3j.build(new HttpService());
            String contractAddress = (String) execution.getVariable("contractAddress");
            ShipmentInfo shipment = (ShipmentInfo) execution.getVariable("shipmentInfo");
            HashMap<Integer, EtherAccount> accounts = (HashMap<Integer, EtherAccount>) execution.getVariable("accounts");

            // Sender: Farmer
            EtherAccount farmerAccount = accounts.get(shipment.getFarmerAccount());
            if (farmerAccount == null) throw new RuntimeException("No farmer account found");

            // Receiver: Carrier
            EtherAccount carrierAccount = accounts.get(shipment.getCarrierAccount());
            if (carrierAccount == null) throw new RuntimeException("No carrier account found");
            String carrierAddress = carrierAccount.getCredentials().getAddress();

            // Load contract using farmer credentials
            Registry registry = Registry.load(contractAddress, web3, farmerAccount.getCredentials(), EtherUtils.GAS_PRICE, EtherUtils.GAS_LIMIT_CONTRACT_TX);

            // Build shipment key string
            String shipmentKeyStr = String.format(
                "shipmentId=%d|farmerAccount=%d|carrierAccount=%d|status=%s|warehouseId=%s|rackLocation=%s",
                shipment.getShipmentId(), shipment.getFarmerAccount(), shipment.getCarrierAccount(),
                shipment.getStatus(),
                shipment.getWarehouseId() != null ? shipment.getWarehouseId().toString() : "null",
                shipment.getRackLocation() != null ? shipment.getRackLocation() : "null"
            );

            Utf8String shipmentKey = new Utf8String(shipmentKeyStr);
            Address newOwner = new Address(carrierAddress);

            System.out.println("Transferring shipment to carrier: " + carrierAddress);
            TransactionReceipt receipt = registry.transfer(shipmentKey, newOwner).get();

            Address updatedOwner = registry.getOwner(shipmentKey).get();
            execution.setVariable("currentOwnerAddress", updatedOwner.toString());

            EtherUtils.reportTransaction("Transferred ownership to carrier", receipt);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error during transfer to carrier", e);
        }
    }
}
