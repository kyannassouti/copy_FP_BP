package mie.ether_example;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

import edu.toronto.dbservice.config.MIE354DBHelper;
import edu.toronto.dbservice.types.ShipmentInfo;

public class PrepareSubInvoices implements JavaDelegate {

    private Connection dbCon;

    public PrepareSubInvoices() {
        dbCon = MIE354DBHelper.getDBConnection();
    }

    @Override
    public void execute(DelegateExecution execution) {
        ShipmentInfo shipment = (ShipmentInfo) execution.getVariable("shipmentInfo");

        Integer shipmentId = shipment.getShipmentId();
        Integer farmerAccount = shipment.getFarmerAccount();
        Integer carrierAccount = shipment.getCarrierAccount();
        Integer inspectorAccount = 8; // Assuming inspector account is constant

        // Query CarrierInvoice for the specific shipmentId
        double carrierAmount = 0.0;
        int distanceKM = 0;
        String carrierQuery = "SELECT amount, distanceKM FROM CarrierInvoices WHERE shipmentId = ?";
        try (PreparedStatement stmt = dbCon.prepareStatement(carrierQuery)) {
            stmt.setInt(1, shipmentId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                carrierAmount = rs.getDouble("amount");
                distanceKM = rs.getInt("distanceKM");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching carrier invoice for shipmentId " + shipmentId, e);
        }

        // Query InspectorInvoice for the specific shipmentId
        double inspectorFee = 0.0;
        String inspectionQuery = "SELECT inspectionFee FROM InspectorInvoices WHERE shipmentId = ?";
        try (PreparedStatement stmt = dbCon.prepareStatement(inspectionQuery)) {
            stmt.setInt(1, shipmentId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                inspectorFee = rs.getDouble("inspectionFee");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching inspector invoice for shipmentId " + shipmentId, e);
        }

        // Create a list to hold the invoices for Farmer, Carrier, and Inspector
        List<Map<String, Object>> invoiceList = new ArrayList<>();

        // Prepare invoices for each role
        String role = (String) execution.getVariable("role");

        // Farmer Invoice Preparation
        if (role.equals("Farmer")) {
            double farmerAmount = 0.0;
            String farmerQuery =
                "UPDATE FarmerInvoices f " +
                "INNER JOIN Bids b ON f.shipmentId = b.shipmentId AND f.farmerAccount = b.bidderAccount " +
                "SET f.amount = b.amount " +
                "WHERE f.shipmentId = ? AND f.farmerAccount = ? " +
                "ORDER BY b.amount ASC, b.quantity DESC " +
                "LIMIT 1";

            try (PreparedStatement farmerStmt = dbCon.prepareStatement(farmerQuery)) {
                farmerStmt.setInt(1, shipmentId);
                farmerStmt.setInt(2, farmerAccount);
                int rowsAffected = farmerStmt.executeUpdate();

                // If no rows are updated, then the farmer amount remains 0
                if (rowsAffected > 0) {
                    String getAmountQuery = "SELECT amount FROM FarmerInvoices WHERE shipmentId = ? AND farmerAccount = ?";
                    try (PreparedStatement getAmountStmt = dbCon.prepareStatement(getAmountQuery)) {
                        getAmountStmt.setInt(1, shipmentId);
                        getAmountStmt.setInt(2, farmerAccount);
                        ResultSet rs = getAmountStmt.executeQuery();
                        if (rs.next()) {
                            farmerAmount = rs.getDouble("amount");
                        }
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException("Error fetching or updating farmer invoice for shipmentId " + shipmentId, e);
            }

            Map<String, Object> farmerInvoice = new HashMap<>();
            farmerInvoice.put("shipmentId", shipmentId);
            farmerInvoice.put("account", farmerAccount);
            farmerInvoice.put("amount", farmerAmount);
            farmerInvoice.put("role", "Farmer");
            invoiceList.add(farmerInvoice);
        }

        // Carrier Invoice Preparation
        if (role.equals("Carrier")) {
            Map<String, Object> carrierInvoice = new HashMap<>();
            carrierInvoice.put("shipmentId", shipmentId);
            carrierInvoice.put("account", carrierAccount);
            carrierInvoice.put("amount", carrierAmount);
            carrierInvoice.put("distanceKM", distanceKM);
            carrierInvoice.put("role", "Carrier");
            invoiceList.add(carrierInvoice);
        }

        // Inspector Invoice Preparation
        if (role.equals("Inspector")) {
            Map<String, Object> inspectorInvoice = new HashMap<>();
            inspectorInvoice.put("shipmentId", shipmentId);
            inspectorInvoice.put("account", inspectorAccount);
            inspectorInvoice.put("amount", inspectorFee);
            inspectorInvoice.put("role", "Inspector");
            invoiceList.add(inspectorInvoice);
        }

        // Add the invoice list to the execution context
        execution.setVariable("invoiceList", invoiceList);
    }

}
