package com.automation.data;

import com.automation.core.ConfigManager;
import com.automation.core.LoggerManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.*;

/**
 * Test Data Manager for handling Excel, JSON, POJO, and Database data
 */
public class TestDataManager {
    private static final Logger logger = LoggerManager.getInstance().getLogger(TestDataManager.class);
    private static TestDataManager instance;
    private final ConfigManager configManager;
    private final ObjectMapper objectMapper;
    private Connection dbConnection;

    private TestDataManager() {
        configManager = ConfigManager.getInstance();
        objectMapper = new ObjectMapper();
    }

    public static TestDataManager getInstance() {
        if (instance == null) {
            instance = new TestDataManager();
        }
        return instance;
    }

    // ===========================================
    // EXCEL DATA MANAGEMENT
    // ===========================================

    public List<Map<String, String>> readExcelData(String sheetName) {
        String excelPath = configManager.getProperty("test.data.path") + "/" + 
                          configManager.getProperty("excel.file.name");
        return readExcelData(excelPath, sheetName);
    }

    public List<Map<String, String>> readExcelData(String filePath, String sheetName) {
        List<Map<String, String>> data = new ArrayList<>();
        
        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {
            
            Sheet sheet = workbook.getSheet(sheetName);
            if (sheet == null) {
                throw new RuntimeException("Sheet not found: " + sheetName);
            }

            Row headerRow = sheet.getRow(0);
            List<String> headers = new ArrayList<>();
            
            for (Cell cell : headerRow) {
                headers.add(cell.getStringCellValue());
            }

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row != null) {
                    Map<String, String> rowData = new HashMap<>();
                    for (int j = 0; j < headers.size(); j++) {
                        Cell cell = row.getCell(j);
                        String value = getCellValueAsString(cell);
                        rowData.put(headers.get(j), value);
                    }
                    data.add(rowData);
                }
            }
            
            logger.info("Read {} rows from Excel sheet: {}", data.size(), sheetName);
            
        } catch (IOException e) {
            logger.error("Error reading Excel file: {}", filePath, e);
            throw new RuntimeException("Failed to read Excel data", e);
        }
        
        return data;
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }
        
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    return String.valueOf((int) cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }

    // ===========================================
    // JSON DATA MANAGEMENT
    // ===========================================

    public <T> T readJsonData(String fileName, Class<T> clazz) {
        String jsonPath = configManager.getProperty("test.data.path") + "/" + fileName;
        return readJsonDataFromPath(jsonPath, clazz);
    }

    public <T> T readJsonDataFromPath(String filePath, Class<T> clazz) {
        try {
            T data = objectMapper.readValue(new File(filePath), clazz);
            logger.info("Read JSON data from: {}", filePath);
            return data;
        } catch (IOException e) {
            logger.error("Error reading JSON file: {}", filePath, e);
            throw new RuntimeException("Failed to read JSON data", e);
        }
    }

    public Map<String, Object> readJsonAsMap(String fileName) {
        String jsonPath = configManager.getProperty("test.data.path") + "/" + fileName;
        return readJsonAsMapFromPath(jsonPath);
    }

    public Map<String, Object> readJsonAsMapFromPath(String filePath) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> data = objectMapper.readValue(new File(filePath), Map.class);
            logger.info("Read JSON data as Map from: {}", filePath);
            return data;
        } catch (IOException e) {
            logger.error("Error reading JSON file: {}", filePath, e);
            throw new RuntimeException("Failed to read JSON data", e);
        }
    }

    public void writeJsonData(String fileName, Object data) {
        String jsonPath = configManager.getProperty("test.data.path") + "/" + fileName;
        writeJsonDataToPath(jsonPath, data);
    }

    public void writeJsonDataToPath(String filePath, Object data) {
        try {
            objectMapper.writeValue(new File(filePath), data);
            logger.info("Written JSON data to: {}", filePath);
        } catch (IOException e) {
            logger.error("Error writing JSON file: {}", filePath, e);
            throw new RuntimeException("Failed to write JSON data", e);
        }
    }

    // ===========================================
    // DATABASE DATA MANAGEMENT
    // ===========================================

    public Connection getDatabaseConnection() {
        if (dbConnection == null || isConnectionClosed()) {
            dbConnection = createDatabaseConnection();
        }
        return dbConnection;
    }

    private Connection createDatabaseConnection() {
        try {
            String url = String.format("jdbc:mysql://%s:%s/%s",
                    configManager.getProperty("db.host"),
                    configManager.getProperty("db.port"),
                    configManager.getProperty("db.name"));
            
            return DriverManager.getConnection(url,
                    configManager.getProperty("db.username"),
                    configManager.getProperty("db.password"));
                    
        } catch (SQLException e) {
            logger.error("Error creating database connection", e);
            throw new RuntimeException("Failed to create database connection", e);
        }
    }

    private boolean isConnectionClosed() {
        try {
            return dbConnection == null || dbConnection.isClosed();
        } catch (SQLException e) {
            return true;
        }
    }

    public List<Map<String, Object>> executeQuery(String query) {
        List<Map<String, Object>> results = new ArrayList<>();
        
        try (Connection conn = getDatabaseConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    row.put(metaData.getColumnName(i), rs.getObject(i));
                }
                results.add(row);
            }
            
            logger.info("Executed query and returned {} rows", results.size());
            
        } catch (SQLException e) {
            logger.error("Error executing database query", e);
            throw new RuntimeException("Failed to execute database query", e);
        }
        
        return results;
    }

    public int executeUpdate(String query) {
        try (Connection conn = getDatabaseConnection();
             Statement stmt = conn.createStatement()) {
            
            int affectedRows = stmt.executeUpdate(query);
            logger.info("Executed update query, affected {} rows", affectedRows);
            return affectedRows;
            
        } catch (SQLException e) {
            logger.error("Error executing database update", e);
            throw new RuntimeException("Failed to execute database update", e);
        }
    }

    public void closeDatabaseConnection() {
        if (dbConnection != null) {
            try {
                dbConnection.close();
                logger.info("Database connection closed");
            } catch (SQLException e) {
                logger.error("Error closing database connection", e);
            } finally {
                dbConnection = null;
            }
        }
    }

    // ===========================================
    // UTILITY METHODS
    // ===========================================

    public Map<String, String> getTestData(String dataKey) {
        // Try to get data from properties first
        String value = configManager.getProperty(dataKey);
        if (value != null) {
            Map<String, String> data = new HashMap<>();
            data.put("value", value);
            return data;
        }
        
        // If not found in properties, return empty map
        return new HashMap<>();
    }

    public String getRandomString(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    public String getRandomEmail() {
        return getRandomString(8) + "@test.com";
    }
}