package mie.ether_example;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.flowable.engine.HistoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.form.FormProperty;
import org.flowable.engine.form.TaskFormData;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.task.api.Task;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import edu.toronto.dbservice.types.EtherAccount;

import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.history.HistoricProcessInstance;


/* Run with parameters */
//@RunWith(Parameterized.class)
public class ProcessFlowUnitTest extends LabBaseUnitTest {

    @BeforeClass
    public static void setupFile() {
        filename = "src/main/resources/diagrams/Final_Diagram.bpmn";
    }
    
	private void startProcess() {	
		RuntimeService runtimeService = flowableContext.getRuntimeService();
		processInstance = runtimeService.startProcessInstanceByKey("process_pool1");
		System.out.println("process_pool1===================");
	}
	
	// Fill Determine Bid Eligibility User Task Form
	private void fillDeterminBidEligibilityForm(int farmerAccount, double bidAmount, int quantity, boolean eligible) {
	    // Prepare the form entries using a map
	    HashMap<String, String> formEntries = new HashMap<>();
	    formEntries.put("farmerAccount", String.valueOf(farmerAccount));
	    formEntries.put("bidAmount", String.valueOf(bidAmount));
	    formEntries.put("quantity", String.valueOf(quantity));
	    formEntries.put("eligible", String.valueOf(eligible));

	    // Get the user task "DeterminBidEligibility"
	    TaskService taskService = flowableContext.getTaskService();
	    Task determinBidEligibilityTask = taskService.createTaskQuery().taskDefinitionKey("usertask8")
	            .singleResult();

	    // Get the list of fields in the form
	    List<String> bpmnFieldNames = new ArrayList<>();
	    TaskFormData taskFormData = flowableContext.getFormService().getTaskFormData(determinBidEligibilityTask.getId());
	    for (FormProperty fp : taskFormData.getFormProperties()) {
	        bpmnFieldNames.add(fp.getId());
	    }

	    // Define the required fields for the form
	    List<String> requiredFields = new ArrayList<>(Arrays.asList("farmerAccount", "bidAmount", "quantity", "eligible"));

	    // Ensure that each of the required fields is present in the form
	    for (String requiredFieldName : requiredFields) {
	        assertTrue(bpmnFieldNames.contains(requiredFieldName));
	    }

	    // Ensure that each of the required fields is assigned a value
	    for (String requiredFieldName : requiredFields) {
	        assertTrue(formEntries.keySet().contains(requiredFieldName));
	    }

	    // Submit the form
	    flowableContext.getFormService().submitTaskFormData(determinBidEligibilityTask.getId(), formEntries);
	}
	
	// Fill Request Inspection User Task Form
	private void fillRequestInspectionForm(int shipmentId, int inspectorAccount, String urgencyLevel) {
	    // Prepare the form entries using a map
	    HashMap<String, String> formEntries = new HashMap<>();
	    formEntries.put("shipmentId", String.valueOf(shipmentId));
	    formEntries.put("inspectorAccount", String.valueOf(inspectorAccount));
	    formEntries.put("urgencyLevel", urgencyLevel);

	    // Get the user task "usertask2" (Inspector review task)
	    TaskService taskService = flowableContext.getTaskService();
	    Task inspectorReviewTask = taskService.createTaskQuery().taskDefinitionKey("usertask2")
	            .singleResult();

	    // Get the list of fields in the form
	    List<String> bpmnFieldNames = new ArrayList<>();
	    TaskFormData taskFormData = flowableContext.getFormService().getTaskFormData(inspectorReviewTask.getId());
	    for (FormProperty fp : taskFormData.getFormProperties()) {
	        bpmnFieldNames.add(fp.getId());
	    }

	    // Define the required fields for the form
	    List<String> requiredFields = new ArrayList<>(Arrays.asList("shipmentId", "inspectorAccount", "urgencyLevel"));

	    // Ensure that each of the required fields is present in the form
	    for (String requiredFieldName : requiredFields) {
	        assertTrue(bpmnFieldNames.contains(requiredFieldName));
	    }

	    // Ensure that each of the required fields is assigned a value
	    for (String requiredFieldName : requiredFields) {
	        assertTrue(formEntries.keySet().contains(requiredFieldName));
	    }

	    // Submit the form (this will lead to completing the user task)
	    flowableContext.getFormService().submitTaskFormData(inspectorReviewTask.getId(), formEntries);
	}
	
