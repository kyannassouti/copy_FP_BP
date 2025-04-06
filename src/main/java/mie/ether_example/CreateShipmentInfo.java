package mie.ether_example;

import java.util.Map;

import edu.toronto.dbservice.types.ShipmentInfo;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;

public class CreateShipmentInfo implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) {

        @SuppressWarnings("unchecked")
        Map<String, Object> bid = (Map<String, Object>) execution.getVariable("winningBid");

        Integer shipmentId = (Integer) bid.get("shipmentId");
        Integer farmerAccount = (Integer) bid.get("bidderAccount"); // bidder is farmer
        Integer carrierAccount = 9;
        String status = "In Transit";
        Integer warehouseId = null;
        String rackLocation = null;

        ShipmentInfo shipment = new ShipmentInfo(
            shipmentId,
            farmerAccount,
            carrierAccount,
            status,
            warehouseId,
            rackLocation
        );

        execution.setVariable("shipmentInfo", shipment);
    }
}
