package com.automation.tests;

import com.automation.core.CustomDriverManager;
import com.automation.data.TestDataManager;
import com.automation.reporting.AllureReportManager;
import io.qameta.allure.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Sample test class demonstrating Allure reporting and retry mechanism
 */
@Epic("Sample Test Suite")
@Feature("Allure Integration")
@Story("Demonstration of Allure Features")
public class SampleAllureTest {
    
    private static final Logger logger = LogManager.getLogger(SampleAllureTest.class);
    private WebDriver driver;
    private AllureReportManager allureManager;
    private TestDataManager dataManager;
    
    @BeforeMethod
    public void setUp() {
        allureManager = AllureReportManager.getInstance();
        dataManager = TestDataManager.getInstance();
        
        // Add test environment info to Allure
        allureManager.addEnvironmentInfo("Browser", "Chrome");
        allureManager.addEnvironmentInfo("Environment", "QA");
        allureManager.addEnvironmentInfo("Test Framework", "TestNG + Cucumber");
        
        // Initialize WebDriver
        driver = CustomDriverManager.getInstance().getDriver();
        
        logger.info("Test setup completed");
    }
    
    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            CustomDriverManager.getInstance().closeDriver();
        }
        logger.info("Test teardown completed");
    }
    
    @Test(description = "Sample test with Allure annotations and retry mechanism")
    @Severity(SeverityLevel.CRITICAL)
    @Description("This test demonstrates various Allure features including steps, attachments, and retry mechanism")
    @Link(name = "Test Documentation", url = "https://example.com/test-docs")
    @Issue("TEST-001")
    @TmsLink("TMS-001")
    public void sampleAllureTest() {
        
        // Add test step to Allure
        allureManager.addStep("Starting sample test with Allure integration");
        
        try {
            // Navigate to a test page
            allureManager.addStepWithValue("Navigating to test page", "https://demoqa.com");
            driver.get("https://demoqa.com");
            
            // Add page title to Allure
            String pageTitle = driver.getTitle();
            allureManager.addTextAttachment("Page Title", pageTitle);
            
            // Verify page title
            allureManager.addStep("Verifying page title");
            Assert.assertTrue(pageTitle.contains("Demo"), "Page title should contain 'Demo'");
            
            // Add screenshot to Allure
            String screenshotPath = takeScreenshot("sample_test_success");
            if (screenshotPath != null) {
                allureManager.addScreenshot("Test Success Screenshot", screenshotPath);
            }
            
            // Add test data to Allure
            String randomData = dataManager.getRandomString(10);
            allureManager.addTextAttachment("Generated Test Data", randomData);
            
            allureManager.addStep("Sample test completed successfully");
            
        } catch (Exception e) {
            // Add error information to Allure
            allureManager.addTextAttachment("Test Error", e.getMessage());
            
            // Take screenshot on failure
            String failureScreenshot = takeScreenshot("sample_test_failure");
            if (failureScreenshot != null) {
                allureManager.addScreenshot("Test Failure Screenshot", failureScreenshot);
            }
            
            throw e;
        }
    }
    
    @Test(description = "Test with retry mechanism demonstration")
    @Severity(SeverityLevel.BLOCKER)
    @Description("This test demonstrates the retry mechanism for flaky tests")
    @Story("Retry Mechanism")
    public void testWithRetryMechanism() {
        
        allureManager.addStep("Starting retry mechanism test");
        
        // Simulate a potentially flaky test
        int randomValue = (int) (Math.random() * 10);
        
        allureManager.addStepWithValue("Generated random value", randomValue);
        
        // This test will fail 30% of the time and retry up to 2 times
        if (randomValue < 3) {
            allureManager.addStep("Test failed - will be retried");
            Assert.fail("Simulated test failure for retry demonstration");
        }
        
        allureManager.addStep("Test passed successfully");
        Assert.assertTrue(randomValue >= 3, "Random value should be >= 3");
    }
    
    @Test(description = "Test with multiple Allure features")
    @Severity(SeverityLevel.NORMAL)
    @Description("Comprehensive test demonstrating various Allure reporting features")
    @Feature("Allure Reporting")
    public void comprehensiveAllureTest() {
        
        // Add multiple steps
        allureManager.addStep("Step 1: Test initialization");
        allureManager.addStepWithParams("Step 2: Processing data", "input", "output");
        
        // Add different types of attachments
        allureManager.addJsonAttachment("Test Configuration", "{\"browser\":\"chrome\",\"headless\":false}");
        allureManager.addHtmlAttachment("Test Summary", "<h1>Test Summary</h1><p>This is a comprehensive test</p>");
        
        // Add test labels and tags
        allureManager.addTestLabel("component", "ui");
        allureManager.addTestTag("smoke");
        allureManager.addTestTag("regression");
        
        // Add test links
        allureManager.addTestLink("Requirements", "https://example.com/requirements");
        allureManager.addTestIssue("BUG-123");
        allureManager.addTestRequirement("REQ-456");
        
        // Simulate some test logic
        allureManager.addStep("Performing test actions");
        
        // Add test data
        String email = dataManager.getRandomEmail();
        allureManager.addTextAttachment("Generated Email", email);
        
        allureManager.addStep("Comprehensive test completed");
    }
    
    private String takeScreenshot(String testName) {
        try {
            // This would normally use the WebDriver's screenshot capability
            // For demonstration, we'll just return a placeholder path
            String screenshotPath = "target/screenshots/" + testName + "_" + System.currentTimeMillis() + ".png";
            logger.info("Screenshot taken: {}", screenshotPath);
            return screenshotPath;
        } catch (Exception e) {
            logger.error("Failed to take screenshot", e);
            return null;
        }
    }
}
