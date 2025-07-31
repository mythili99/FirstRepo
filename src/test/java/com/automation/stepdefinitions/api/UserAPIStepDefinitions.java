package com.automation.stepdefinitions.api;

import com.automation.api.UserAPI;
import com.automation.core.ConfigManager;
import com.automation.core.LoggerManager;
import com.automation.data.TestDataManager;
import com.automation.reporting.ExtentReportManager;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;

import java.util.List;
import java.util.Map;

/**
 * Step definitions for API user management functionality
 */
public class UserAPIStepDefinitions {
    private static final Logger logger = LoggerManager.getInstance().getLogger(UserAPIStepDefinitions.class);
    private final ConfigManager configManager = ConfigManager.getInstance();
    private final TestDataManager testDataManager = TestDataManager.getInstance();
    private final ExtentReportManager extentReportManager = ExtentReportManager.getInstance();
    
    private UserAPI userAPI;
    private Response currentResponse;
    private String currentEndpoint;
    private String currentMethod;

    @Before
    public void setUp() {
        logger.info("Setting up User API Step Definitions");
        userAPI = new UserAPI();
    }

    @After
    public void tearDown() {
        logger.info("Tearing down User API Step Definitions");
    }

    @Given("the API base URL is configured")
    public void theApiBaseUrlIsConfigured() {
        String apiBaseUrl = configManager.getApiBaseUrl();
        Assert.assertNotNull(apiBaseUrl, "API base URL should be configured");
        logger.info("API base URL configured: {}", apiBaseUrl);
        extentReportManager.logInfo(extentReportManager.createTest("API Configuration"), 
                                 "API base URL configured: " + apiBaseUrl);
    }

    @Given("I have valid API credentials")
    public void iHaveValidApiCredentials() {
        // In a real scenario, you would set up authentication tokens
        logger.info("API credentials configured");
        extentReportManager.logInfo(extentReportManager.createTest("API Credentials"), 
                                 "API credentials configured");
    }

    @When("I send a GET request to {string}")
    public void iSendAGetRequestTo(String endpoint) {
        this.currentEndpoint = endpoint;
        this.currentMethod = "GET";
        
        currentResponse = userAPI.getAllUsers(endpoint);
        logger.info("Sent GET request to: {}", endpoint);
        extentReportManager.logInfo(extentReportManager.createTest("GET Request"), 
                                 "Sent GET request to: " + endpoint);
    }

    @When("I send a POST request to {string} with the following data:")
    public void iSendAPostRequestToWithTheFollowingData(String endpoint, io.cucumber.datatable.DataTable dataTable) {
        this.currentEndpoint = endpoint;
        this.currentMethod = "POST";
        
        List<Map<String, String>> data = dataTable.asMaps();
        Map<String, String> userData = data.get(0);
        
        currentResponse = userAPI.createUser(endpoint, userData);
        logger.info("Sent POST request to: {} with data: {}", endpoint, userData);
        extentReportManager.logInfo(extentReportManager.createTest("POST Request"), 
                                 "Sent POST request to: " + endpoint + " with data: " + userData);
    }

    @When("I send a PUT request to {string} with the following data:")
    public void iSendAPutRequestToWithTheFollowingData(String endpoint, io.cucumber.datatable.DataTable dataTable) {
        this.currentEndpoint = endpoint;
        this.currentMethod = "PUT";
        
        List<Map<String, String>> data = dataTable.asMaps();
        Map<String, String> userData = data.get(0);
        
        currentResponse = userAPI.updateUser(endpoint, userData);
        logger.info("Sent PUT request to: {} with data: {}", endpoint, userData);
        extentReportManager.logInfo(extentReportManager.createTest("PUT Request"), 
                                 "Sent PUT request to: " + endpoint + " with data: " + userData);
    }

    @When("I send a DELETE request to {string}")
    public void iSendADeleteRequestTo(String endpoint) {
        this.currentEndpoint = endpoint;
        this.currentMethod = "DELETE";
        
        currentResponse = userAPI.deleteUser(endpoint);
        logger.info("Sent DELETE request to: {}", endpoint);
        extentReportManager.logInfo(extentReportManager.createTest("DELETE Request"), 
                                 "Sent DELETE request to: " + endpoint);
    }

