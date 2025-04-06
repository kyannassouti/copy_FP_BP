package mie.ether_example;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import edu.toronto.dbservice.config.MIE354DBHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;

public class DiscardBidServiceTask implements JavaDelegate {

    Connection dbCon = null;

    public DiscardBidServiceTask() {
        dbCon = MIE354DBHelper.getDBConnection();
    }

    @Override
    public void execute(DelegateExecution execution) {

        // Get the current bid being processed
        Map<String, Object> bid = (Map<String, Object>) execution.getVariable("bid");
        Integer bidId = (Integer) bid.get("bidId");

        // SQL to update eligibility to false
        String updateQuery = "UPDATE Bids SET eligible = FALSE WHERE bidId = ?";

        try (PreparedStatement stmt = dbCon.prepareStatement(updateQuery)) {
            stmt.setInt(1, bidId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
