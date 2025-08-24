package com.automation.steps;

import com.automation.api.ApiClient;
import com.automation.api.ApiValidator;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import org.apache.logging.log4j.Logger;

import java.util.Map;

/**
 * API step definitions for Cucumber scenarios
 * Provides reusable steps for API testing
 */
public class ApiSteps extends BaseSteps {
    
    private static final Logger logger = LoggerManager.getLogger(ApiSteps.class);
    private ApiClient apiClient;
    private Response response;
    private String requestBody;
    
    @Given("I have the API client configured")
    public void i_have_the_api_client_configured() {
        apiClient = ApiClient.getInstance();
        extentReportManager.logInfo("API client configured with base URL: " + apiClient.getBaseUri());
        logger.info("API client configured");
    }
    
    @Given("I set the request body to {string}")
    public void i_set_the_request_body_to(String body) {
        this.requestBody = body;
        extentReportManager.logInfo("Request body set: " + body);
        logger.info("Request body set: {}", body);
    }
    
    @When("I send a GET request to {string}")
    public void i_send_a_get_request_to(String endpoint) {
        response = apiClient.get(endpoint);
        extentReportManager.logInfo("GET request sent to: " + endpoint);
        logger.info("GET request sent to endpoint: {}", endpoint);
    }
    
    @When("I send a POST request to {string}")
    public void i_send_a_post_request_to(String endpoint) {
        response = apiClient.post(endpoint, requestBody);
        extentReportManager.logInfo("POST request sent to: " + endpoint);
        logger.info("POST request sent to endpoint: {}", endpoint);
    }
    
    @When("I send a PUT request to {string}")
    public void i_send_a_put_request_to(String endpoint) {
        response = apiClient.put(endpoint, requestBody);
        extentReportManager.logInfo("PUT request sent to: " + endpoint);
        logger.info("PUT request sent to endpoint: {}", endpoint);
    }
    
    @When("I send a DELETE request to {string}")
    public void i_send_a_delete_request_to(String endpoint) {
        response = apiClient.delete(endpoint);
        extentReportManager.logInfo("DELETE request sent to: " + endpoint);
        logger.info("DELETE request sent to endpoint: {}", endpoint);
    }
    
    @Then("the response status code should be {int}")
    public void the_response_status_code_should_be(int expectedStatusCode) {
        new ApiValidator(response).validateStatusCode(expectedStatusCode);
        logger.info("Status code validation passed: {}", expectedStatusCode);
    }
    
    @Then("the response should be successful")
    public void the_response_should_be_successful() {
        new ApiValidator(response).validateSuccessStatusCode();
        logger.info("Success status code validation passed");
    }
    
    @Then("the response time should be less than {long} milliseconds")
    public void the_response_time_should_be_less_than_milliseconds(long maxResponseTime) {
        new ApiValidator(response).validateResponseTime(maxResponseTime);
        logger.info("Response time validation passed: {}ms", response.getTime());
    }
    
    @Then("the response should contain field {string} with value {string}")
    public void the_response_should_contain_field_with_value(String jsonPath, String expectedValue) {
        new ApiValidator(response).validateJsonField(jsonPath, expectedValue);
        logger.info("JSON field validation passed: {} = {}", jsonPath, expectedValue);
    }
    
    @Then("the response should contain field {string}")
    public void the_response_should_contain_field(String jsonPath) {
        new ApiValidator(response).validateJsonFieldExists(jsonPath);
        logger.info("JSON field existence validation passed: {}", jsonPath);
    }
    
    @Then("the response array {string} should have size {int}")
    public void the_response_array_should_have_size(String jsonPath, int expectedSize) {
        new ApiValidator(response).validateJsonArraySize(jsonPath, expectedSize);
        logger.info("JSON array size validation passed: {} has size {}", jsonPath, expectedSize);
    }
    
    @Then("the response should have content type {string}")
    public void the_response_should_have_content_type(String expectedContentType) {
        new ApiValidator(response).validateContentType(expectedContentType);
        logger.info("Content type validation passed: {}", expectedContentType);
    }
    
    @Then("the response should contain text {string}")
    public void the_response_should_contain_text(String expectedText) {
        new ApiValidator(response).validateBodyContains(expectedText);
        logger.info("Response body contains validation passed: {}", expectedText);
    }
    
    @Then("the response body should not be empty")
    public void the_response_body_should_not_be_empty() {
        new ApiValidator(response).validateBodyNotEmpty();
        logger.info("Response body not empty validation passed");
    }
}