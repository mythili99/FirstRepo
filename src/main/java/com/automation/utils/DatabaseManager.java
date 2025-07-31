package com.automation.utils;

import com.automation.data.TestData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Database manager utility for reading test data from database
 * Supports connection management and data retrieval
 */
public class DatabaseManager {
    
    private static final Logger logger = LogManager.getLogger(DatabaseManager.class);
    private static DatabaseManager instance;
    private final ConfigReader configReader;
    private Connection connection;
    
    private DatabaseManager() {
        this.configReader = ConfigReader.getInstance();
    }
    
    public static DatabaseManager getInstance() {
        if (instance == null) {
            synchronized (DatabaseManager.class) {
                if (instance == null) {
                    instance = new DatabaseManager();
                }
            }
        }
        return instance;
    }
    
    /**
     * Establish database connection
     */
    public void connect() {
        try {
            if (connection == null || connection.isClosed()) {
                String url = configReader.getDatabaseUrl();
                String username = configReader.getDatabaseUsername();
                String password = configReader.getDatabasePassword();
                
                if (url == null || username == null || password == null) {
                    throw new RuntimeException("Database configuration not found in properties file");
                }
                
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(url, username, password);
                connection.setAutoCommit(true);
                
                logger.info("Database connection established successfully");
            }
        } catch (Exception e) {
            logger.error("Failed to establish database connection", e);
            throw new RuntimeException("Database connection failed", e);
        }
    }
    
