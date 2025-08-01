package com.automation.listeners;

import com.automation.core.LoggerManager;
import com.automation.reporting.ExtentReportManager;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import org.apache.logging.log4j.Logger;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

/**
 * ExtentReports listener for enhanced reporting integration
 */
public class ExtentReportListener implements ITestListener {
    private static final Logger logger = LoggerManager.getInstance().getLogger(ExtentReportListener.class);
    private final ExtentReportManager extentReportManager = ExtentReportManager.getInstance();
    private final ExtentReports extentReports = extentReportManager.getExtentReports();

    @Override
    public void onTestStart(ITestResult result) {
        String testName = result.getName();
        String testDescription = result.getMethod().getDescription();
        
        ExtentTest test = extentReports.createTest(testName, testDescription);
        test.log(Status.INFO, "Test started: " + testName);
        
        // Add test parameters if any
        Object[] parameters = result.getParameters();
        if (parameters != null && parameters.length > 0) {
            StringBuilder paramInfo = new StringBuilder("Test Parameters: ");
            for (Object param : parameters) {
                paramInfo.append(param).append(", ");
            }
            test.log(Status.INFO, paramInfo.toString());
        }
        
        logger.info("ExtentReports: Test started - {}", testName);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        String testName = result.getName();
        long duration = result.getEndMillis() - result.getStartMillis();
        
        ExtentTest test = extentReports.createTest(testName);
        test.log(Status.PASS, "Test PASSED: " + testName + " (Duration: " + duration + "ms)");
        
        // Add system information
        test.log(Status.INFO, "Browser: " + System.getProperty("browser", "chrome"));
        test.log(Status.INFO, "Environment: " + System.getProperty("environment", "qa"));
        
        logger.info("ExtentReports: Test passed - {} ({}ms)", testName, duration);
    }

    @Override
    public void onTestFailure(ITestResult result) {
        String testName = result.getName();
        Throwable throwable = result.getThrowable();
        long duration = result.getEndMillis() - result.getStartMillis();
        
        ExtentTest test = extentReports.createTest(testName);
        test.log(Status.FAIL, "Test FAILED: " + testName + " (Duration: " + duration + "ms)");
        
        if (throwable != null) {
            test.log(Status.FAIL, "Error: " + throwable.getMessage());
            test.log(Status.FAIL, "Stack Trace: " + throwable.getStackTrace());
        }
        
        // Add system information
        test.log(Status.INFO, "Browser: " + System.getProperty("browser", "chrome"));
        test.log(Status.INFO, "Environment: " + System.getProperty("environment", "qa"));
        
        logger.error("ExtentReports: Test failed - {} ({}ms)", testName, duration, throwable);
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        String testName = result.getName();
        long duration = result.getEndMillis() - result.getStartMillis();
        
        ExtentTest test = extentReports.createTest(testName);
        test.log(Status.SKIP, "Test SKIPPED: " + testName + " (Duration: " + duration + "ms)");
        
        // Add skip reason if available
        if (result.getThrowable() != null) {
            test.log(Status.SKIP, "Skip Reason: " + result.getThrowable().getMessage());
        }
        
        logger.warn("ExtentReports: Test skipped - {} ({}ms)", testName, duration);
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        String testName = result.getName();
        long duration = result.getEndMillis() - result.getStartMillis();
        
        ExtentTest test = extentReports.createTest(testName);
        test.log(Status.WARNING, "Test FAILED but within success percentage: " + testName + " (Duration: " + duration + "ms)");
        
        logger.warn("ExtentReports: Test failed but within success percentage - {} ({}ms)", testName, duration);
    }

    @Override
    public void onStart(ITestContext context) {
        String suiteName = context.getSuite().getName();
        String testName = context.getName();
        
        ExtentTest test = extentReports.createTest("Test Suite: " + testName);
        test.log(Status.INFO, "Starting test suite: " + suiteName + " - " + testName);
        
        // Add suite information
        test.log(Status.INFO, "Suite Name: " + suiteName);
        test.log(Status.INFO, "Test Name: " + testName);
        test.log(Status.INFO, "Total Test Methods: " + context.getAllTestMethods().length);
        
        logger.info("ExtentReports: Test suite started - {} - {}", suiteName, testName);
    }

    @Override
    public void onFinish(ITestContext context) {
        String suiteName = context.getSuite().getName();
        String testName = context.getName();
        
        // Generate detailed summary
        int totalTests = context.getAllTestMethods().length;
        int passedTests = context.getPassedTests().size();
        int failedTests = context.getFailedTests().size();
        int skippedTests = context.getSkippedTests().size();
        
        ExtentTest test = extentReports.createTest("Test Suite Summary: " + testName);
        test.log(Status.INFO, String.format("Test suite completed: %s - %s", suiteName, testName));
        test.log(Status.INFO, String.format("Total Tests: %d", totalTests));
        test.log(Status.PASS, String.format("Passed Tests: %d", passedTests));
        test.log(Status.FAIL, String.format("Failed Tests: %d", failedTests));
        test.log(Status.SKIP, String.format("Skipped Tests: %d", skippedTests));
        
        // Calculate success rate
        double successRate = totalTests > 0 ? (double) passedTests / totalTests * 100 : 0;
        test.log(Status.INFO, String.format("Success Rate: %.2f%%", successRate));
        
        logger.info("ExtentReports: Test suite completed - {} - {} (Total: {}, Passed: {}, Failed: {}, Skipped: {})", 
                   suiteName, testName, totalTests, passedTests, failedTests, skippedTests);
        
        // Flush and close reports
        extentReportManager.flushReports();
    }
}