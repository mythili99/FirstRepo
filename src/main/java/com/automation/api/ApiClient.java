package com.automation.api;

import com.automation.utils.ConfigReader;
import com.automation.utils.LoggerManager;
import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * RestAssured API client for making HTTP requests
 * Provides reusable methods for common API operations
 */
public class ApiClient {
    
    private static final Logger logger = LogManager.getLogger(ApiClient.class);
    private static ApiClient instance;
    private final ConfigReader configReader;
    private final LoggerManager loggerManager;
    private String baseUri;
    private RequestSpecification requestSpec;
    
    private ApiClient() {
        this.configReader = ConfigReader.getInstance();
        this.loggerManager = LoggerManager.getInstance();
        initializeClient();
    }
    
    public static ApiClient getInstance() {
        if (instance == null) {
            synchronized (ApiClient.class) {
                if (instance == null) {
                    instance = new ApiClient();
                }
            }
        }
        return instance;
    }
    
    /**
     * Initialize RestAssured client with configuration
     */
    private void initializeClient() {
        baseUri = configReader.getApiBaseUrl();
        
        // Configure RestAssured
        RestAssured.config = RestAssuredConfig.config()
                .encoderConfig(EncoderConfig.encoderConfig()
                        .encodeContentTypeAs("application/json", ContentType.TEXT));
        
        // Create base request specification
        requestSpec = RestAssured.given()
                .baseUri(baseUri)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .relaxedHTTPSValidation()
                .config(RestAssuredConfig.config()
                        .httpClient(io.restassured.config.HttpClientConfig.httpClientConfig()
                                .setParam("http.connection.timeout", configReader.getIntProperty("api.timeout", 30000))
                                .setParam("http.socket.timeout", configReader.getIntProperty("api.timeout", 30000))));
        
        logger.info("API client initialized with base URI: {}", baseUri);
    }
    
    /**
     * Create a new request specification
     * @return RequestSpecification
     */
    public RequestSpecification createRequest() {
        return RestAssured.given(requestSpec);
    }
    
    /**
     * Create a new request specification with headers
     * @param headers Map of headers
     * @return RequestSpecification
     */
    public RequestSpecification createRequest(Map<String, String> headers) {
        RequestSpecification request = createRequest();
        if (headers != null && !headers.isEmpty()) {
            request.headers(headers);
        }
        return request;
    }
    
    /**
     * Create a new request specification with authentication token
     * @param token Bearer token
     * @return RequestSpecification
     */
    public RequestSpecification createRequestWithAuth(String token) {
        return createRequest().auth().oauth2(token);
    }
    
    /**
     * Create a new request specification with basic authentication
     * @param username Username
     * @param password Password
     * @return RequestSpecification
     */
    public RequestSpecification createRequestWithBasicAuth(String username, String password) {
        return createRequest().auth().basic(username, password);
    }
    
    /**
     * Perform GET request
     * @param endpoint API endpoint
     * @return Response
     */
    public Response get(String endpoint) {
        long startTime = System.currentTimeMillis();
        
        try {
            Response response = createRequest()
                    .when()
                    .get(endpoint);
            
            long responseTime = System.currentTimeMillis() - startTime;
            logApiRequest("GET", baseUri + endpoint, null, response.getStatusCode(), responseTime);
            
            return response;
            
        } catch (Exception e) {
            logger.error("GET request failed for endpoint: {}", endpoint, e);
            throw new RuntimeException("GET request failed: " + endpoint, e);
        }
    }
    
    /**
     * Perform GET request with query parameters
     * @param endpoint API endpoint
     * @param queryParams Query parameters
     * @return Response
     */
    public Response get(String endpoint, Map<String, Object> queryParams) {
        long startTime = System.currentTimeMillis();
        
        try {
            RequestSpecification request = createRequest();
            if (queryParams != null && !queryParams.isEmpty()) {
                request.queryParams(queryParams);
            }
            
            Response response = request
                    .when()
                    .get(endpoint);
            
            long responseTime = System.currentTimeMillis() - startTime;
            logApiRequest("GET", baseUri + endpoint, null, response.getStatusCode(), responseTime);
            
            return response;
            
        } catch (Exception e) {
            logger.error("GET request failed for endpoint: {}", endpoint, e);
            throw new RuntimeException("GET request failed: " + endpoint, e);
        }
    }
    
    /**
     * Perform POST request
     * @param endpoint API endpoint
     * @param requestBody Request body
     * @return Response
     */
    public Response post(String endpoint, Object requestBody) {
        long startTime = System.currentTimeMillis();
        
        try {
            Response response = createRequest()
                    .body(requestBody)
                    .when()
                    .post(endpoint);
            
            long responseTime = System.currentTimeMillis() - startTime;
            logApiRequest("POST", baseUri + endpoint, requestBody.toString(), response.getStatusCode(), responseTime);
            
            return response;
            
        } catch (Exception e) {
            logger.error("POST request failed for endpoint: {}", endpoint, e);
            throw new RuntimeException("POST request failed: " + endpoint, e);
        }
    }
    
