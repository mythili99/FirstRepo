package com.automation.utils;

import com.automation.data.TestData;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * JSON data reader utility for reading test data from JSON files
 * Supports reading data into TestData objects and generic maps
 */
public class JsonDataReader {
    
    private static final Logger logger = LogManager.getLogger(JsonDataReader.class);
    private final ObjectMapper objectMapper;
    private final String filePath;
    
    public JsonDataReader(String filePath) {
        this.filePath = filePath;
        this.objectMapper = new ObjectMapper();
    }
    
    /**
     * Read all test data from JSON file
     * @return List of TestData objects
     */
    public List<TestData> readTestData() {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                logger.error("JSON file not found: {}", filePath);
                throw new RuntimeException("JSON file not found: " + filePath);
            }
            
            // Try to read as array of TestData objects
            List<TestData> testDataList = objectMapper.readValue(file, new TypeReference<List<TestData>>() {});
            logger.info("Read {} test data records from JSON file: {}", testDataList.size(), filePath);
            return testDataList;
            
        } catch (IOException e) {
            logger.error("Failed to read JSON file: {}", filePath, e);
            throw new RuntimeException("Unable to read JSON file: " + filePath, e);
        }
    }
    
    /**
     * Read test data for specific test case ID
     * @param testCaseId Test case ID to filter
     * @return TestData object or null if not found
     */
    public TestData readTestDataByTestCaseId(String testCaseId) {
        List<TestData> allTestData = readTestData();
        return allTestData.stream()
                .filter(testData -> testCaseId.equals(testData.getTestCaseId()))
                .findFirst()
                .orElse(null);
    }
    
    /**
     * Read test data with run flag enabled
     * @return List of TestData objects with runFlag = "Y" or "Yes"
     */
    public List<TestData> readEnabledTestData() {
        List<TestData> allTestData = readTestData();
        return allTestData.stream()
                .filter(testData -> isRunFlagEnabled(testData.getRunFlag()))
                .collect(ArrayList::new, (list, testData) -> list.add(testData), ArrayList::addAll);
    }
    
    /**
     * Read data as generic maps (for custom data structures)
     * @return List of maps representing each test data record
     */
    public List<Map<String, Object>> readDataAsMap() {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                logger.error("JSON file not found: {}", filePath);
                throw new RuntimeException("JSON file not found: " + filePath);
            }
            
            List<Map<String, Object>> dataList = objectMapper.readValue(file, 
                    new TypeReference<List<Map<String, Object>>>() {});
            logger.info("Read {} data records as maps from JSON file: {}", dataList.size(), filePath);
            return dataList;
            
        } catch (IOException e) {
            logger.error("Failed to read JSON file: {}", filePath, e);
            throw new RuntimeException("Unable to read JSON file: " + filePath, e);
        }
    }
    
    /**
     * Read specific section from JSON file
     * @param sectionName Name of the section to read
     * @return List of TestData objects from specific section
     */
    public List<TestData> readTestDataBySection(String sectionName) {
        try {
            File file = new File(filePath);
            JsonNode rootNode = objectMapper.readTree(file);
            JsonNode sectionNode = rootNode.get(sectionName);
            
            if (sectionNode == null) {
                logger.warn("Section '{}' not found in JSON file", sectionName);
                return new ArrayList<>();
            }
            
            List<TestData> testDataList = objectMapper.convertValue(sectionNode, 
                    new TypeReference<List<TestData>>() {});
            logger.info("Read {} test data records from section '{}' in JSON file", 
                       testDataList.size(), sectionName);
            return testDataList;
            
        } catch (IOException e) {
            logger.error("Failed to read section '{}' from JSON file: {}", sectionName, filePath, e);
            throw new RuntimeException("Unable to read section from JSON file: " + filePath, e);
        }
    }
    
    /**
     * Read single test data object from JSON file
     * @return Single TestData object
     */
    public TestData readSingleTestData() {
        try {
            File file = new File(filePath);
            TestData testData = objectMapper.readValue(file, TestData.class);
            logger.info("Read single test data record from JSON file: {}", filePath);
            return testData;
            
        } catch (IOException e) {
            logger.error("Failed to read single test data from JSON file: {}", filePath, e);
            throw new RuntimeException("Unable to read single test data from JSON file: " + filePath, e);
        }
    }
    
    /**
     * Read nested JSON data by path
     * @param jsonPath Path to the data (e.g., "users.admin", "testData.loginData")
     * @return JsonNode representing the data at the specified path
     */
    public JsonNode readDataByPath(String jsonPath) {
        try {
            File file = new File(filePath);
            JsonNode rootNode = objectMapper.readTree(file);
            
            String[] pathParts = jsonPath.split("\\.");
            JsonNode currentNode = rootNode;
            
            for (String part : pathParts) {
                currentNode = currentNode.get(part);
                if (currentNode == null) {
                    logger.warn("Path '{}' not found in JSON file", jsonPath);
                    return null;
                }
            }
            
            logger.info("Successfully read data from path '{}' in JSON file", jsonPath);
            return currentNode;
            
        } catch (IOException e) {
            logger.error("Failed to read data from path '{}' in JSON file: {}", jsonPath, filePath, e);
            throw new RuntimeException("Unable to read data from JSON path: " + jsonPath, e);
        }
    }
    
    /**
     * Convert JsonNode to TestData object
     * @param jsonNode JsonNode to convert
     * @return TestData object
     */
    public TestData convertToTestData(JsonNode jsonNode) {
        try {
            return objectMapper.convertValue(jsonNode, TestData.class);
        } catch (Exception e) {
            logger.error("Failed to convert JsonNode to TestData", e);
            throw new RuntimeException("Unable to convert JsonNode to TestData", e);
        }
    }
    
    /**
     * Convert JsonNode to Map
     * @param jsonNode JsonNode to convert
     * @return Map representation of JsonNode
     */
    public Map<String, Object> convertToMap(JsonNode jsonNode) {
        try {
            return objectMapper.convertValue(jsonNode, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            logger.error("Failed to convert JsonNode to Map", e);
            throw new RuntimeException("Unable to convert JsonNode to Map", e);
        }
    }
    
    /**
     * Check if run flag is enabled
     * @param runFlag Run flag value
     * @return true if enabled
     */
    private boolean isRunFlagEnabled(String runFlag) {
        return runFlag != null && 
               (runFlag.equalsIgnoreCase("Y") || 
                runFlag.equalsIgnoreCase("Yes") || 
                runFlag.equalsIgnoreCase("TRUE") || 
                runFlag.equals("1"));
    }
    
    /**
     * Check if JSON file exists
     * @return true if file exists
     */
    public boolean fileExists() {
        return new File(filePath).exists();
    }
}