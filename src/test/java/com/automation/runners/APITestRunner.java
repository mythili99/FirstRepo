package com.automation.runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;

import com.automation.core.ConfigManager;
import com.automation.core.LoggerManager;
import com.automation.reporting.ExtentReportManager;
import org.apache.logging.log4j.Logger;

/**
 * API Test Runner for Cucumber with TestNG integration
 */
@CucumberOptions(
    features = "src/test/resources/features/api",
    glue = {"com.automation.stepdefinitions.api", "com.automation.hooks"},
    plugin = {
        "pretty",
        "html:target/cucumber-reports/api-cucumber-report.html",
        "json:target/cucumber-reports/api-cucumber-report.json",
        "junit:target/cucumber-reports/api-cucumber-report.xml"
    },
    monochrome = true,
    dryRun = false,
    publish = false
)
public class APITestRunner extends AbstractTestNGCucumberTests {
    private static final Logger logger = LoggerManager.getInstance().getLogger(APITestRunner.class);
    private final ConfigManager configManager = ConfigManager.getInstance();
    private final ExtentReportManager extentReportManager = ExtentReportManager.getInstance();

    @BeforeClass
    public void setUp() {
        logger.info("Setting up API Test Runner");
        logger.info("API Base URL: {}", configManager.getApiBaseUrl());
        logger.info("Environment: {}", configManager.getProperty("environment", "qa"));
        
        extentReportManager.logInfo(extentReportManager.createTest("API Test Setup"), 
                                 "API Test Runner setup completed");
    }

    @AfterClass
    public void tearDown() {
        logger.info("Tearing down API Test Runner");
        
        // Close reports
        extentReportManager.closeReports();
        
        logger.info("API Test Runner teardown completed");
    }

    @Override
    @DataProvider(parallel = true)
    public Object[][] scenarios() {
        return super.scenarios();
    }
}