    /**
     * Close database connection
     */
    public void disconnect() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                logger.info("Database connection closed successfully");
            }
        } catch (SQLException e) {
            logger.error("Error closing database connection", e);
        }
    }
    
    /**
     * Execute SELECT query and return results as TestData objects
     * @param query SQL SELECT query
     * @return List of TestData objects
     */
    public List<TestData> executeQueryForTestData(String query) {
        List<TestData> testDataList = new ArrayList<>();
        
        try {
            connect();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            ResultSetMetaData metaData = resultSet.getMetaData();
            
            while (resultSet.next()) {
                TestData testData = mapResultSetToTestData(resultSet, metaData);
                testDataList.add(testData);
            }
            
            resultSet.close();
            statement.close();
            logger.info("Executed query and retrieved {} test data records", testDataList.size());
            
        } catch (SQLException e) {
            logger.error("Error executing query: {}", query, e);
            throw new RuntimeException("Database query execution failed", e);
        }
        
        return testDataList;
    }
    
    /**
     * Execute SELECT query and return results as maps
     * @param query SQL SELECT query
     * @return List of maps representing each row
     */
    public List<Map<String, String>> executeQuery(String query) {
        List<Map<String, String>> resultList = new ArrayList<>();
        
        try {
            connect();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            
            while (resultSet.next()) {
                Map<String, String> row = new HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnName(i);
                    String value = resultSet.getString(i);
                    row.put(columnName, value != null ? value : "");
                }
                resultList.add(row);
            }
            
            resultSet.close();
            statement.close();
            logger.info("Executed query and retrieved {} records", resultList.size());
            
        } catch (SQLException e) {
            logger.error("Error executing query: {}", query, e);
            throw new RuntimeException("Database query execution failed", e);
        }
        
        return resultList;
    }
    
    /**
     * Execute UPDATE, INSERT, or DELETE query
     * @param query SQL query
     * @return Number of affected rows
     */
    public int executeUpdate(String query) {
        try {
            connect();
            Statement statement = connection.createStatement();
            int affectedRows = statement.executeUpdate(query);
            statement.close();
            
            logger.info("Executed update query, affected rows: {}", affectedRows);
            return affectedRows;
            
        } catch (SQLException e) {
            logger.error("Error executing update query: {}", query, e);
            throw new RuntimeException("Database update execution failed", e);
        }
    }
    
    /**
     * Execute prepared statement with parameters
     * @param query SQL query with placeholders
     * @param parameters Parameters to bind
     * @return List of maps representing results
     */
    public List<Map<String, String>> executePreparedQuery(String query, Object... parameters) {
        List<Map<String, String>> resultList = new ArrayList<>();
        
        try {
            connect();
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            
            // Set parameters
            for (int i = 0; i < parameters.length; i++) {
                preparedStatement.setObject(i + 1, parameters[i]);
            }
            
            ResultSet resultSet = preparedStatement.executeQuery();
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            
            while (resultSet.next()) {
                Map<String, String> row = new HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnName(i);
                    String value = resultSet.getString(i);
                    row.put(columnName, value != null ? value : "");
                }
                resultList.add(row);
            }
            
            resultSet.close();
            preparedStatement.close();
            logger.info("Executed prepared query and retrieved {} records", resultList.size());
            
        } catch (SQLException e) {
            logger.error("Error executing prepared query: {}", query, e);
            throw new RuntimeException("Database prepared query execution failed", e);
        }
        
        return resultList;
    }
    
    /**
     * Get test data by test case ID from database
     * @param testCaseId Test case ID
     * @return TestData object or null if not found
     */
    public TestData getTestDataByTestCaseId(String testCaseId) {
        String query = "SELECT * FROM test_data WHERE test_case_id = ?";
        List<Map<String, String>> results = executePreparedQuery(query, testCaseId);
        
        if (!results.isEmpty()) {
            return mapToTestData(results.get(0));
        }
        
        return null;
    }
    
    /**
     * Get all enabled test data from database
     * @return List of TestData objects with run_flag = 'Y'
     */
    public List<TestData> getEnabledTestData() {
        String query = "SELECT * FROM test_data WHERE run_flag = 'Y'";
        return executeQueryForTestData(query);
    }
    
    /**
     * Get test data by environment
     * @param environment Environment name
     * @return List of TestData objects for the specified environment
     */
    public List<TestData> getTestDataByEnvironment(String environment) {
        String query = "SELECT * FROM test_data WHERE environment = ?";
        List<Map<String, String>> results = executePreparedQuery(query, environment);
        
        List<TestData> testDataList = new ArrayList<>();
        for (Map<String, String> row : results) {
            testDataList.add(mapToTestData(row));
        }
        
        return testDataList;
    }
    
    /**
     * Map ResultSet to TestData object
     * @param resultSet ResultSet from database query
     * @param metaData ResultSet metadata
     * @return TestData object
     */
    private TestData mapResultSetToTestData(ResultSet resultSet, ResultSetMetaData metaData) throws SQLException {
        TestData testData = new TestData();
        
        // Map database columns to TestData fields
        for (int i = 1; i <= metaData.getColumnCount(); i++) {
            String columnName = metaData.getColumnName(i).toLowerCase();
            String value = resultSet.getString(i);
            
            mapFieldValue(testData, columnName, value);
        }
        
        return testData;
    }
    
    /**
     * Map database row to TestData object
     * @param row Database row as map
     * @return TestData object
     */
    private TestData mapToTestData(Map<String, String> row) {
        TestData testData = new TestData();
        
        for (Map.Entry<String, String> entry : row.entrySet()) {
            String columnName = entry.getKey().toLowerCase();
            String value = entry.getValue();
            
            mapFieldValue(testData, columnName, value);
        }
        
        return testData;
    }
    
    /**
     * Map field value to TestData object
     * @param testData TestData object
     * @param columnName Column name
     * @param value Field value
     */
    private void mapFieldValue(TestData testData, String columnName, String value) {
        if (value == null) value = "";
        
        switch (columnName) {
            case "test_case_id":
            case "testcaseid":
                testData.setTestCaseId(value);
                break;
            case "username":
                testData.setUsername(value);
                break;
            case "password":
                testData.setPassword(value);
                break;
            case "email":
                testData.setEmail(value);
                break;
            case "first_name":
            case "firstname":
                testData.setFirstName(value);
                break;
            case "last_name":
            case "lastname":
                testData.setLastName(value);
                break;
            case "phone_number":
            case "phonenumber":
                testData.setPhoneNumber(value);
                break;
            case "address":
                testData.setAddress(value);
                break;
            case "city":
                testData.setCity(value);
                break;
            case "zip_code":
            case "zipcode":
                testData.setZipCode(value);
                break;
            case "country":
                testData.setCountry(value);
                break;
            case "product_name":
            case "productname":
                testData.setProductName(value);
                break;
            case "product_price":
            case "productprice":
                testData.setProductPrice(value);
                break;
            case "quantity":
                testData.setQuantity(value);
                break;
            case "search_term":
            case "searchterm":
                testData.setSearchTerm(value);
                break;
            case "expected_result":
            case "expectedresult":
                testData.setExpectedResult(value);
                break;
            case "test_description":
            case "testdescription":
                testData.setTestDescription(value);
                break;
            case "environment":
                testData.setEnvironment(value);
                break;
            case "run_flag":
            case "runflag":
                testData.setRunFlag(value);
                break;
        }
    }
    
    /**
     * Check if database connection is alive
     * @return true if connection is alive
     */
    public boolean isConnectionAlive() {
        try {
            return connection != null && !connection.isClosed() && connection.isValid(5);
        } catch (SQLException e) {
            return false;
        }
    }
}