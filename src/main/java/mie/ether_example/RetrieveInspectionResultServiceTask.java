package mie.ether_example;

import edu.toronto.dbservice.config.MIE354DBHelper;
import edu.toronto.dbservice.types.ShipmentInfo;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class RetrieveInspectionResultServiceTask implements JavaDelegate {

    Connection dbCon = null;

    public RetrieveInspectionResultServiceTask() {
        dbCon = MIE354DBHelper.getDBConnection();
    }

    @Override
    public void execute(DelegateExecution execution) {
        ShipmentInfo shipment = (ShipmentInfo) execution.getVariable("shipmentInfo");
        int shipmentId = shipment.getShipmentId();

        try {
            String query = "SELECT result, timeStamp FROM Inspections WHERE shipmentId = ?";
            PreparedStatement stmt = dbCon.prepareStatement(query);
            stmt.setInt(1, shipmentId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String result = rs.getString("result");
                String timeStamp = rs.getString("timeStamp");

                System.out.println("Inspection result for shipment " + shipmentId + ": " + result + " at " + timeStamp);

                execution.setVariable("inspectionResult", result);
                execution.setVariable("inspectionTime", timeStamp);
            } else {
                System.out.println("No inspection result found for shipment " + shipmentId);
                execution.setVariable("inspectionResult", "Unknown");
                execution.setVariable("inspectionTime", null);
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to retrieve inspection result", e);
        }
    }
}
