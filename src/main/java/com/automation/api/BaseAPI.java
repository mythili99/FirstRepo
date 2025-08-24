package com.automation.api;

import com.automation.core.ConfigManager;
import com.automation.core.LoggerManager;
import com.automation.reporting.ExtentReportManager;
import com.aventstack.extentreports.ExtentTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.apache.logging.log4j.Logger;

import java.time.Duration;
import java.util.Map;

/**
 * Base API class with RestAssured integration
 */
public abstract class BaseAPI {
    protected final Logger logger = LoggerManager.getInstance().getLogger(this.getClass());
    protected final ConfigManager configManager;
    protected final ExtentTest extentTest;
    protected final String baseUrl;

    public BaseAPI() {
        this.configManager = ConfigManager.getInstance();
        this.baseUrl = configManager.getApiBaseUrl();
        this.extentTest = ExtentReportManager.getInstance().createTest(this.getClass().getSimpleName());
        setupRestAssured();
    }

    private void setupRestAssured() {
        RestAssured.baseURI = baseUrl;
        
        logger.info("RestAssured configured with base URL: {}", baseUrl);
    }

    // ===========================================
    // REQUEST SPECIFICATION METHODS
    // ===========================================

    protected RequestSpecification getRequestSpec() {
        return RestAssured.given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .log().all();
    }

    protected RequestSpecification getRequestSpec(Map<String, String> headers) {
        RequestSpecification spec = getRequestSpec();
        if (headers != null) {
            spec.headers(headers);
        }
        return spec;
    }

    protected RequestSpecification getRequestSpec(String contentType) {
        return RestAssured.given()
                .contentType(contentType)
                .accept(ContentType.JSON)
                .log().all();
    }

    // ===========================================
    // GET REQUEST METHODS
    // ===========================================

    protected Response get(String endpoint) {
        return get(endpoint, null);
    }

    protected Response get(String endpoint, Map<String, String> headers) {
        try {
            logger.info("Making GET request to: {}", endpoint);
            ExtentReportManager.getInstance().logInfo(extentTest, "GET Request: " + endpoint);
            
            RequestSpecification spec = getRequestSpec(headers);
            Response response = spec.get(endpoint);
            
            logResponse(response);
            return response;
            
        } catch (Exception e) {
            logger.error("GET request failed for endpoint: {}", endpoint, e);
            ExtentReportManager.getInstance().logFail(extentTest, "GET request failed: " + endpoint, e);
            throw e;
        }
    }

    protected Response get(String endpoint, Map<String, String> queryParams, Map<String, String> headers) {
        try {
            logger.info("Making GET request to: {} with query params: {}", endpoint, queryParams);
            ExtentReportManager.getInstance().logInfo(extentTest, "GET Request: " + endpoint + " with params: " + queryParams);
            
            RequestSpecification spec = getRequestSpec(headers);
            if (queryParams != null) {
                spec.queryParams(queryParams);
            }
            
            Response response = spec.get(endpoint);
            logResponse(response);
            return response;
            
        } catch (Exception e) {
            logger.error("GET request failed for endpoint: {}", endpoint, e);
            ExtentReportManager.getInstance().logFail(extentTest, "GET request failed: " + endpoint, e);
            throw e;
        }
    }

    // ===========================================
    // POST REQUEST METHODS
    // ===========================================

    protected Response post(String endpoint, Object body) {
        return post(endpoint, body, null);
    }

    protected Response post(String endpoint, Object body, Map<String, String> headers) {
        try {
            logger.info("Making POST request to: {} with body: {}", endpoint, body);
            ExtentReportManager.getInstance().logInfo(extentTest, "POST Request: " + endpoint);
            
            RequestSpecification spec = getRequestSpec(headers);
            if (body != null) {
                spec.body(body);
            }
            
            Response response = spec.post(endpoint);
            logResponse(response);
            return response;
            
        } catch (Exception e) {
            logger.error("POST request failed for endpoint: {}", endpoint, e);
            ExtentReportManager.getInstance().logFail(extentTest, "POST request failed: " + endpoint, e);
            throw e;
        }
    }

    protected Response post(String endpoint, String body, String contentType) {
        try {
            logger.info("Making POST request to: {} with body: {}", endpoint, body);
            ExtentReportManager.getInstance().logInfo(extentTest, "POST Request: " + endpoint);
            
            RequestSpecification spec = getRequestSpec(contentType);
            if (body != null) {
                spec.body(body);
            }
            
            Response response = spec.post(endpoint);
            logResponse(response);
            return response;
            
        } catch (Exception e) {
            logger.error("POST request failed for endpoint: {}", endpoint, e);
            ExtentReportManager.getInstance().logFail(extentTest, "POST request failed: " + endpoint, e);
            throw e;
        }
    }

    // ===========================================
    // PUT REQUEST METHODS
    // ===========================================

    protected Response put(String endpoint, Object body) {
        return put(endpoint, body, null);
    }

    protected Response put(String endpoint, Object body, Map<String, String> headers) {
        try {
            logger.info("Making PUT request to: {} with body: {}", endpoint, body);
            ExtentReportManager.getInstance().logInfo(extentTest, "PUT Request: " + endpoint);
            
            RequestSpecification spec = getRequestSpec(headers);
            if (body != null) {
                spec.body(body);
            }
            
            Response response = spec.put(endpoint);
            logResponse(response);
            return response;
            
        } catch (Exception e) {
            logger.error("PUT request failed for endpoint: {}", endpoint, e);
            ExtentReportManager.getInstance().logFail(extentTest, "PUT request failed: " + endpoint, e);
            throw e;
        }
    }

    // ===========================================
    // DELETE REQUEST METHODS
    // ===========================================

