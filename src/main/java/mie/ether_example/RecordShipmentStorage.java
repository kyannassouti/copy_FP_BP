package mie.ether_example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import edu.toronto.dbservice.config.MIE354DBHelper;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;

public class RecordShipmentStorage implements JavaDelegate {

    private Connection dbCon;

    public RecordShipmentStorage() {
        dbCon = MIE354DBHelper.getDBConnection();
    }

    @Override
    public void execute(DelegateExecution execution) {
        Integer shipmentId = (Integer) execution.getVariable("shipmentId");
        Integer warehouseId = (Integer) execution.getVariable("warehouseId");
        String rackLocation = (String) execution.getVariable("rackLocation");
        Boolean storageConfirmed = (Boolean) execution.getVariable("storageConfirmed");

        if (Boolean.TRUE.equals(storageConfirmed)) {
            String sql = "UPDATE Shipments SET warehouseId = ?, rackLocation = ?, status = 'Stored' WHERE shipmentId = ?";

            try (PreparedStatement stmt = dbCon.prepareStatement(sql)) {
                stmt.setInt(1, warehouseId);
                stmt.setString(2, rackLocation);
                stmt.setInt(3, shipmentId);
                stmt.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException("Failed to record shipment storage: " + e.getMessage(), e);
            }
        } else {
            throw new RuntimeException("Storage not confirmed for shipment " + shipmentId);
        }
    }
}
