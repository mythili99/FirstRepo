package com.automation.api;

import com.automation.utils.ExtentReportManager;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hamcrest.Matchers;

import java.io.File;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * API response validator utility for validating REST API responses
 * Provides methods for validating status codes, response body, headers, and JSON schema
 */
public class ApiValidator {
    
    private static final Logger logger = LogManager.getLogger(ApiValidator.class);
    private final Response response;
    private final ExtentReportManager extentReportManager;
    private final ObjectMapper objectMapper;
    
    public ApiValidator(Response response) {
        this.response = response;
        this.extentReportManager = ExtentReportManager.getInstance();
        this.objectMapper = new ObjectMapper();
    }
    
    /**
     * Validate status code
     * @param expectedStatusCode Expected status code
     * @return ApiValidator for method chaining
     */
    public ApiValidator validateStatusCode(int expectedStatusCode) {
        try {
            int actualStatusCode = response.getStatusCode();
            assertThat(actualStatusCode)
                    .as("Status code validation")
                    .isEqualTo(expectedStatusCode);
            
            logger.info("Status code validation passed: Expected={}, Actual={}", expectedStatusCode, actualStatusCode);
            extentReportManager.logPass("Status code validation passed: " + actualStatusCode);
            
        } catch (AssertionError e) {
            String errorMessage = String.format("Status code validation failed: Expected=%d, Actual=%d", 
                                               expectedStatusCode, response.getStatusCode());
            logger.error(errorMessage);
            extentReportManager.logFail(errorMessage);
            throw e;
        }
        
        return this;
    }
    
    /**
     * Validate status code is in the success range (200-299)
     * @return ApiValidator for method chaining
     */
    public ApiValidator validateSuccessStatusCode() {
        try {
            int statusCode = response.getStatusCode();
            assertThat(statusCode)
                    .as("Success status code validation")
                    .isBetween(200, 299);
            
            logger.info("Success status code validation passed: {}", statusCode);
            extentReportManager.logPass("Success status code validation passed: " + statusCode);
            
        } catch (AssertionError e) {
            String errorMessage = "Success status code validation failed: " + response.getStatusCode();
            logger.error(errorMessage);
            extentReportManager.logFail(errorMessage);
            throw e;
        }
        
        return this;
    }
    
    /**
     * Validate response time
     * @param maxResponseTime Maximum acceptable response time in milliseconds
     * @return ApiValidator for method chaining
     */
    public ApiValidator validateResponseTime(long maxResponseTime) {
        try {
            long actualResponseTime = response.getTime();
            assertThat(actualResponseTime)
                    .as("Response time validation")
                    .isLessThanOrEqualTo(maxResponseTime);
            
            logger.info("Response time validation passed: {}ms (max: {}ms)", actualResponseTime, maxResponseTime);
            extentReportManager.logPass("Response time validation passed: " + actualResponseTime + "ms");
            
        } catch (AssertionError e) {
            String errorMessage = String.format("Response time validation failed: %dms (max: %dms)", 
                                               response.getTime(), maxResponseTime);
            logger.error(errorMessage);
            extentReportManager.logFail(errorMessage);
            throw e;
        }
        
        return this;
    }
    
    /**
     * Validate response header
     * @param headerName Header name
     * @param expectedValue Expected header value
     * @return ApiValidator for method chaining
     */
    public ApiValidator validateHeader(String headerName, String expectedValue) {
        try {
            String actualValue = response.getHeader(headerName);
            assertThat(actualValue)
                    .as("Header validation for: " + headerName)
                    .isEqualTo(expectedValue);
            
            logger.info("Header validation passed: {}={}", headerName, actualValue);
            extentReportManager.logPass("Header validation passed: " + headerName + "=" + actualValue);
            
        } catch (AssertionError e) {
            String errorMessage = String.format("Header validation failed: %s. Expected=%s, Actual=%s", 
                                               headerName, expectedValue, response.getHeader(headerName));
            logger.error(errorMessage);
            extentReportManager.logFail(errorMessage);
            throw e;
        }
        
        return this;
    }
    
    /**
     * Validate response header exists
     * @param headerName Header name
     * @return ApiValidator for method chaining
     */
    public ApiValidator validateHeaderExists(String headerName) {
        try {
            String headerValue = response.getHeader(headerName);
            assertThat(headerValue)
                    .as("Header existence validation for: " + headerName)
                    .isNotNull();
            
            logger.info("Header existence validation passed: {}", headerName);
            extentReportManager.logPass("Header existence validation passed: " + headerName);
            
        } catch (AssertionError e) {
            String errorMessage = "Header existence validation failed: " + headerName + " not found";
            logger.error(errorMessage);
            extentReportManager.logFail(errorMessage);
            throw e;
        }
        
        return this;
    }
    
