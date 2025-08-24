package com.automation.reporting;

import com.automation.core.ConfigManager;
import com.automation.core.LoggerManager;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Singleton ExtentReports manager for comprehensive test reporting
 */
public class ExtentReportManager {
    private static final Logger logger = LoggerManager.getInstance().getLogger(ExtentReportManager.class);
    private static ExtentReportManager instance;
    private ExtentReports extentReports;
    private final ConfigManager configManager;

    private ExtentReportManager() {
        configManager = ConfigManager.getInstance();
        initializeExtentReports();
    }

    public static ExtentReportManager getInstance() {
        if (instance == null) {
            instance = new ExtentReportManager();
        }
        return instance;
    }

    private void initializeExtentReports() {
        extentReports = new ExtentReports();
        
        // Create report directory if it doesn't exist
        String reportPath = configManager.getProperty("extent.report.path");
        File reportDir = new File(reportPath);
        if (!reportDir.exists()) {
            reportDir.mkdirs();
        }

        // Generate report filename with timestamp
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
        String reportFileName = "ExtentReport_" + timestamp + ".html";
        String reportFilePath = reportPath + "/" + reportFileName;

        // Configure Spark Reporter
        ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportFilePath);
        sparkReporter.config().setTheme(Theme.valueOf(configManager.getProperty("extent.report.theme", "STANDARD").toUpperCase()));
        sparkReporter.config().setDocumentTitle(configManager.getProperty("extent.report.document.title", "Test Execution Report"));
        sparkReporter.config().setReportName(configManager.getProperty("extent.report.title", "UI + API Automation Framework"));
        sparkReporter.config().setTimeStampFormat("yyyy-MM-dd HH:mm:ss");

        // Attach reporter to ExtentReports
        extentReports.attachReporter(sparkReporter);

        // Set system information
        extentReports.setSystemInfo("OS", System.getProperty("os.name"));
        extentReports.setSystemInfo("Java Version", System.getProperty("java.version"));
        extentReports.setSystemInfo("Browser", configManager.getBrowser());
        extentReports.setSystemInfo("Environment", configManager.getProperty("environment", "QA"));
        extentReports.setSystemInfo("Base URL", configManager.getBaseUrl());
        extentReports.setSystemInfo("API Base URL", configManager.getApiBaseUrl());

        logger.info("ExtentReports initialized with report path: {}", reportFilePath);
    }

    public ExtentTest createTest(String testName) {
        return extentReports.createTest(testName);
    }

    public ExtentTest createTest(String testName, String description) {
        return extentReports.createTest(testName, description);
    }

    public void logInfo(ExtentTest test, String message) {
        if (test != null) {
            test.log(Status.INFO, message);
        }
        logger.info(message);
    }

    public void logPass(ExtentTest test, String message) {
        if (test != null) {
            test.log(Status.PASS, message);
        }
        logger.info("PASS: {}", message);
    }

    public void logFail(ExtentTest test, String message) {
        if (test != null) {
            test.log(Status.FAIL, message);
        }
        logger.error("FAIL: {}", message);
    }

    public void logFail(ExtentTest test, String message, Throwable throwable) {
        if (test != null) {
            test.log(Status.FAIL, message + "\n" + throwable.getMessage());
        }
        logger.error("FAIL: {}", message, throwable);
    }

    public void logSkip(ExtentTest test, String message) {
        if (test != null) {
            test.log(Status.SKIP, message);
        }
        logger.warn("SKIP: {}", message);
    }

    public void logWarning(ExtentTest test, String message) {
        if (test != null) {
            test.log(Status.WARNING, message);
        }
        logger.warn("WARNING: {}", message);
    }

    public void addScreenshot(ExtentTest test, String screenshotPath, String title) {
        if (test != null && screenshotPath != null) {
            try {
                test.addScreenCaptureFromPath(screenshotPath, title);
                logger.info("Screenshot added to report: {}", screenshotPath);
            } catch (Exception e) {
                logger.error("Error adding screenshot to report: {}", screenshotPath, e);
            }
        }
    }

    public void flushReports() {
        if (extentReports != null) {
            extentReports.flush();
            logger.info("ExtentReports flushed successfully");
        }
    }



    public ExtentReports getExtentReports() {
        return extentReports;
    }
}