    @When("I send a POST request to {string} with invalid data:")
    public void iSendAPostRequestToWithInvalidData(String endpoint, io.cucumber.datatable.DataTable dataTable) {
        this.currentEndpoint = endpoint;
        this.currentMethod = "POST";
        
        List<Map<String, String>> data = dataTable.asMaps();
        Map<String, String> invalidData = data.get(0);
        
        currentResponse = userAPI.createUser(endpoint, invalidData);
        logger.info("Sent POST request to: {} with invalid data: {}", endpoint, invalidData);
        extentReportManager.logInfo(extentReportManager.createTest("Invalid POST Request"), 
                                 "Sent POST request to: " + endpoint + " with invalid data: " + invalidData);
    }

    @Then("the response status code should be {int}")
    public void theResponseStatusCodeShouldBe(int expectedStatusCode) {
        int actualStatusCode = currentResponse.getStatusCode();
        Assert.assertEquals(actualStatusCode, expectedStatusCode, 
                          "Response status code should be " + expectedStatusCode);
        
        logger.info("Response status code validation passed: expected={}, actual={}", 
                   expectedStatusCode, actualStatusCode);
        extentReportManager.logPass(extentReportManager.createTest("Status Code Validation"), 
                                 "Response status code validation passed: " + actualStatusCode);
    }

    @Then("the response should contain a list of users")
    public void theResponseShouldContainAListOfUsers() {
        String responseBody = currentResponse.getBody().asString();
        Assert.assertTrue(responseBody.contains("users") || responseBody.contains("data"), 
                        "Response should contain user data");
        
        logger.info("Response contains user list");
        extentReportManager.logPass(extentReportManager.createTest("User List Validation"), 
                                 "Response contains user list");
    }

    @Then("the response should contain user details")
    public void theResponseShouldContainUserDetails() {
        String responseBody = currentResponse.getBody().asString();
        Assert.assertTrue(responseBody.contains("id") || responseBody.contains("name"), 
                        "Response should contain user details");
        
        logger.info("Response contains user details");
        extentReportManager.logPass(extentReportManager.createTest("User Details Validation"), 
                                 "Response contains user details");
    }

    @Then("the user ID should be {string}")
    public void theUserIdShouldBe(String expectedUserId) {
        String responseBody = currentResponse.getBody().asString();
        Assert.assertTrue(responseBody.contains(expectedUserId), 
                        "Response should contain user ID: " + expectedUserId);
        
        logger.info("User ID validation passed: {}", expectedUserId);
        extentReportManager.logPass(extentReportManager.createTest("User ID Validation"), 
                                 "User ID validation passed: " + expectedUserId);
    }

    @Then("the response should contain the created user details")
    public void theResponseShouldContainTheCreatedUserDetails() {
        String responseBody = currentResponse.getBody().asString();
        Assert.assertTrue(responseBody.contains("id") || responseBody.contains("created"), 
                        "Response should contain created user details");
        
        logger.info("Created user details validation passed");
        extentReportManager.logPass(extentReportManager.createTest("Created User Validation"), 
                                 "Created user details validation passed");
    }

    @Then("the user should have an ID")
    public void theUserShouldHaveAnId() {
        String responseBody = currentResponse.getBody().asString();
        Assert.assertTrue(responseBody.contains("id"), "User should have an ID");
        
        logger.info("User ID presence validation passed");
        extentReportManager.logPass(extentReportManager.createTest("User ID Presence"), 
                                 "User ID presence validation passed");
    }

    @Then("the response should contain the updated user details")
    public void theResponseShouldContainTheUpdatedUserDetails() {
        String responseBody = currentResponse.getBody().asString();
        Assert.assertTrue(responseBody.contains("id") || responseBody.contains("updated"), 
                        "Response should contain updated user details");
        
        logger.info("Updated user details validation passed");
        extentReportManager.logPass(extentReportManager.createTest("Updated User Validation"), 
                                 "Updated user details validation passed");
    }

    @Then("the user should be deleted")
    public void theUserShouldBeDeleted() {
        // For DELETE requests, we typically check the status code (204) rather than response body
        int statusCode = currentResponse.getStatusCode();
        Assert.assertEquals(statusCode, 204, "User should be deleted (status 204)");
        
        logger.info("User deletion validation passed");
        extentReportManager.logPass(extentReportManager.createTest("User Deletion"), 
                                 "User deletion validation passed");
    }

    @Then("the response should contain an error message")
    public void theResponseShouldContainAnErrorMessage() {
        String responseBody = currentResponse.getBody().asString();
        Assert.assertTrue(responseBody.contains("error") || responseBody.contains("message"), 
                        "Response should contain an error message");
        
        logger.info("Error message validation passed");
        extentReportManager.logPass(extentReportManager.createTest("Error Message Validation"), 
                                 "Error message validation passed");
    }

