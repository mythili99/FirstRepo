package com.automation.listeners;

import com.automation.core.LoggerManager;
import com.automation.core.WebDriverManager;
import com.automation.reporting.ExtentReportManager;
import org.apache.logging.log4j.Logger;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

/**
 * TestNG listener for test lifecycle management and reporting
 */
public class TestListener implements ITestListener {
    private static final Logger logger = LoggerManager.getInstance().getLogger(TestListener.class);
    private final ExtentReportManager extentReportManager = ExtentReportManager.getInstance();

    @Override
    public void onTestStart(ITestResult result) {
        String testName = result.getName();
        String testClass = result.getTestClass().getName();
        
        logger.info("Starting test: {} in class: {}", testName, testClass);
        extentReportManager.logInfo(extentReportManager.createTest(testName), 
                                 "Starting test: " + testName + " in class: " + testClass);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        String testName = result.getName();
        long duration = result.getEndMillis() - result.getStartMillis();
        
        logger.info("Test PASSED: {} (Duration: {}ms)", testName, duration);
        extentReportManager.logPass(extentReportManager.createTest(testName), 
                                 "Test PASSED: " + testName + " (Duration: " + duration + "ms)");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        String testName = result.getName();
        Throwable throwable = result.getThrowable();
        long duration = result.getEndMillis() - result.getStartMillis();
        
        logger.error("Test FAILED: {} (Duration: {}ms)", testName, duration, throwable);
        extentReportManager.logFail(extentReportManager.createTest(testName), 
                                 "Test FAILED: " + testName + " (Duration: " + duration + "ms)", throwable);
        
        // Take screenshot on failure if configured
        if (result.getTestClass().getRealClass().getPackage().getName().contains("ui")) {
            takeScreenshotOnFailure(testName);
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        String testName = result.getName();
        long duration = result.getEndMillis() - result.getStartMillis();
        
        logger.warn("Test SKIPPED: {} (Duration: {}ms)", testName, duration);
        extentReportManager.logSkip(extentReportManager.createTest(testName), 
                                 "Test SKIPPED: " + testName + " (Duration: " + duration + "ms)");
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        String testName = result.getName();
        long duration = result.getEndMillis() - result.getStartMillis();
        
        logger.warn("Test FAILED but within success percentage: {} (Duration: {}ms)", testName, duration);
        extentReportManager.logWarning(extentReportManager.createTest(testName), 
                                    "Test FAILED but within success percentage: " + testName + " (Duration: " + duration + "ms)");
    }

    @Override
    public void onStart(ITestContext context) {
        String suiteName = context.getSuite().getName();
        String testName = context.getName();
        
        logger.info("Starting test suite: {} - {}", suiteName, testName);
        extentReportManager.logInfo(extentReportManager.createTest("Test Suite: " + testName), 
                                 "Starting test suite: " + suiteName + " - " + testName);
    }

    @Override
    public void onFinish(ITestContext context) {
        String suiteName = context.getSuite().getName();
        String testName = context.getName();
        
        // Generate summary
        int totalTests = context.getAllTestMethods().length;
        int passedTests = context.getPassedTests().size();
        int failedTests = context.getFailedTests().size();
        int skippedTests = context.getSkippedTests().size();
        
        logger.info("Test suite completed: {} - {} (Total: {}, Passed: {}, Failed: {}, Skipped: {})", 
                   suiteName, testName, totalTests, passedTests, failedTests, skippedTests);
        
        extentReportManager.logInfo(extentReportManager.createTest("Test Suite Summary: " + testName), 
                                 String.format("Test suite completed: %s - %s (Total: %d, Passed: %d, Failed: %d, Skipped: %d)", 
                                             suiteName, testName, totalTests, passedTests, failedTests, skippedTests));
        
        // Flush reports
        extentReportManager.flushReports();
    }

    private void takeScreenshotOnFailure(String testName) {
        try {
            WebDriverManager webDriverManager = WebDriverManager.getInstance();
            if (webDriverManager.getDriver() != null) {
                String screenshotPath = webDriverManager.takeScreenshot(testName);
                if (screenshotPath != null) {
                    logger.info("Screenshot taken on failure: {}", screenshotPath);
                }
            }
        } catch (Exception e) {
            logger.error("Failed to take screenshot on test failure", e);
        }
    }
}