package com.automation.steps;

import com.automation.core.DriverManager;
import com.automation.utils.*;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.apache.logging.log4j.Logger;

/**
 * Base step definitions class with setup and teardown hooks
 * Manages WebDriver lifecycle and reporting
 */
public class BaseSteps {
    
    private static final Logger logger = LoggerManager.getLogger(BaseSteps.class);
    protected DriverManager driverManager;
    protected ExtentReportManager extentReportManager;
    protected ScreenshotManager screenshotManager;
    protected ConfigReader configReader;
    
    @Before
    public void setUp(Scenario scenario) {
        logger.info("Setting up test scenario: {}", scenario.getName());
        
        // Initialize managers
        driverManager = DriverManager.getInstance();
        extentReportManager = ExtentReportManager.getInstance();
        screenshotManager = ScreenshotManager.getInstance();
        configReader = ConfigReader.getInstance();
        
        // Initialize reports if not already done
        extentReportManager.initializeReport();
        
        // Create test in ExtentReports
        String scenarioName = scenario.getName();
        String description = "Cucumber scenario: " + scenarioName;
        extentReportManager.createTest(scenarioName, description);
        
        // Initialize WebDriver for UI tests
        if (scenario.getSourceTagNames().contains("@ui")) {
            try {
                driverManager.initializeDriver();
                extentReportManager.logInfo("WebDriver initialized for UI test");
                logger.info("WebDriver initialized for scenario: {}", scenarioName);
            } catch (Exception e) {
                logger.error("Failed to initialize WebDriver for scenario: {}", scenarioName, e);
                extentReportManager.logFail("Failed to initialize WebDriver: " + e.getMessage());
                throw e;
            }
        }
        
        extentReportManager.logInfo("Test scenario started: " + scenarioName);
        logger.info("Test scenario setup completed: {}", scenarioName);
    }
    
    @After
    public void tearDown(Scenario scenario) {
        String scenarioName = scenario.getName();
        logger.info("Tearing down test scenario: {}", scenarioName);
        
        try {
            // Handle test result based on scenario status
            if (scenario.isFailed()) {
                handleTestFailure(scenario);
            } else {
                handleTestSuccess(scenario);
            }
            
            // Quit WebDriver for UI tests
            if (scenario.getSourceTagNames().contains("@ui")) {
                try {
                    driverManager.quitDriver();
                    extentReportManager.logInfo("WebDriver quit successfully");
                    logger.info("WebDriver quit for scenario: {}", scenarioName);
                } catch (Exception e) {
                    logger.error("Error quitting WebDriver for scenario: {}", scenarioName, e);
                }
            }
            
            extentReportManager.logInfo("Test scenario completed: " + scenarioName);
            
        } catch (Exception e) {
            logger.error("Error during test teardown for scenario: {}", scenarioName, e);
        } finally {
            // Clean up ExtentTest from ThreadLocal
            extentReportManager.removeTest();
            logger.info("Test scenario teardown completed: {}", scenarioName);
        }
    }
    
    /**
     * Handle test failure - capture screenshot and log error
     */
    private void handleTestFailure(Scenario scenario) {
        String scenarioName = scenario.getName();
        logger.error("Test scenario failed: {}", scenarioName);
        
        // Capture screenshot for UI tests
        if (scenario.getSourceTagNames().contains("@ui")) {
            String screenshotPath = screenshotManager.captureFailureScreenshot(scenarioName);
            if (screenshotPath != null) {
                // Attach screenshot to ExtentReports
                String relativePath = screenshotManager.getRelativePath(screenshotPath);
                extentReportManager.attachScreenshot(relativePath, "Failure Screenshot");
                
                // Attach screenshot to Cucumber report
                try {
                    byte[] screenshot = ((org.openqa.selenium.TakesScreenshot) driverManager.getDriver())
                            .getScreenshotAs(org.openqa.selenium.OutputType.BYTES);
                    scenario.attach(screenshot, "image/png", "Failure Screenshot");
                } catch (Exception e) {
                    logger.error("Failed to attach screenshot to Cucumber report", e);
                }
            }
        }
        
        extentReportManager.logFail("Test scenario failed: " + scenarioName);
    }
    
    /**
     * Handle test success - capture screenshot if enabled
     */
    private void handleTestSuccess(Scenario scenario) {
        String scenarioName = scenario.getName();
        logger.info("Test scenario passed: {}", scenarioName);
        
        // Capture screenshot for UI tests if enabled
        if (scenario.getSourceTagNames().contains("@ui")) {
            String screenshotPath = screenshotManager.capturePassScreenshot(scenarioName);
            if (screenshotPath != null) {
                String relativePath = screenshotManager.getRelativePath(screenshotPath);
                extentReportManager.attachScreenshot(relativePath, "Pass Screenshot");
            }
        }
        
        extentReportManager.logPass("Test scenario passed: " + scenarioName);
    }
}