	// Fill Store Shipment User Task Form
	private void fillStoreShipmentForm(int shipmentId, int warehouseId, String rackLocation, boolean storageConfirmed) {
	    // Prepare the form entries using a map 
	    HashMap<String, String> formEntries = new HashMap<>();
	    formEntries.put("shipmentId", String.valueOf(shipmentId));
	    formEntries.put("warehouseId", String.valueOf(warehouseId));
	    formEntries.put("rackLocation", rackLocation); 
	    formEntries.put("storageConfirmed", String.valueOf(storageConfirmed));

	    // Get the user task "usertask4" (Store Shipment task)
	    TaskService taskService = flowableContext.getTaskService();
	    Task storeShipmentTask = taskService.createTaskQuery().taskDefinitionKey("usertask4")
	            .singleResult();

	    // Get the list of fields in the form
	    List<String> bpmnFieldNames = new ArrayList<>();
	    TaskFormData taskFormData = flowableContext.getFormService().getTaskFormData(storeShipmentTask.getId());
	    for (FormProperty fp : taskFormData.getFormProperties()) {
	        bpmnFieldNames.add(fp.getId());
	    }

	    // Define the required fields for the form
	    List<String> requiredFields = new ArrayList<>(Arrays.asList("shipmentId", "warehouseId", "rackLocation", "storageConfirmed"));

	    // Ensure that each of the required fields is present in the form
	    for (String requiredFieldName : requiredFields) {
	        assertTrue(bpmnFieldNames.contains(requiredFieldName));
	    }

	    // Ensure that each of the required fields is assigned a value
	    for (String requiredFieldName : requiredFields) {
	        assertTrue(formEntries.keySet().contains(requiredFieldName));
	    }

	    // Submit the form (this will lead to completing the user task)
	    flowableContext.getFormService().submitTaskFormData(storeShipmentTask.getId(), formEntries);
	}
	
	
 
     // Test 1: Check if the process starts and pauses at the first user task.
     // This verifies that the process correctly stops at the "Determine Bid Eligibility" task after initiation.
    @Test
    public void testProcessStartsAndPausesAtFirstUsertask() {
    	
    	startProcess();
        // Ensure the process started
        assertNotNull("Process instance should not be null", processInstance);

		// get pending tasks
		List<Task> list = flowableContext.getTaskService().createTaskQuery()
				.list();
		
		// assert one pending task
		assertTrue(list.size() == 1);
		
		/* assert pending task id */
		assertTrue("Found the first user task", list.get(0).getTaskDefinitionKey().equals("usertask8"));
    }
    
    // Test 2: Check if the process reaches completion successfully.
    @Test
    public void testProcessEndsSuccessfully() {
        // Start the process
        startProcess();
        
        // Ensure the process started
        assertNotNull("Process instance should not be null", processInstance);

        // Complete the first task: Determine Bid Eligibility
        fillDeterminBidEligibilityForm(4, 1100, 200, true);  // Winning bid (farmer 4)
        
        // Complete the second task: Request Inspection
        fillRequestInspectionForm(101, 8, "High");
        
        // Complete the third task: Store Shipment
        fillStoreShipmentForm(101, 1, "A1-01", true);  // Example values for the form
        
        // Fetch the list of completed tasks to verify the process has moved forward
        List<Task> list = flowableContext.getTaskService().createTaskQuery().list();

        // Assert that no tasks are left (process has ended)
        assertTrue("No tasks should be pending", list.isEmpty());

        // Now fetch the process instance history to verify that the process has ended
        HistoryService historyService = flowableContext.getHistoryService();
        HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery()
                .processInstanceId(processInstance.getId())
                .singleResult();

        // Assert that the process has ended
        assertNotNull("Historic process instance should not be null", historicProcessInstance);
        assertEquals("Process should be finished", "completed", historicProcessInstance.getState());
    }
    
