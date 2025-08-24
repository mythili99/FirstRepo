package com.automation.runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;

import com.automation.core.ConfigManager;
import com.automation.core.LoggerManager;
import com.automation.core.WebDriverManager;
import com.automation.reporting.ExtentReportManager;
import org.apache.logging.log4j.Logger;

/**
 * Integration Test Runner for Cucumber with TestNG integration
 */
@CucumberOptions(
    features = "src/test/resources/features/integration",
    glue = {"com.automation.stepdefinitions.integration", "com.automation.hooks"},
    plugin = {
        "pretty",
        "html:target/cucumber-reports/integration-cucumber-report.html",
        "json:target/cucumber-reports/integration-cucumber-report.json",
        "junit:target/cucumber-reports/integration-cucumber-report.xml"
    },
    monochrome = true,
    dryRun = false,
    publish = false
)
public class IntegrationTestRunner extends AbstractTestNGCucumberTests {
    private static final Logger logger = LoggerManager.getInstance().getLogger(IntegrationTestRunner.class);
    private final ConfigManager configManager = ConfigManager.getInstance();
    private final ExtentReportManager extentReportManager = ExtentReportManager.getInstance();

    @BeforeClass
    public void setUp() {
        logger.info("Setting up Integration Test Runner");
        logger.info("Browser: {}", configManager.getBrowser());
        logger.info("Base URL: {}", configManager.getBaseUrl());
        logger.info("API Base URL: {}", configManager.getApiBaseUrl());
        logger.info("Environment: {}", configManager.getProperty("environment", "qa"));
        
        extentReportManager.logInfo(extentReportManager.createTest("Integration Test Setup"), 
                                 "Integration Test Runner setup completed");
    }

    @AfterClass
    public void tearDown() {
        logger.info("Tearing down Integration Test Runner");
        
        // Quit WebDriver
        WebDriverManager.getInstance().quitDriver();
        
        // Close reports
        extentReportManager.closeReports();
        
        logger.info("Integration Test Runner teardown completed");
    }

    @Override
    @DataProvider(parallel = false) // Integration tests should not run in parallel
    public Object[][] scenarios() {
        return super.scenarios();
    }
}