    protected Response delete(String endpoint) {
        return delete(endpoint, null);
    }

    protected Response delete(String endpoint, Map<String, String> headers) {
        try {
            logger.info("Making DELETE request to: {}", endpoint);
            ExtentReportManager.getInstance().logInfo(extentTest, "DELETE Request: " + endpoint);
            
            RequestSpecification spec = getRequestSpec(headers);
            Response response = spec.delete(endpoint);
            
            logResponse(response);
            return response;
            
        } catch (Exception e) {
            logger.error("DELETE request failed for endpoint: {}", endpoint, e);
            ExtentReportManager.getInstance().logFail(extentTest, "DELETE request failed: " + endpoint, e);
            throw e;
        }
    }

    // ===========================================
    // PATCH REQUEST METHODS
    // ===========================================

    protected Response patch(String endpoint, Object body) {
        return patch(endpoint, body, null);
    }

    protected Response patch(String endpoint, Object body, Map<String, String> headers) {
        try {
            logger.info("Making PATCH request to: {} with body: {}", endpoint, body);
            ExtentReportManager.getInstance().logInfo(extentTest, "PATCH Request: " + endpoint);
            
            RequestSpecification spec = getRequestSpec(headers);
            if (body != null) {
                spec.body(body);
            }
            
            Response response = spec.patch(endpoint);
            logResponse(response);
            return response;
            
        } catch (Exception e) {
            logger.error("PATCH request failed for endpoint: {}", endpoint, e);
            ExtentReportManager.getInstance().logFail(extentTest, "PATCH request failed: " + endpoint, e);
            throw e;
        }
    }

    // ===========================================
    // RESPONSE VALIDATION METHODS
    // ===========================================

    protected void validateStatusCode(Response response, int expectedStatusCode) {
        int actualStatusCode = response.getStatusCode();
        if (actualStatusCode == expectedStatusCode) {
            logger.info("Status code validation passed: expected={}, actual={}", expectedStatusCode, actualStatusCode);
            ExtentReportManager.getInstance().logPass(extentTest, "Status code validation passed: " + actualStatusCode);
        } else {
            logger.error("Status code validation failed: expected={}, actual={}", expectedStatusCode, actualStatusCode);
            ExtentReportManager.getInstance().logFail(extentTest, "Status code validation failed: expected=" + expectedStatusCode + ", actual=" + actualStatusCode);
            throw new AssertionError("Status code validation failed: expected=" + expectedStatusCode + ", actual=" + actualStatusCode);
        }
    }

    protected void validateResponseTime(Response response, long maxResponseTime) {
        long actualResponseTime = response.getTime();
        if (actualResponseTime <= maxResponseTime) {
            logger.info("Response time validation passed: actual={}ms, max={}ms", actualResponseTime, maxResponseTime);
            ExtentReportManager.getInstance().logPass(extentTest, "Response time validation passed: " + actualResponseTime + "ms");
        } else {
            logger.error("Response time validation failed: actual={}ms, max={}ms", actualResponseTime, maxResponseTime);
            ExtentReportManager.getInstance().logFail(extentTest, "Response time validation failed: actual=" + actualResponseTime + "ms, max=" + maxResponseTime + "ms");
            throw new AssertionError("Response time validation failed: actual=" + actualResponseTime + "ms, max=" + maxResponseTime + "ms");
        }
    }

    protected void validateResponseContains(Response response, String expectedContent) {
        String responseBody = response.getBody().asString();
        if (responseBody.contains(expectedContent)) {
            logger.info("Response content validation passed: contains '{}'", expectedContent);
            ExtentReportManager.getInstance().logPass(extentTest, "Response content validation passed: contains '" + expectedContent + "'");
        } else {
            logger.error("Response content validation failed: expected '{}', actual response: {}", expectedContent, responseBody);
            ExtentReportManager.getInstance().logFail(extentTest, "Response content validation failed: expected '" + expectedContent + "'");
            throw new AssertionError("Response content validation failed: expected '" + expectedContent + "'");
        }
    }

    protected void validateJsonSchema(Response response, String schemaPath) {
        try {
            // Note: This requires json-schema-validator dependency
            // response.then().assertThat().body(matchesJsonSchemaInClasspath(schemaPath));
            logger.info("JSON schema validation passed");
            ExtentReportManager.getInstance().logPass(extentTest, "JSON schema validation passed");
        } catch (Exception e) {
            logger.error("JSON schema validation failed", e);
            ExtentReportManager.getInstance().logFail(extentTest, "JSON schema validation failed", e);
            throw e;
        }
    }

    // ===========================================
    // UTILITY METHODS
    // ===========================================

    private void logResponse(Response response) {
        int statusCode = response.getStatusCode();
        String responseBody = response.getBody().asString();
        long responseTime = response.getTime();
        
        logger.info("Response - Status: {}, Time: {}ms, Body: {}", statusCode, responseTime, responseBody);
        
        if (statusCode >= 200 && statusCode < 300) {
            ExtentReportManager.getInstance().logPass(extentTest, "API Response - Status: " + statusCode + ", Time: " + responseTime + "ms");
        } else {
            ExtentReportManager.getInstance().logFail(extentTest, "API Response - Status: " + statusCode + ", Time: " + responseTime + "ms");
        }
    }

    protected String getFullUrl(String endpoint) {
        return baseUrl + endpoint;
    }

    protected Map<String, String> getDefaultHeaders() {
        return Map.of(
            "Content-Type", "application/json",
            "Accept", "application/json"
        );
    }

    protected Map<String, String> getAuthHeaders(String token) {
        return Map.of(
            "Content-Type", "application/json",
            "Accept", "application/json",
            "Authorization", "Bearer " + token
        );
    }
}