    /**
     * Validate response content type
     * @param expectedContentType Expected content type
     * @return ApiValidator for method chaining
     */
    public ApiValidator validateContentType(String expectedContentType) {
        try {
            String actualContentType = response.getContentType();
            assertThat(actualContentType)
                    .as("Content type validation")
                    .contains(expectedContentType);
            
            logger.info("Content type validation passed: {}", actualContentType);
            extentReportManager.logPass("Content type validation passed: " + actualContentType);
            
        } catch (AssertionError e) {
            String errorMessage = String.format("Content type validation failed: Expected=%s, Actual=%s", 
                                               expectedContentType, response.getContentType());
            logger.error(errorMessage);
            extentReportManager.logFail(errorMessage);
            throw e;
        }
        
        return this;
    }
    
    /**
     * Validate JSON response field value
     * @param jsonPath JSON path expression
     * @param expectedValue Expected value
     * @return ApiValidator for method chaining
     */
    public ApiValidator validateJsonField(String jsonPath, Object expectedValue) {
        try {
            Object actualValue = response.jsonPath().get(jsonPath);
            assertThat(actualValue)
                    .as("JSON field validation for: " + jsonPath)
                    .isEqualTo(expectedValue);
            
            logger.info("JSON field validation passed: {}={}", jsonPath, actualValue);
            extentReportManager.logPass("JSON field validation passed: " + jsonPath + "=" + actualValue);
            
        } catch (AssertionError e) {
            String errorMessage = String.format("JSON field validation failed: %s. Expected=%s, Actual=%s", 
                                               jsonPath, expectedValue, response.jsonPath().get(jsonPath));
            logger.error(errorMessage);
            extentReportManager.logFail(errorMessage);
            throw e;
        }
        
        return this;
    }
    
    /**
     * Validate JSON field exists
     * @param jsonPath JSON path expression
     * @return ApiValidator for method chaining
     */
    public ApiValidator validateJsonFieldExists(String jsonPath) {
        try {
            Object value = response.jsonPath().get(jsonPath);
            assertThat(value)
                    .as("JSON field existence validation for: " + jsonPath)
                    .isNotNull();
            
            logger.info("JSON field existence validation passed: {}", jsonPath);
            extentReportManager.logPass("JSON field existence validation passed: " + jsonPath);
            
        } catch (AssertionError e) {
            String errorMessage = "JSON field existence validation failed: " + jsonPath + " not found";
            logger.error(errorMessage);
            extentReportManager.logFail(errorMessage);
            throw e;
        }
        
        return this;
    }
    
    /**
     * Validate JSON array size
     * @param jsonPath JSON path to array
     * @param expectedSize Expected array size
     * @return ApiValidator for method chaining
     */
    public ApiValidator validateJsonArraySize(String jsonPath, int expectedSize) {
        try {
            List<?> jsonArray = response.jsonPath().getList(jsonPath);
            assertThat(jsonArray)
                    .as("JSON array size validation for: " + jsonPath)
                    .hasSize(expectedSize);
            
            logger.info("JSON array size validation passed: {}={}", jsonPath, jsonArray.size());
            extentReportManager.logPass("JSON array size validation passed: " + jsonPath + "=" + jsonArray.size());
            
        } catch (AssertionError e) {
            List<?> jsonArray = response.jsonPath().getList(jsonPath);
            int actualSize = jsonArray != null ? jsonArray.size() : 0;
            String errorMessage = String.format("JSON array size validation failed: %s. Expected=%d, Actual=%d", 
                                               jsonPath, expectedSize, actualSize);
            logger.error(errorMessage);
            extentReportManager.logFail(errorMessage);
            throw e;
        }
        
        return this;
    }
    
    /**
     * Validate JSON array contains value
     * @param jsonPath JSON path to array
     * @param expectedValue Expected value in array
     * @return ApiValidator for method chaining
     */
    public ApiValidator validateJsonArrayContains(String jsonPath, Object expectedValue) {
        try {
            List<?> jsonArray = response.jsonPath().getList(jsonPath);
            assertThat(jsonArray)
                    .as("JSON array contains validation for: " + jsonPath)
                    .contains(expectedValue);
            
            logger.info("JSON array contains validation passed: {} contains {}", jsonPath, expectedValue);
            extentReportManager.logPass("JSON array contains validation passed: " + jsonPath + " contains " + expectedValue);
            
        } catch (AssertionError e) {
            String errorMessage = String.format("JSON array contains validation failed: %s does not contain %s", 
                                               jsonPath, expectedValue);
            logger.error(errorMessage);
            extentReportManager.logFail(errorMessage);
            throw e;
        }
        
        return this;
    }
    