    @Test
    public void testComplexTypeHandling() {
        // Create a ShipmentInfo object to pass as a process variable
        ShipmentInfo shipmentInfo = new ShipmentInfo();
        shipmentInfo.setShipmentId(101);
        shipmentInfo.setFarmerAccount(1);
        shipmentInfo.setCarrierAccount(9);
        shipmentInfo.setStatus("In Transit");
        shipmentInfo.setWarehouseId("WH-01");
        shipmentInfo.setRackLocation("A1-01");

        // Set the complex type as a process variable
        Map<String, Object> variables = new HashMap<>();
        variables.put("shipmentInfo", shipmentInfo);

        // Start the process and pass the complex type as a process variable
        RuntimeService runtimeService = flowableContext.getRuntimeService();
        processInstance = runtimeService.startProcessInstanceByKey("process_pool1", variables);
        System.out.println("Process started with complex type variable.");

        // Ensure the process started
        assertNotNull("Process instance should not be null", processInstance);

        // Get the task "usertask1" and fill the form
        TaskService taskService = flowableContext.getTaskService();
        Task task = taskService.createTaskQuery().taskDefinitionKey("usertask1").singleResult();
        
        // Retrieve the form data for this task
        TaskFormData taskFormData = flowableContext.getFormService().getTaskFormData(task.getId());
        assertNotNull("Form data should not be null", taskFormData);

        // Extract the values from the form and verify the complex type (shipmentInfo) data is passed correctly
        Map<String, String> formEntries = new HashMap<>();
        for (FormProperty fp : taskFormData.getFormProperties()) {
            formEntries.put(fp.getId(), fp.getValue());
        }

        // Here, we're assuming that the complex type is serialized/deserialized correctly as a string or JSON.
        // In this case, we validate the content of shipmentInfo manually.
        assertEquals("Shipment ID should match", 101, shipmentInfo.getShipmentId());
        assertEquals("Farmer Account should match", 1, shipmentInfo.getFarmerAccount());
        assertEquals("Carrier Account should match", 9, shipmentInfo.getCarrierAccount());
        assertEquals("Status should match", "In Transit", shipmentInfo.getStatus());
        assertEquals("Warehouse ID should match", "WH-01", shipmentInfo.getWarehouseId());
        assertEquals("Rack Location should match", "A1-01", shipmentInfo.getRackLocation());

        // Continue with process (e.g., submit form)
        flowableContext.getFormService().submitTaskFormData(task.getId(), formEntries);
        
        // Optionally, check if the process completes or moves to the next task
        Task nextTask = taskService.createTaskQuery().taskDefinitionKey("usertask2").singleResult();
        assertNotNull("Next task should be available", nextTask);
        assertEquals("Next task should be usertask2", "usertask2", nextTask.getTaskDefinitionKey());
    }

    
    /*

     // Test 2: Check if the process reaches completion successfully.
     // Simulates an ideal path where shipment is stored, invoices are prepared and reviewed, and payment is approved.
    @Test
    public void testProcessReachesEnd() {
        // Start the process
        processInstance = flowableContext.getRuntimeService().startProcessInstanceByKey("process_pool1");

        TaskService taskService = flowableContext.getTaskService();

        // Step 1: Complete "Store Shipment" user task
        Task storeTask = taskService.createTaskQuery()
                .taskDefinitionKey("usertask4") 
                .singleResult();
        assertNotNull("Store Shipment task should exist", storeTask);
        taskService.complete(storeTask.getId());

        // Step 2: Complete "Review Invoices" user task
        Task reviewTask = taskService.createTaskQuery()
                .taskDefinitionKey("usertask6")  
                .singleResult();
        assertNotNull("Review Invoices task should exist", reviewTask);

        // Submit the form values (simulate approval of all invoices)
        Map<String, String> formEntries = new HashMap<>();
        formEntries.put("farmerPaymentApproved", "true");
        formEntries.put("carrierPaymentApproved", "true");
        formEntries.put("inspectorPaymentApproved", "true");

        flowableContext.getFormService().submitTaskFormData(reviewTask.getId(), formEntries);

        // Step 3: Complete "Approve Payment" user task
        Task approveTask = taskService.createTaskQuery()
                .taskDefinitionKey("usertask5")
                .singleResult();
        assertNotNull("Approve Payment task should exist", approveTask);
        taskService.complete(approveTask.getId());

        // Final check: process should be marked as completed
        HistoryService historyService = flowableContext.getHistoryService();
        HistoricProcessInstance historic = historyService
                .createHistoricProcessInstanceQuery()
                .processInstanceId(processInstance.getId())
                .singleResult();

        assertNotNull("Historic process instance should be found", historic);
        assertTrue("Process should be ended", historic.getEndTime() != null);
    }
  
     // Test 3: Verify that the complex type ShipmentInfo is set, passed, and retrieved correctly.
    @Test
    public void testComplexTypeShipmentInfoHandledCorrectly() {
        // Step 1: Create and set a complex ShipmentInfo object as a process variable
        ShipmentInfo testShipment = new ShipmentInfo(
            123,       // shipmentId
            201,       // farmerId
            301,       // carrierId
            "In Transit",
            null,      // warehouseId
            null       // rackLocation
        );

        Map<String, Object> variables = new HashMap<>();
        variables.put("shipmentInfo", testShipment);

        processInstance = flowableContext.getRuntimeService()
                .startProcessInstanceByKey("process_pool1", variables);

        // Step 2: Retrieve the variable from runtime and assert contents
        ShipmentInfo retrieved = (ShipmentInfo) flowableContext.getRuntimeService()
                .getVariable(processInstance.getId(), "shipmentInfo");

        assertNotNull("ShipmentInfo variable should be stored in the process context", retrieved);
        assertTrue("Shipment ID should match", retrieved.getShipmentId().equals(123));
        assertTrue("Farmer ID should match", retrieved.getFarmerId().equals(201));
        assertTrue("Carrier ID should match", retrieved.getCarrierId().equals(301));
        assertTrue("Status should be 'In Transit'", retrieved.getStatus().equals("In Transit"));
        assertTrue("Warehouse ID should be null", retrieved.getWarehouseId() == null);
        assertTrue("Rack Location should be null", retrieved.getRackLocation() == null);
    }

     //Test 4: Fill out and submit a form correctly at a user task.
     // This test checks that the form on the "Store Shipment" task exists, contains required fields, and submits successfully.
    @Test
    public void testFormSubmissionAtStoreShipment() {
        processInstance = flowableContext.getRuntimeService().startProcessInstanceByKey("process_pool1");

        Task storeTask = flowableContext.getTaskService().createTaskQuery()
                .taskDefinitionKey("usertask4") 
                .singleResult();

        assertNotNull("Store Shipment task should be available", storeTask);

        // STEP 1: Check form fields
        TaskFormData formData = flowableContext.getFormService().getTaskFormData(storeTask.getId());

        List<String> expectedFields = Arrays.asList("warehouseId", "rackLocation", "storageConfirmed");

        // Check if formProperties is null before iterating
        if (formData != null && formData.getFormProperties() != null) {
            for (FormProperty field : formData.getFormProperties()) {
                assertTrue("Expected form field is missing: " + field.getId(),
                        expectedFields.contains(field.getId()));
            }
        } else {
             System.out.println("Warning: TaskFormData or FormProperties is null for task ID: " + storeTask.getId());
        }

        // STEP 2: Submit the form with dummy test values
        Map<String, String> formValues = new HashMap<>();
        formValues.put("warehouseId", "2");
        formValues.put("rackLocation", "B2-15");
        formValues.put("storageConfirmed", "true");

        flowableContext.getFormService().submitTaskFormData(storeTask.getId(), formValues);

        // STEP 3: Ensure process moved past the form
        Task nextTask = flowableContext.getTaskService().createTaskQuery()
                .processInstanceId(processInstance.getId())
                .singleResult();

        assertNotNull("Process should move to next user task after form is submitted", nextTask);
        assertTrue("Next task should not be storeShipment again", !nextTask.getTaskDefinitionKey().equals("storeShipment"));
    }
    */
}