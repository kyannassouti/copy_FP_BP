package mie.ether_example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import edu.toronto.dbservice.config.MIE354DBHelper;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;

public class SelectWinningBid implements JavaDelegate {

    private Connection dbCon;

    public SelectWinningBid() {
        dbCon = MIE354DBHelper.getDBConnection();
    }

    @Override
    public void execute(DelegateExecution execution) {
    	// Sort by cheapest bids, and take the first (and highest quantity in case of a tie)
        String sql = "SELECT bidId, shipmentId, bidderAccount, bidderType, amount, quantity " +
                     "FROM Bids WHERE eligible = TRUE ORDER BY amount ASC, quantity DESC LIMIT 1";

        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            stmt = dbCon.prepareStatement(sql);
            rs = stmt.executeQuery();

            if (rs.next()) {
                Map<String, Object> winningBid = new HashMap<>();
                winningBid.put("bidId", rs.getInt("bidId"));
                winningBid.put("shipmentId", rs.getInt("shipmentId"));
                winningBid.put("bidderAccount", rs.getInt("bidderAccount"));
                winningBid.put("bidderType", rs.getString("bidderType"));
                winningBid.put("amount", rs.getBigDecimal("amount"));
                winningBid.put("quantity", rs.getInt("quantity"));

                execution.setVariable("winningBid", winningBid);
            } else {
                throw new RuntimeException("No eligible bids found in the Bids table.");
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error selecting winning bid: " + e.getMessage(), e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                // Ignored silently
            }
        }
    }
}