    @Then("the response should contain validation errors")
    public void theResponseShouldContainValidationErrors() {
        String responseBody = currentResponse.getBody().asString();
        Assert.assertTrue(responseBody.contains("error") || responseBody.contains("validation"), 
                        "Response should contain validation errors");
        
        logger.info("Validation errors validation passed");
        extentReportManager.logPass(extentReportManager.createTest("Validation Errors"), 
                                 "Validation errors validation passed");
    }

    @Then("the response time should be less than {int}ms")
    public void theResponseTimeShouldBeLessThanMs(int maxResponseTime) {
        long actualResponseTime = currentResponse.getTime();
        Assert.assertTrue(actualResponseTime < maxResponseTime, 
                        "Response time should be less than " + maxResponseTime + "ms, but was " + actualResponseTime + "ms");
        
        logger.info("Response time validation passed: {}ms < {}ms", actualResponseTime, maxResponseTime);
        extentReportManager.logPass(extentReportManager.createTest("Response Time Validation"), 
                                 "Response time validation passed: " + actualResponseTime + "ms");
    }

    @Then("the response should be valid JSON")
    public void theResponseShouldBeValidJson() {
        String responseBody = currentResponse.getBody().asString();
        Assert.assertTrue(responseBody.startsWith("{") || responseBody.startsWith("["), 
                        "Response should be valid JSON");
        
        logger.info("JSON validation passed");
        extentReportManager.logPass(extentReportManager.createTest("JSON Validation"), 
                                 "JSON validation passed");
    }

    // Data-driven test using Excel
    @When("I test API endpoints with data from Excel sheet {string}")
    public void iTestApiEndpointsWithDataFromExcelSheet(String sheetName) {
        List<Map<String, String>> apiTestData = testDataManager.readExcelData(sheetName);
        
        for (Map<String, String> data : apiTestData) {
            String endpoint = data.get("Endpoint");
            String method = data.get("Method");
            String expectedStatus = data.get("ExpectedStatus");
            String expectedResponse = data.get("ExpectedResponse");
            
            logger.info("Testing API: {} {} - Expected: {} {}", method, endpoint, expectedStatus, expectedResponse);
            
            Response response = null;
            switch (method.toUpperCase()) {
                case "GET":
                    response = userAPI.getAllUsers(endpoint);
                    break;
                case "POST":
                    response = userAPI.createUser(endpoint, data);
                    break;
                case "PUT":
                    response = userAPI.updateUser(endpoint, data);
                    break;
                case "DELETE":
                    response = userAPI.deleteUser(endpoint);
                    break;
            }
            
            if (response != null) {
                int actualStatus = response.getStatusCode();
                int expectedStatusInt = Integer.parseInt(expectedStatus);
                Assert.assertEquals(actualStatus, expectedStatusInt, 
                                 "Status code should be " + expectedStatus);
                
                logger.info("API test passed: {} {} - Status: {}", method, endpoint, actualStatus);
            }
        }
    }

    // Data-driven test using JSON
    @When("I test API endpoints with data from JSON")
    public void iTestApiEndpointsWithDataFromJson() {
        Map<String, Object> testData = testDataManager.readJsonAsMap("TestData.json");
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> apiTestData = (List<Map<String, Object>>) testData.get("apiTestData");
        
        for (Map<String, Object> data : apiTestData) {
            String endpoint = (String) data.get("endpoint");
            String method = (String) data.get("method");
            Integer expectedStatus = (Integer) data.get("expectedStatus");
            String expectedResponse = (String) data.get("expectedResponse");
            
            logger.info("Testing API: {} {} - Expected: {} {}", method, endpoint, expectedStatus, expectedResponse);
            
            Response response = null;
            switch (method.toUpperCase()) {
                case "GET":
                    response = userAPI.getAllUsers(endpoint);
                    break;
                case "POST":
                    response = userAPI.createUser(endpoint, Map.of("name", "Test User"));
                    break;
                case "PUT":
                    response = userAPI.updateUser(endpoint, Map.of("name", "Updated User"));
                    break;
                case "DELETE":
                    response = userAPI.deleteUser(endpoint);
                    break;
            }
            
            if (response != null) {
                int actualStatus = response.getStatusCode();
                Assert.assertEquals(actualStatus, expectedStatus.intValue(), 
                                 "Status code should be " + expectedStatus);
                
                logger.info("API test passed: {} {} - Status: {}", method, endpoint, actualStatus);
            }
        }
    }
}