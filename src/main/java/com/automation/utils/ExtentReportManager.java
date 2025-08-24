package com.automation.utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.automation.constants.FrameworkConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * ExtentReports manager for generating HTML test reports
 * Singleton pattern implementation for thread-safe reporting
 */
public class ExtentReportManager {
    
    private static final Logger logger = LogManager.getLogger(ExtentReportManager.class);
    private static ExtentReports extentReports;
    private static final ThreadLocal<ExtentTest> extentTest = new ThreadLocal<>();
    private static ExtentReportManager instance;
    private final ConfigReader configReader;
    
    private ExtentReportManager() {
        this.configReader = ConfigReader.getInstance();
    }
    
    public static ExtentReportManager getInstance() {
        if (instance == null) {
            synchronized (ExtentReportManager.class) {
                if (instance == null) {
                    instance = new ExtentReportManager();
                }
            }
        }
        return instance;
    }
    
    /**
     * Initialize ExtentReports with configuration
     */
    public synchronized void initializeReport() {
        if (extentReports == null) {
            // Create reports directory if it doesn't exist
            File reportsDir = new File("reports");
            if (!reportsDir.exists()) {
                reportsDir.mkdirs();
            }
            
            // Generate timestamp for report name
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
            String reportPath = "reports/ExtentReport_" + timestamp + ".html";
            
            ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportPath);
            configureSparkReporter(sparkReporter);
            
            extentReports = new ExtentReports();
            extentReports.attachReporter(sparkReporter);
            addSystemInformation();
            
            logger.info("ExtentReports initialized successfully. Report path: {}", reportPath);
        }
    }
    
    /**
     * Configure ExtentSparkReporter with custom settings
     */
    private void configureSparkReporter(ExtentSparkReporter sparkReporter) {
        sparkReporter.config().setReportName(configReader.getProperty("extent.report.name", "Automation Test Report"));
        sparkReporter.config().setDocumentTitle(configReader.getProperty("extent.report.title", "Test Execution Results"));
        sparkReporter.config().setTheme(Theme.STANDARD);
        sparkReporter.config().setTimeStampFormat("dd-MM-yyyy HH:mm:ss");
        sparkReporter.config().setEncoding("utf-8");
        
        // Custom CSS and JavaScript can be added here
        sparkReporter.config().setCss(getCustomCSS());
        sparkReporter.config().setJs(getCustomJS());
    }
    
    /**
     * Add system information to the report
     */
    private void addSystemInformation() {
        extentReports.setSystemInfo("Operating System", System.getProperty("os.name"));
        extentReports.setSystemInfo("OS Version", System.getProperty("os.version"));
        extentReports.setSystemInfo("Java Version", System.getProperty("java.version"));
        extentReports.setSystemInfo("User Name", System.getProperty("user.name"));
        extentReports.setSystemInfo("Environment", configReader.getEnvironment());
        extentReports.setSystemInfo("Browser", configReader.getBrowser());
        extentReports.setSystemInfo("Headless Mode", String.valueOf(configReader.isHeadless()));
        extentReports.setSystemInfo("Base URL", configReader.getBaseUrl());
        extentReports.setSystemInfo("API Base URL", configReader.getApiBaseUrl());
    }
    
    /**
     * Create a new test in the report
     * @param testName Name of the test
     * @param testDescription Description of the test
     * @return ExtentTest instance
     */
    public ExtentTest createTest(String testName, String testDescription) {
        ExtentTest test = extentReports.createTest(testName, testDescription);
        extentTest.set(test);
        logger.debug("Created test in ExtentReport: {}", testName);
        return test;
    }
    
    /**
     * Create a new test with category
     * @param testName Name of the test
     * @param testDescription Description of the test
     * @param category Test category/tag
     * @return ExtentTest instance
     */
    public ExtentTest createTest(String testName, String testDescription, String category) {
        ExtentTest test = extentReports.createTest(testName, testDescription);
        test.assignCategory(category);
        extentTest.set(test);
        logger.debug("Created test in ExtentReport with category: {} - {}", testName, category);
        return test;
    }
    
    /**
     * Get current test instance
     * @return Current ExtentTest instance
     */
    public ExtentTest getTest() {
        return extentTest.get();
    }
    
    /**
     * Log info message to the current test
     * @param message Message to log
     */
    public void logInfo(String message) {
        if (extentTest.get() != null) {
            extentTest.get().log(Status.INFO, message);
        }
    }
    
    /**
     * Log pass message to the current test
     * @param message Message to log
     */
    public void logPass(String message) {
        if (extentTest.get() != null) {
            extentTest.get().log(Status.PASS, message);
        }
    }
    
    /**
     * Log fail message to the current test
     * @param message Message to log
     */
    public void logFail(String message) {
        if (extentTest.get() != null) {
            extentTest.get().log(Status.FAIL, message);
        }
    }
    
    /**
     * Log skip message to the current test
     * @param message Message to log
     */
    public void logSkip(String message) {
        if (extentTest.get() != null) {
            extentTest.get().log(Status.SKIP, message);
        }
    }
    
    /**
     * Log warning message to the current test
     * @param message Message to log
     */
    public void logWarning(String message) {
        if (extentTest.get() != null) {
            extentTest.get().log(Status.WARNING, message);
        }
    }
    
    /**
     * Attach screenshot to the current test
     * @param screenshotPath Path to the screenshot
     * @param description Screenshot description
     */
    public void attachScreenshot(String screenshotPath, String description) {
        if (extentTest.get() != null) {
            try {
                extentTest.get().addScreenCaptureFromPath(screenshotPath, description);
                logger.debug("Screenshot attached to test: {}", screenshotPath);
            } catch (Exception e) {
                logger.error("Failed to attach screenshot: {}", screenshotPath, e);
            }
        }
    }
    
    /**
     * Log step with status
     * @param stepName Name of the step
     * @param status Status of the step
     * @param details Step details
     */
    public void logStep(String stepName, Status status, String details) {
        if (extentTest.get() != null) {
            extentTest.get().log(status, "<b>" + stepName + "</b>: " + details);
        }
    }
    
    /**
     * Log API request details
     * @param method HTTP method
     * @param url Request URL
     * @param requestBody Request body (optional)
     * @param statusCode Response status code
     * @param responseTime Response time in ms
     */
    public void logApiRequest(String method, String url, String requestBody, int statusCode, long responseTime) {
        if (extentTest.get() != null) {
            StringBuilder apiDetails = new StringBuilder();
            apiDetails.append("<details><summary><b>API Request Details</b></summary>");
            apiDetails.append("<p><b>Method:</b> ").append(method).append("</p>");
            apiDetails.append("<p><b>URL:</b> ").append(url).append("</p>");
            apiDetails.append("<p><b>Status Code:</b> ").append(statusCode).append("</p>");
            apiDetails.append("<p><b>Response Time:</b> ").append(responseTime).append("ms</p>");
            
            if (requestBody != null && !requestBody.isEmpty()) {
                apiDetails.append("<p><b>Request Body:</b></p>");
                apiDetails.append("<pre>").append(requestBody).append("</pre>");
            }
            
            apiDetails.append("</details>");
            
            Status status = (statusCode >= 200 && statusCode < 300) ? Status.PASS : Status.FAIL;
            extentTest.get().log(status, apiDetails.toString());
        }
    }
    
    /**
     * Add child test (for test steps or sub-tests)
     * @param childTestName Name of the child test
     * @param description Description of the child test
     * @return ExtentTest instance for child test
     */
    public ExtentTest createChildTest(String childTestName, String description) {
        if (extentTest.get() != null) {
            ExtentTest childTest = extentTest.get().createNode(childTestName, description);
            logger.debug("Created child test: {}", childTestName);
            return childTest;
        }
        return null;
    }
    
    /**
     * Flush the reports and save to file
     */
    public synchronized void flushReports() {
        if (extentReports != null) {
            extentReports.flush();
            logger.info("ExtentReports flushed successfully");
        }
    }
    
    /**
     * Remove current test from ThreadLocal
     */
    public void removeTest() {
        extentTest.remove();
    }
    
    /**
     * Get custom CSS for report styling
     * @return Custom CSS string
     */
    private String getCustomCSS() {
        return """
            .navbar-brand {
                color: #fff !important;
            }
            .card-panel {
                border-radius: 8px;
            }
            .test-name {
                font-weight: bold;
            }
            """;
    }
    
    /**
     * Get custom JavaScript for report functionality
     * @return Custom JavaScript string
     */
    private String getCustomJS() {
        return """
            document.addEventListener('DOMContentLoaded', function() {
                console.log('Automation Framework Report Loaded');
            });
            """;
    }
}