package mie.ether_example;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import edu.toronto.dbservice.config.MIE354DBHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;

public class StoreBidServiceTask implements JavaDelegate {

    Connection dbCon = null;

    public StoreBidServiceTask() {
        dbCon = MIE354DBHelper.getDBConnection();
    }

    @Override
    public void execute(DelegateExecution execution) {

        // Get the bid currently being evaluated
        Map<String, Object> bid = (Map<String, Object>) execution.getVariable("bid");
        Integer bidId = (Integer) bid.get("bidId");

        // Get the eligibility decision from the user form
        Boolean isEligible = (Boolean) execution.getVariable("eligible");

        // Update the bid in the database
        String updateQuery = "UPDATE Bids SET eligible = ? WHERE bidId = ?";

        try (PreparedStatement stmt = dbCon.prepareStatement(updateQuery)) {
            stmt.setBoolean(1, isEligible);
            stmt.setInt(2, bidId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
