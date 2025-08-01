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
 * UI Test Runner for Cucumber with TestNG integration
 */
@CucumberOptions(
    features = "src/test/resources/features/ui",
    glue = {"com.automation.stepdefinitions.ui", "com.automation.hooks"},
    plugin = {
        "pretty",
        "html:target/cucumber-reports/ui-cucumber-report.html",
        "json:target/cucumber-reports/ui-cucumber-report.json",
        "junit:target/cucumber-reports/ui-cucumber-report.xml"
    },
    monochrome = true,
    dryRun = false,
    publish = false
)
public class UITestRunner extends AbstractTestNGCucumberTests {
    private static final Logger logger = LoggerManager.getInstance().getLogger(UITestRunner.class);
    private final ConfigManager configManager = ConfigManager.getInstance();
    private final ExtentReportManager extentReportManager = ExtentReportManager.getInstance();

    @BeforeClass
    public void setUp() {
        logger.info("Setting up UI Test Runner");
        logger.info("Browser: {}", configManager.getBrowser());
        logger.info("Headless: {}", configManager.isHeadless());
        logger.info("Base URL: {}", configManager.getBaseUrl());
        
        extentReportManager.logInfo(extentReportManager.createTest("UI Test Setup"), 
                                 "UI Test Runner setup completed");
    }

    @AfterClass
    public void tearDown() {
        logger.info("Tearing down UI Test Runner");
        
        // Quit WebDriver
        WebDriverManager.getInstance().quitDriver();
        
        // Close reports
        extentReportManager.closeReports();
        
        logger.info("UI Test Runner teardown completed");
    }

    @Override
    @DataProvider(parallel = true)
    public Object[][] scenarios() {
        return super.scenarios();
    }
}