    /**
     * Validate JSON schema
     * @param schemaFilePath Path to JSON schema file
     * @return ApiValidator for method chaining
     */
    public ApiValidator validateJsonSchema(String schemaFilePath) {
        try {
            response.then().assertThat().body(JsonSchemaValidator.matchesJsonSchema(new File(schemaFilePath)));
            
            logger.info("JSON schema validation passed: {}", schemaFilePath);
            extentReportManager.logPass("JSON schema validation passed: " + schemaFilePath);
            
        } catch (AssertionError e) {
            String errorMessage = "JSON schema validation failed: " + schemaFilePath;
            logger.error(errorMessage);
            extentReportManager.logFail(errorMessage);
            throw e;
        }
        
        return this;
    }
    
    /**
     * Validate response body contains text
     * @param expectedText Expected text in response body
     * @return ApiValidator for method chaining
     */
    public ApiValidator validateBodyContains(String expectedText) {
        try {
            String responseBody = response.getBody().asString();
            assertThat(responseBody)
                    .as("Response body contains validation")
                    .contains(expectedText);
            
            logger.info("Response body contains validation passed: '{}'", expectedText);
            extentReportManager.logPass("Response body contains validation passed: " + expectedText);
            
        } catch (AssertionError e) {
            String errorMessage = "Response body contains validation failed: '" + expectedText + "' not found";
            logger.error(errorMessage);
            extentReportManager.logFail(errorMessage);
            throw e;
        }
        
        return this;
    }
    
    /**
     * Validate response body is not empty
     * @return ApiValidator for method chaining
     */
    public ApiValidator validateBodyNotEmpty() {
        try {
            String responseBody = response.getBody().asString();
            assertThat(responseBody)
                    .as("Response body not empty validation")
                    .isNotEmpty();
            
            logger.info("Response body not empty validation passed");
            extentReportManager.logPass("Response body not empty validation passed");
            
        } catch (AssertionError e) {
            String errorMessage = "Response body not empty validation failed: response body is empty";
            logger.error(errorMessage);
            extentReportManager.logFail(errorMessage);
            throw e;
        }
        
        return this;
    }
    
    /**
     * Custom validation using lambda expression
     * @param validator Custom validator function
     * @param validationDescription Description of the validation
     * @return ApiValidator for method chaining
     */
    public ApiValidator customValidation(java.util.function.Predicate<Response> validator, String validationDescription) {
        try {
            boolean isValid = validator.test(response);
            assertThat(isValid)
                    .as(validationDescription)
                    .isTrue();
            
            logger.info("Custom validation passed: {}", validationDescription);
            extentReportManager.logPass("Custom validation passed: " + validationDescription);
            
        } catch (AssertionError e) {
            String errorMessage = "Custom validation failed: " + validationDescription;
            logger.error(errorMessage);
            extentReportManager.logFail(errorMessage);
            throw e;
        }
        
        return this;
    }
    
    /**
     * Extract value from JSON response
     * @param jsonPath JSON path expression
     * @return Extracted value
     */
    public <T> T extractValue(String jsonPath) {
        try {
            T value = response.jsonPath().get(jsonPath);
            logger.info("Value extracted from JSON path {}: {}", jsonPath, value);
            return value;
        } catch (Exception e) {
            logger.error("Failed to extract value from JSON path: {}", jsonPath, e);
            throw new RuntimeException("Failed to extract value from JSON path: " + jsonPath, e);
        }
    }
    
    /**
     * Get response as string
     * @return Response body as string
     */
    public String getResponseBody() {
        return response.getBody().asString();
    }
    
    /**
     * Get response as JSON object
     * @return JsonNode representing the response
     */
    public JsonNode getResponseAsJson() {
        try {
            return objectMapper.readTree(response.getBody().asString());
        } catch (Exception e) {
            logger.error("Failed to parse response as JSON", e);
            throw new RuntimeException("Failed to parse response as JSON", e);
        }
    }
    
    /**
     * Log response details for debugging
     * @return ApiValidator for method chaining
     */
    public ApiValidator logResponseDetails() {
        logger.info("Response Details:");
        logger.info("Status Code: {}", response.getStatusCode());
        logger.info("Response Time: {}ms", response.getTime());
        logger.info("Content Type: {}", response.getContentType());
        logger.info("Headers: {}", response.getHeaders());
        logger.info("Response Body: {}", response.getBody().asString());
        
        return this;
    }
}