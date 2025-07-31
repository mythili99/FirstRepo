package com.automation.utils;

import com.automation.constants.FrameworkConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Excel report manager for generating test execution results in Excel format
 * Provides functionality to create, update, and manage Excel test reports
 */
public class ExcelReportManager {
    
    private static final Logger logger = LogManager.getLogger(ExcelReportManager.class);
    private static ExcelReportManager instance;
    private Workbook workbook;
    private Sheet testResultsSheet;
    private String reportFilePath;
    private int currentRow = 1; // Start after header row
    
    private ExcelReportManager() {
        initializeReport();
    }
    
    public static ExcelReportManager getInstance() {
        if (instance == null) {
            synchronized (ExcelReportManager.class) {
                if (instance == null) {
                    instance = new ExcelReportManager();
                }
            }
        }
        return instance;
    }
    
    /**
     * Initialize Excel report with headers
     */
    private void initializeReport() {
        try {
            // Generate timestamp for report name
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
            reportFilePath = "reports/TestResults_" + timestamp + ".xlsx";
            
            workbook = new XSSFWorkbook();
            testResultsSheet = workbook.createSheet("Test Results");
            
            createHeaderRow();
            
            logger.info("Excel report initialized successfully: {}", reportFilePath);
            
        } catch (Exception e) {
            logger.error("Failed to initialize Excel report", e);
            throw new RuntimeException("Excel report initialization failed", e);
        }
    }
    
