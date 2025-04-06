package mie.ether_example;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.toronto.dbservice.config.MIE354DBHelper;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;

public class CollectBidsServiceTask implements JavaDelegate {

    Connection dbCon = null;

    public CollectBidsServiceTask() {
        dbCon = MIE354DBHelper.getDBConnection();
    }

    @Override
    public void execute(DelegateExecution execution) {

        Statement statement = null;
        ResultSet resultSet = null;
        List<Map<String, Object>> farmerBids = new ArrayList<>();

        try {
            statement = dbCon.createStatement();

            // SQL query to collect only Farmer bids
            resultSet = statement.executeQuery("SELECT * FROM Bids WHERE bidderType = 'Farmer'");

            while (resultSet.next()) {
                Map<String, Object> bid = new HashMap<>();
                bid.put("bidId", resultSet.getInt("bidId"));
                bid.put("shipmentId", resultSet.getInt("shipmentId"));
                bid.put("bidderId", resultSet.getInt("bidderId"));
                bid.put("bidderType", resultSet.getString("bidderType"));
                bid.put("amount", resultSet.getBigDecimal("amount"));
                bid.put("quantity", resultSet.getInt("quantity"));
                bid.put("eligible", resultSet.getBoolean("eligible"));

                farmerBids.add(bid);
            }

            resultSet.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Save the list of farmer bids as a process variable
        execution.setVariable("farmerBids", farmerBids);
    }
}
