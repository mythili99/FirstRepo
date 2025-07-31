package com.automation.listeners;

import com.automation.utils.ExtentReportManager;
import com.automation.utils.ExcelReportManager;
import com.automation.utils.LoggerManager;
import org.apache.logging.log4j.Logger;
import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * TestNG listener for test lifecycle management
 * Handles test suite and test method events for reporting
 */
public class TestListener implements ITestListener, ISuiteListener {
    
    private static final Logger logger = LoggerManager.getLogger(TestListener.class);
    private ExtentReportManager extentReportManager;
    private ExcelReportManager excelReportManager;
    private long suiteStartTime;
    private int totalTests = 0;
    private int passedTests = 0;
    private int failedTests = 0;
    private int skippedTests = 0;
    
    @Override
    public void onStart(ISuite suite) {
        suiteStartTime = System.currentTimeMillis();
        String suiteName = suite.getName();
        
        logger.info("Test suite started: {}", suiteName);
        
        // Initialize reports
        extentReportManager = ExtentReportManager.getInstance();
        extentReportManager.initializeReport();
        
        excelReportManager = ExcelReportManager.getInstance();
        
        LoggerManager.getInstance().logFrameworkInfo("Test Suite", "Started: " + suiteName);
    }
    
    @Override
    public void onFinish(ISuite suite) {
        long suiteEndTime = System.currentTimeMillis();
        long totalExecutionTime = suiteEndTime - suiteStartTime;
        String suiteName = suite.getName();
        
        logger.info("Test suite finished: {}. Total execution time: {}ms", suiteName, totalExecutionTime);
        
        // Create execution summary
        String startTime = LocalDateTime.now().minusSeconds((int) (totalExecutionTime / 1000))
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String endTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        
        ExcelReportManager.TestExecutionSummary summary = new ExcelReportManager.TestExecutionSummary(
                totalTests, passedTests, failedTests, skippedTests, totalExecutionTime,
                startTime, endTime, "dev", "chrome"
        );
        
        // Add summary to Excel report
        excelReportManager.addSummarySheet(summary);
        excelReportManager.closeReport();
        
        // Flush ExtentReports
        extentReportManager.flushReports();
        
        // Log final summary
        logger.info("Test Execution Summary:");
        logger.info("Total Tests: {}", totalTests);
        logger.info("Passed: {}", passedTests);
        logger.info("Failed: {}", failedTests);
        logger.info("Skipped: {}", skippedTests);
        logger.info("Pass Rate: {}%", summary.getPassRate());
        logger.info("Total Execution Time: {}ms", totalExecutionTime);
        
        LoggerManager.getInstance().logFrameworkInfo("Test Suite", "Finished: " + suiteName);
    }
    
    @Override
    public void onTestStart(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        String className = result.getTestClass().getName();
        
        totalTests++;
        
        logger.info("Test started: {}.{}", className, testName);
        LoggerManager.getInstance().logTestStep("Test Start", testName);
    }
    
    @Override
    public void onTestSuccess(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        String className = result.getTestClass().getName();
        long duration = result.getEndMillis() - result.getStartMillis();
        
        passedTests++;
        
        logger.info("Test passed: {}.{} - Duration: {}ms", className, testName, duration);
        LoggerManager.getInstance().logTestResult(testName, "PASS", duration);
        
        // Add to Excel report
        addTestResultToExcel(result, "PASS", duration, null);
    }
    
    @Override
    public void onTestFailure(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        String className = result.getTestClass().getName();
        long duration = result.getEndMillis() - result.getStartMillis();
        String errorMessage = result.getThrowable() != null ? result.getThrowable().getMessage() : "Unknown error";
        
        failedTests++;
        
        logger.error("Test failed: {}.{} - Duration: {}ms - Error: {}", 
                    className, testName, duration, errorMessage);
        LoggerManager.getInstance().logTestResult(testName, "FAIL", duration);
        
        // Add to Excel report
        addTestResultToExcel(result, "FAIL", duration, errorMessage);
    }
    
    @Override
    public void onTestSkipped(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        String className = result.getTestClass().getName();
        long duration = result.getEndMillis() - result.getStartMillis();
        String skipReason = result.getThrowable() != null ? result.getThrowable().getMessage() : "Test skipped";
        
        skippedTests++;
        
        logger.warn("Test skipped: {}.{} - Reason: {}", className, testName, skipReason);
        LoggerManager.getInstance().logTestResult(testName, "SKIP", duration);
        
        // Add to Excel report
        addTestResultToExcel(result, "SKIP", duration, skipReason);
    }
    
    /**
     * Add test result to Excel report
     */
    private void addTestResultToExcel(ITestResult result, String status, long duration, String errorMessage) {
        try {
            String testName = result.getMethod().getMethodName();
            String className = result.getTestClass().getName();
            String startTime = LocalDateTime.now().minusSeconds((int) (duration / 1000))
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            String endTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            
            ExcelReportManager.TestResult testResult = new ExcelReportManager.TestResult(
                    testName,
                    testName,
                    className,
                    "Automation",
                    startTime,
                    endTime,
                    duration,
                    status,
                    "chrome",
                    "dev",
                    errorMessage != null ? errorMessage : "",
                    ""
            );
            
            excelReportManager.addTestResult(testResult);
            
        } catch (Exception e) {
            logger.error("Failed to add test result to Excel report", e);
        }
    }
}