    /**
     * Create header row for test results
     */
    private void createHeaderRow() {
        Row headerRow = testResultsSheet.createRow(0);
        CellStyle headerStyle = createHeaderStyle();
        
        String[] headers = {
            "Test Case ID", "Test Name", "Test Description", "Category",
            "Start Time", "End Time", "Duration (ms)", "Status",
            "Browser", "Environment", "Error Message", "Screenshot Path"
        };
        
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
            testResultsSheet.autoSizeColumn(i);
        }
    }
    
    /**
     * Create header cell style
     */
    private CellStyle createHeaderStyle() {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        return style;
    }
    
    /**
     * Create cell style based on test status
     */
    private CellStyle createStatusStyle(String status) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        
        switch (status.toUpperCase()) {
            case "PASS":
                style.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
                font.setColor(IndexedColors.DARK_GREEN.getIndex());
                break;
            case "FAIL":
                style.setFillForegroundColor(IndexedColors.LIGHT_ORANGE.getIndex());
                font.setColor(IndexedColors.DARK_RED.getIndex());
                break;
            case "SKIP":
                style.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
                font.setColor(IndexedColors.DARK_YELLOW.getIndex());
                break;
            default:
                style.setFillForegroundColor(IndexedColors.LIGHT_GREY.getIndex());
                font.setColor(IndexedColors.BLACK.getIndex());
        }
        
        style.setFont(font);
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        
        return style;
    }
    
    /**
     * Add test result to Excel report
     */
    public synchronized void addTestResult(TestResult testResult) {
        try {
            Row row = testResultsSheet.createRow(currentRow++);
            CellStyle statusStyle = createStatusStyle(testResult.getStatus());
            
            // Test Case ID
            Cell cell0 = row.createCell(0);
            cell0.setCellValue(testResult.getTestCaseId());
            
            // Test Name
            Cell cell1 = row.createCell(1);
            cell1.setCellValue(testResult.getTestName());
            
            // Test Description
            Cell cell2 = row.createCell(2);
            cell2.setCellValue(testResult.getTestDescription());
            
            // Category
            Cell cell3 = row.createCell(3);
            cell3.setCellValue(testResult.getCategory());
            
            // Start Time
            Cell cell4 = row.createCell(4);
            cell4.setCellValue(testResult.getStartTime());
            
            // End Time
            Cell cell5 = row.createCell(5);
            cell5.setCellValue(testResult.getEndTime());
            
            // Duration
            Cell cell6 = row.createCell(6);
            cell6.setCellValue(testResult.getDuration());
            
            // Status
            Cell cell7 = row.createCell(7);
            cell7.setCellValue(testResult.getStatus());
            cell7.setCellStyle(statusStyle);
            
            // Browser
            Cell cell8 = row.createCell(8);
            cell8.setCellValue(testResult.getBrowser());
            
            // Environment
            Cell cell9 = row.createCell(9);
            cell9.setCellValue(testResult.getEnvironment());
            
            // Error Message
            Cell cell10 = row.createCell(10);
            cell10.setCellValue(testResult.getErrorMessage());
            
            // Screenshot Path
            Cell cell11 = row.createCell(11);
            cell11.setCellValue(testResult.getScreenshotPath());
            
            logger.debug("Added test result to Excel: {}", testResult.getTestName());
            
        } catch (Exception e) {
            logger.error("Failed to add test result to Excel: {}", testResult.getTestName(), e);
        }
    }
    
    /**
     * Add summary sheet with test execution statistics
     */
    public void addSummarySheet(TestExecutionSummary summary) {
        try {
            Sheet summarySheet = workbook.createSheet("Summary");
            CellStyle headerStyle = createHeaderStyle();
            
            // Create summary headers
            Row headerRow = summarySheet.createRow(0);
            String[] summaryHeaders = {"Metric", "Value"};
            
            for (int i = 0; i < summaryHeaders.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(summaryHeaders[i]);
                cell.setCellStyle(headerStyle);
            }
            
            // Add summary data
            addSummaryRow(summarySheet, 1, "Total Tests", String.valueOf(summary.getTotalTests()));
            addSummaryRow(summarySheet, 2, "Passed", String.valueOf(summary.getPassedTests()));
            addSummaryRow(summarySheet, 3, "Failed", String.valueOf(summary.getFailedTests()));
            addSummaryRow(summarySheet, 4, "Skipped", String.valueOf(summary.getSkippedTests()));
            addSummaryRow(summarySheet, 5, "Pass Rate", summary.getPassRate() + "%");
            addSummaryRow(summarySheet, 6, "Execution Time", summary.getTotalExecutionTime() + " ms");
            addSummaryRow(summarySheet, 7, "Start Time", summary.getExecutionStartTime());
            addSummaryRow(summarySheet, 8, "End Time", summary.getExecutionEndTime());
            addSummaryRow(summarySheet, 9, "Environment", summary.getEnvironment());
            addSummaryRow(summarySheet, 10, "Browser", summary.getBrowser());
            
            // Auto-size columns
            for (int i = 0; i < summaryHeaders.length; i++) {
                summarySheet.autoSizeColumn(i);
            }
            
            logger.info("Added summary sheet to Excel report");
            
        } catch (Exception e) {
            logger.error("Failed to add summary sheet to Excel report", e);
        }
    }
    
    /**
     * Add a row to summary sheet
     */
    private void addSummaryRow(Sheet sheet, int rowNum, String metric, String value) {
        Row row = sheet.createRow(rowNum);
        
        Cell metricCell = row.createCell(0);
        metricCell.setCellValue(metric);
        
        Cell valueCell = row.createCell(1);
        valueCell.setCellValue(value);
    }
    
    /**
     * Save Excel report to file
     */
    public synchronized void saveReport() {
        try (FileOutputStream outputStream = new FileOutputStream(reportFilePath)) {
            workbook.write(outputStream);
            logger.info("Excel report saved successfully: {}", reportFilePath);
        } catch (IOException e) {
            logger.error("Failed to save Excel report: {}", reportFilePath, e);
        }
    }
    
    /**
     * Close the workbook and save the report
     */
    public void closeReport() {
        try {
            saveReport();
            if (workbook != null) {
                workbook.close();
            }
            logger.info("Excel report closed successfully");
        } catch (IOException e) {
            logger.error("Error closing Excel report", e);
        }
    }
    
    /**
     * Get the report file path
     */
    public String getReportFilePath() {
        return reportFilePath;
    }
    
    /**
     * Inner class for test result data
     */
    public static class TestResult {
        private String testCaseId;
        private String testName;
        private String testDescription;
        private String category;
        private String startTime;
        private String endTime;
        private long duration;
        private String status;
        private String browser;
        private String environment;
        private String errorMessage;
        private String screenshotPath;
        
        // Constructor
        public TestResult(String testCaseId, String testName, String testDescription, 
                         String category, String startTime, String endTime, long duration,
                         String status, String browser, String environment, 
                         String errorMessage, String screenshotPath) {
            this.testCaseId = testCaseId;
            this.testName = testName;
            this.testDescription = testDescription;
            this.category = category;
            this.startTime = startTime;
            this.endTime = endTime;
            this.duration = duration;
            this.status = status;
            this.browser = browser;
            this.environment = environment;
            this.errorMessage = errorMessage;
            this.screenshotPath = screenshotPath;
        }
        
        // Getters
        public String getTestCaseId() { return testCaseId; }
        public String getTestName() { return testName; }
        public String getTestDescription() { return testDescription; }
        public String getCategory() { return category; }
        public String getStartTime() { return startTime; }
        public String getEndTime() { return endTime; }
        public long getDuration() { return duration; }
        public String getStatus() { return status; }
        public String getBrowser() { return browser; }
        public String getEnvironment() { return environment; }
        public String getErrorMessage() { return errorMessage; }
        public String getScreenshotPath() { return screenshotPath; }
    }
    
    /**
     * Inner class for test execution summary
     */
    public static class TestExecutionSummary {
        private int totalTests;
        private int passedTests;
        private int failedTests;
        private int skippedTests;
        private double passRate;
        private long totalExecutionTime;
        private String executionStartTime;
        private String executionEndTime;
        private String environment;
        private String browser;
        
        // Constructor
        public TestExecutionSummary(int totalTests, int passedTests, int failedTests, 
                                   int skippedTests, long totalExecutionTime,
                                   String executionStartTime, String executionEndTime,
                                   String environment, String browser) {
            this.totalTests = totalTests;
            this.passedTests = passedTests;
            this.failedTests = failedTests;
            this.skippedTests = skippedTests;
            this.totalExecutionTime = totalExecutionTime;
            this.executionStartTime = executionStartTime;
            this.executionEndTime = executionEndTime;
            this.environment = environment;
            this.browser = browser;
            this.passRate = totalTests > 0 ? (double) passedTests / totalTests * 100 : 0;
        }
        
        // Getters
        public int getTotalTests() { return totalTests; }
        public int getPassedTests() { return passedTests; }
        public int getFailedTests() { return failedTests; }
        public int getSkippedTests() { return skippedTests; }
        public double getPassRate() { return Math.round(passRate * 100.0) / 100.0; }
        public long getTotalExecutionTime() { return totalExecutionTime; }
        public String getExecutionStartTime() { return executionStartTime; }
        public String getExecutionEndTime() { return executionEndTime; }
        public String getEnvironment() { return environment; }
        public String getBrowser() { return browser; }
    }
}