package com.automation.utils;

import com.automation.data.TestData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Excel reader utility for reading test data from Excel files
 * Supports reading data into TestData objects and generic maps
 */
public class ExcelReader {
    
    private static final Logger logger = LogManager.getLogger(ExcelReader.class);
    private Workbook workbook;
    private String filePath;
    
    public ExcelReader(String filePath) {
        this.filePath = filePath;
        try {
            FileInputStream fis = new FileInputStream(filePath);
            workbook = new XSSFWorkbook(fis);
            logger.info("Excel file loaded successfully: {}", filePath);
        } catch (IOException e) {
            logger.error("Failed to load Excel file: {}", filePath, e);
            throw new RuntimeException("Unable to read Excel file: " + filePath, e);
        }
    }
    
    /**
     * Read all test data from specified sheet
     * @param sheetName Name of the sheet
     * @return List of TestData objects
     */
    public List<TestData> readTestData(String sheetName) {
        List<TestData> testDataList = new ArrayList<>();
        Sheet sheet = workbook.getSheet(sheetName);
        
        if (sheet == null) {
            logger.error("Sheet '{}' not found in Excel file", sheetName);
            throw new RuntimeException("Sheet not found: " + sheetName);
        }
        
        // Get header row to map column names
        Row headerRow = sheet.getRow(0);
        Map<String, Integer> columnMap = createColumnMap(headerRow);
        
        // Read data rows (skip header)
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row != null && !isRowEmpty(row)) {
                TestData testData = createTestDataFromRow(row, columnMap);
                testDataList.add(testData);
            }
        }
        
        logger.info("Read {} test data records from sheet '{}'", testDataList.size(), sheetName);
        return testDataList;
    }
    
    /**
     * Read test data for specific test case ID
     * @param sheetName Name of the sheet
     * @param testCaseId Test case ID to filter
     * @return TestData object or null if not found
     */
    public TestData readTestDataByTestCaseId(String sheetName, String testCaseId) {
        List<TestData> allTestData = readTestData(sheetName);
        return allTestData.stream()
                .filter(testData -> testCaseId.equals(testData.getTestCaseId()))
                .findFirst()
                .orElse(null);
    }
    
    /**
     * Read test data with run flag enabled
     * @param sheetName Name of the sheet
     * @return List of TestData objects with runFlag = "Y" or "Yes"
     */
    public List<TestData> readEnabledTestData(String sheetName) {
        List<TestData> allTestData = readTestData(sheetName);
        return allTestData.stream()
                .filter(testData -> isRunFlagEnabled(testData.getRunFlag()))
                .collect(ArrayList::new, (list, testData) -> list.add(testData), ArrayList::addAll);
    }
    
    /**
     * Read data as generic map (for custom data structures)
     * @param sheetName Name of the sheet
     * @return List of maps representing each row
     */
    public List<Map<String, String>> readDataAsMap(String sheetName) {
        List<Map<String, String>> dataList = new ArrayList<>();
        Sheet sheet = workbook.getSheet(sheetName);
        
        if (sheet == null) {
            logger.error("Sheet '{}' not found in Excel file", sheetName);
            throw new RuntimeException("Sheet not found: " + sheetName);
        }
        
        // Get header row
        Row headerRow = sheet.getRow(0);
        List<String> headers = new ArrayList<>();
        for (Cell cell : headerRow) {
            headers.add(getCellValueAsString(cell));
        }
        
        // Read data rows
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row != null && !isRowEmpty(row)) {
                Map<String, String> rowData = new HashMap<>();
                for (int j = 0; j < headers.size(); j++) {
                    Cell cell = row.getCell(j);
                    String value = getCellValueAsString(cell);
                    rowData.put(headers.get(j), value);
                }
                dataList.add(rowData);
            }
        }
        
        logger.info("Read {} data records as maps from sheet '{}'", dataList.size(), sheetName);
        return dataList;
    }
    
    /**
     * Get cell value as string regardless of cell type
     * @param cell Excel cell
     * @return String value of the cell
     */
    public String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }
        
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    // Handle both integer and decimal numbers
                    double numericValue = cell.getNumericCellValue();
                    if (numericValue == Math.floor(numericValue)) {
                        return String.valueOf((long) numericValue);
                    } else {
                        return String.valueOf(numericValue);
                    }
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            case BLANK:
            case _NONE:
            default:
                return "";
        }
    }
    
    /**
     * Create column name to index mapping
     * @param headerRow Header row
     * @return Map of column names to indices
     */
    private Map<String, Integer> createColumnMap(Row headerRow) {
        Map<String, Integer> columnMap = new HashMap<>();
        for (Cell cell : headerRow) {
            String columnName = getCellValueAsString(cell).toLowerCase();
            columnMap.put(columnName, cell.getColumnIndex());
        }
        return columnMap;
    }
    
    /**
     * Create TestData object from Excel row
     * @param row Excel row
     * @param columnMap Column mapping
     * @return TestData object
     */
    private TestData createTestDataFromRow(Row row, Map<String, Integer> columnMap) {
        TestData testData = new TestData();
        
        // Map Excel columns to TestData fields
        testData.setTestCaseId(getCellValue(row, columnMap, "testcaseid"));
        testData.setUsername(getCellValue(row, columnMap, "username"));
        testData.setPassword(getCellValue(row, columnMap, "password"));
        testData.setEmail(getCellValue(row, columnMap, "email"));
        testData.setFirstName(getCellValue(row, columnMap, "firstname"));
        testData.setLastName(getCellValue(row, columnMap, "lastname"));
        testData.setPhoneNumber(getCellValue(row, columnMap, "phonenumber"));
        testData.setAddress(getCellValue(row, columnMap, "address"));
        testData.setCity(getCellValue(row, columnMap, "city"));
        testData.setZipCode(getCellValue(row, columnMap, "zipcode"));
        testData.setCountry(getCellValue(row, columnMap, "country"));
        testData.setProductName(getCellValue(row, columnMap, "productname"));
        testData.setProductPrice(getCellValue(row, columnMap, "productprice"));
        testData.setQuantity(getCellValue(row, columnMap, "quantity"));
        testData.setSearchTerm(getCellValue(row, columnMap, "searchterm"));
        testData.setExpectedResult(getCellValue(row, columnMap, "expectedresult"));
        testData.setTestDescription(getCellValue(row, columnMap, "testdescription"));
        testData.setEnvironment(getCellValue(row, columnMap, "environment"));
        testData.setRunFlag(getCellValue(row, columnMap, "runflag"));
        
        return testData;
    }
    
    /**
     * Get cell value by column name
     * @param row Excel row
     * @param columnMap Column mapping
     * @param columnName Column name
     * @return Cell value as string
     */
    private String getCellValue(Row row, Map<String, Integer> columnMap, String columnName) {
        Integer columnIndex = columnMap.get(columnName.toLowerCase());
        if (columnIndex != null) {
            Cell cell = row.getCell(columnIndex);
            return getCellValueAsString(cell);
        }
        return "";
    }
    
    /**
     * Check if row is empty
     * @param row Excel row
     * @return true if row is empty
     */
    private boolean isRowEmpty(Row row) {
        if (row == null) {
            return true;
        }
        
        for (int i = row.getFirstCellNum(); i < row.getLastCellNum(); i++) {
            Cell cell = row.getCell(i);
            if (cell != null && !getCellValueAsString(cell).isEmpty()) {
                return false;
            }
        }
        return true;
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
     * Close the workbook
     */
    public void close() {
        try {
            if (workbook != null) {
                workbook.close();
                logger.info("Excel workbook closed successfully");
            }
        } catch (IOException e) {
            logger.error("Error closing Excel workbook", e);
        }
    }
}