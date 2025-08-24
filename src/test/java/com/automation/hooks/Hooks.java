package com.automation.hooks;

import com.automation.core.LoggerManager;
import com.automation.core.CustomDriverManager;
import com.automation.reporting.ExtentReportManager;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.TakesScreenshot;

/**
 * Cucumber hooks for setup and teardown operations
 */
public class Hooks {
    private static final Logger logger = LoggerManager.getInstance().getLogger(Hooks.class);
    private final ExtentReportManager extentReportManager = ExtentReportManager.getInstance();

    @Before
    public void setUp(Scenario scenario) {
        logger.info("Starting scenario: {}", scenario.getName());
        extentReportManager.logInfo(extentReportManager.createTest("Scenario: " + scenario.getName()), 
                                 "Starting scenario: " + scenario.getName());
        
        // Add scenario tags to report
        if (scenario.getSourceTagNames() != null && !scenario.getSourceTagNames().isEmpty()) {
            String tags = String.join(", ", scenario.getSourceTagNames());
            extentReportManager.logInfo(extentReportManager.createTest("Scenario Tags"), 
                                     "Tags: " + tags);
        }
    }

    @After
    public void tearDown(Scenario scenario) {
        logger.info("Finished scenario: {} - Status: {}", scenario.getName(), scenario.getStatus());
        
        if (scenario.isFailed()) {
            // Take screenshot on failure
            String screenshotPath = takeScreenshotOnFailure(scenario.getName());
            if (screenshotPath != null) {
                extentReportManager.addScreenshot(extentReportManager.createTest("Failure Screenshot"), 
                                               screenshotPath, "Failure Screenshot");
            }
            
            extentReportManager.logFail(extentReportManager.createTest("Scenario Failed"), 
                                     "Scenario failed: " + scenario.getName());
        } else {
            extentReportManager.logPass(extentReportManager.createTest("Scenario Passed"), 
                                     "Scenario passed: " + scenario.getName());
        }
        
        // Clean up WebDriver if needed
        cleanupWebDriver();
    }

    @Before("@ui")
    public void setUpUI() {
        logger.info("Setting up UI test environment");
        // Initialize WebDriver for UI tests
        CustomDriverManager.getInstance().getDriver();
    }

    @After("@ui")
    public void tearDownUI() {
        logger.info("Tearing down UI test environment");
        // WebDriver cleanup is handled in the main tearDown method
    }

    @Before("@api")
    public void setUpAPI() {
        logger.info("Setting up API test environment");
        // API tests don't need WebDriver
    }

    @After("@api")
    public void tearDownAPI() {
        logger.info("Tearing down API test environment");
        // No cleanup needed for API tests
    }

    @Before("@smoke")
    public void setUpSmoke() {
        logger.info("Setting up smoke test environment");
    }

    @After("@smoke")
    public void tearDownSmoke() {
        logger.info("Tearing down smoke test environment");
    }

    @Before("@regression")
    public void setUpRegression() {
        logger.info("Setting up regression test environment");
    }

    @After("@regression")
    public void tearDownRegression() {
        logger.info("Tearing down regression test environment");
    }

    private String takeScreenshotOnFailure(String scenarioName) {
        try {
            CustomDriverManager webDriverManager = CustomDriverManager.getInstance();
            if (webDriverManager.getDriver() != null) {
                // Use the WebDriver's screenshot capability directly
                TakesScreenshot ts = (TakesScreenshot) webDriverManager.getDriver();
                String screenshotPath = "target/screenshots/" + scenarioName + "_failure_" + System.currentTimeMillis() + ".png";
                // Save screenshot to file
                try {
                    java.nio.file.Files.write(java.nio.file.Paths.get(screenshotPath), ts.getScreenshotAs(org.openqa.selenium.OutputType.BYTES));
                } catch (Exception e) {
                    logger.error("Failed to save screenshot to file: {}", screenshotPath, e);
                }
                logger.info("Screenshot taken on failure: {}", screenshotPath);
                return screenshotPath;
            }
        } catch (Exception e) {
            logger.error("Failed to take screenshot on failure", e);
        }
        return null;
    }

    private void cleanupWebDriver() {
        try {
            CustomDriverManager webDriverManager = CustomDriverManager.getInstance();
            if (webDriverManager.getDriver() != null) {
                // Don't quit the driver here as it might be reused
                // The driver will be quit in the test runner's @AfterClass method
                logger.debug("WebDriver cleanup completed");
            }
        } catch (Exception e) {
            logger.error("Error during WebDriver cleanup", e);
        }
    }
}