    /**
     * Perform POST request with headers
     * @param endpoint API endpoint
     * @param requestBody Request body
     * @param headers Headers
     * @return Response
     */
    public Response post(String endpoint, Object requestBody, Map<String, String> headers) {
        long startTime = System.currentTimeMillis();
        
        try {
            Response response = createRequest(headers)
                    .body(requestBody)
                    .when()
                    .post(endpoint);
            
            long responseTime = System.currentTimeMillis() - startTime;
            logApiRequest("POST", baseUri + endpoint, requestBody.toString(), response.getStatusCode(), responseTime);
            
            return response;
            
        } catch (Exception e) {
            logger.error("POST request failed for endpoint: {}", endpoint, e);
            throw new RuntimeException("POST request failed: " + endpoint, e);
        }
    }
    
    /**
     * Perform PUT request
     * @param endpoint API endpoint
     * @param requestBody Request body
     * @return Response
     */
    public Response put(String endpoint, Object requestBody) {
        long startTime = System.currentTimeMillis();
        
        try {
            Response response = createRequest()
                    .body(requestBody)
                    .when()
                    .put(endpoint);
            
            long responseTime = System.currentTimeMillis() - startTime;
            logApiRequest("PUT", baseUri + endpoint, requestBody.toString(), response.getStatusCode(), responseTime);
            
            return response;
            
        } catch (Exception e) {
            logger.error("PUT request failed for endpoint: {}", endpoint, e);
            throw new RuntimeException("PUT request failed: " + endpoint, e);
        }
    }
    
    /**
     * Perform PATCH request
     * @param endpoint API endpoint
     * @param requestBody Request body
     * @return Response
     */
    public Response patch(String endpoint, Object requestBody) {
        long startTime = System.currentTimeMillis();
        
        try {
            Response response = createRequest()
                    .body(requestBody)
                    .when()
                    .patch(endpoint);
            
            long responseTime = System.currentTimeMillis() - startTime;
            logApiRequest("PATCH", baseUri + endpoint, requestBody.toString(), response.getStatusCode(), responseTime);
            
            return response;
            
        } catch (Exception e) {
            logger.error("PATCH request failed for endpoint: {}", endpoint, e);
            throw new RuntimeException("PATCH request failed: " + endpoint, e);
        }
    }
    
    /**
     * Perform DELETE request
     * @param endpoint API endpoint
     * @return Response
     */
    public Response delete(String endpoint) {
        long startTime = System.currentTimeMillis();
        
        try {
            Response response = createRequest()
                    .when()
                    .delete(endpoint);
            
            long responseTime = System.currentTimeMillis() - startTime;
            logApiRequest("DELETE", baseUri + endpoint, null, response.getStatusCode(), responseTime);
            
            return response;
            
        } catch (Exception e) {
            logger.error("DELETE request failed for endpoint: {}", endpoint, e);
            throw new RuntimeException("DELETE request failed: " + endpoint, e);
        }
    }
    
    /**
     * Perform multipart file upload
     * @param endpoint API endpoint
     * @param filePath Path to file
     * @param fileParamName Parameter name for file
     * @return Response
     */
    public Response uploadFile(String endpoint, String filePath, String fileParamName) {
        long startTime = System.currentTimeMillis();
        
        try {
            Response response = RestAssured.given()
                    .baseUri(baseUri)
                    .multiPart(fileParamName, new java.io.File(filePath))
                    .when()
                    .post(endpoint);
            
            long responseTime = System.currentTimeMillis() - startTime;
            logApiRequest("POST (File Upload)", baseUri + endpoint, "File: " + filePath, response.getStatusCode(), responseTime);
            
            return response;
            
        } catch (Exception e) {
            logger.error("File upload failed for endpoint: {}", endpoint, e);
            throw new RuntimeException("File upload failed: " + endpoint, e);
        }
    }
    
    /**
     * Set base URI for requests
     * @param baseUri Base URI
     */
    public void setBaseUri(String baseUri) {
        this.baseUri = baseUri;
        RestAssured.baseURI = baseUri;
        logger.info("Base URI updated to: {}", baseUri);
    }
    
    /**
     * Get current base URI
     * @return Base URI
     */
    public String getBaseUri() {
        return baseUri;
    }
    
    /**
     * Log API request details
     */
    private void logApiRequest(String method, String url, String requestBody, int statusCode, long responseTime) {
        loggerManager.logApiRequest(method, url, statusCode, responseTime);
        
        StringBuilder logMessage = new StringBuilder();
        logMessage.append("\n").append("=".repeat(80))
                  .append("\nAPI REQUEST DETAILS:")
                  .append("\nMethod: ").append(method)
                  .append("\nURL: ").append(url)
                  .append("\nStatus Code: ").append(statusCode)
                  .append("\nResponse Time: ").append(responseTime).append("ms");
        
        if (requestBody != null && !requestBody.isEmpty()) {
            logMessage.append("\nRequest Body: ").append(requestBody);
        }
        
        logMessage.append("\n").append("=".repeat(80));
        
        if (statusCode >= 200 && statusCode < 300) {
            logger.info(logMessage.toString());
        } else {
            logger.error(logMessage.toString());
        }
    }
}