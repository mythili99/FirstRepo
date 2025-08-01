package com.automation.stepdefinitions.ui;

import com.automation.core.ConfigManager;
import com.automation.core.LoggerManager;
import com.automation.data.TestDataManager;
import com.automation.ui.pages.LoginPage;
import com.automation.reporting.ExtentReportManager;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;

import java.util.List;
import java.util.Map;

/**
 * Step definitions for UI login functionality
 */
public class LoginStepDefinitions {
    private static final Logger logger = LoggerManager.getInstance().getLogger(LoginStepDefinitions.class);
    private final ConfigManager configManager = ConfigManager.getInstance();
    private final TestDataManager testDataManager = TestDataManager.getInstance();
    private final ExtentReportManager extentReportManager = ExtentReportManager.getInstance();
    
    private LoginPage loginPage;
    private String currentUsername;
    private String currentPassword;

    @Before
    public void setUp() {
        logger.info("Setting up Login Step Definitions");
        loginPage = new LoginPage();
    }

    @After
    public void tearDown() {
        logger.info("Tearing down Login Step Definitions");
    }

    @Given("I am on the login page")
    public void iAmOnTheLoginPage() {
        String baseUrl = configManager.getBaseUrl();
        loginPage.navigateTo(baseUrl + "/login");
        logger.info("Navigated to login page: {}", baseUrl + "/login");
        extentReportManager.logInfo(extentReportManager.createTest("Login Navigation"), 
                                 "Navigated to login page");
    }

    @Given("the login form is displayed")
    public void theLoginFormIsDisplayed() {
        boolean isFormDisplayed = loginPage.isLoginFormDisplayed();
        Assert.assertTrue(isFormDisplayed, "Login form should be displayed");
        logger.info("Login form is displayed");
        extentReportManager.logPass(extentReportManager.createTest("Login Form Display"), 
                                 "Login form is displayed");
    }

    @When("I enter valid username {string}")
    public void iEnterValidUsername(String username) {
        this.currentUsername = username;
        loginPage.enterUsername(username);
        logger.info("Entered valid username: {}", username);
        extentReportManager.logInfo(extentReportManager.createTest("Username Entry"), 
                                 "Entered username: " + username);
    }

    @When("I enter valid password {string}")
    public void iEnterValidPassword(String password) {
        this.currentPassword = password;
        loginPage.enterPassword(password);
        logger.info("Entered valid password");
        extentReportManager.logInfo(extentReportManager.createTest("Password Entry"), 
                                 "Entered password");
    }

    @When("I enter invalid username {string}")
    public void iEnterInvalidUsername(String username) {
        this.currentUsername = username;
        loginPage.enterUsername(username);
        logger.info("Entered invalid username: {}", username);
        extentReportManager.logInfo(extentReportManager.createTest("Invalid Username Entry"), 
                                 "Entered invalid username: " + username);
    }

    @When("I enter invalid password {string}")
    public void iEnterInvalidPassword(String password) {
        this.currentPassword = password;
        loginPage.enterPassword(password);
        logger.info("Entered invalid password");
        extentReportManager.logInfo(extentReportManager.createTest("Invalid Password Entry"), 
                                 "Entered invalid password");
    }

    @When("I leave the username field empty")
    public void iLeaveTheUsernameFieldEmpty() {
        this.currentUsername = "";
        loginPage.clearUsername();
        logger.info("Left username field empty");
        extentReportManager.logInfo(extentReportManager.createTest("Empty Username"), 
                                 "Left username field empty");
    }

    @When("I leave the password field empty")
    public void iLeaveThePasswordFieldEmpty() {
        this.currentPassword = "";
        loginPage.clearPassword();
        logger.info("Left password field empty");
        extentReportManager.logInfo(extentReportManager.createTest("Empty Password"), 
                                 "Left password field empty");
    }

    @When("I click the login button")
    public void iClickTheLoginButton() {
        loginPage.clickLoginButton();
        logger.info("Clicked login button");
        extentReportManager.logInfo(extentReportManager.createTest("Login Button Click"), 
                                 "Clicked login button");
    }

    @When("I enter password {string}")
    public void iEnterPassword(String password) {
        loginPage.enterPassword(password);
        logger.info("Entered password for security test");
        extentReportManager.logInfo(extentReportManager.createTest("Password Security Test"), 
                                 "Entered password for security test");
    }

    @Then("I should be logged in successfully")
    public void iShouldBeLoggedInSuccessfully() {
        boolean isLoggedIn = loginPage.isUserLoggedIn();
        Assert.assertTrue(isLoggedIn, "User should be logged in successfully");
        logger.info("User logged in successfully");
        extentReportManager.logPass(extentReportManager.createTest("Login Success"), 
                                 "User logged in successfully");
    }

    @Then("I should see the dashboard page")
    public void iShouldSeeTheDashboardPage() {
        boolean isDashboardDisplayed = loginPage.isDashboardDisplayed();
        Assert.assertTrue(isDashboardDisplayed, "Dashboard should be displayed");
        logger.info("Dashboard page is displayed");
        extentReportManager.logPass(extentReportManager.createTest("Dashboard Display"), 
                                 "Dashboard page is displayed");
    }

    @Then("I should see an error message")
    public void iShouldSeeAnErrorMessage() {
        boolean isErrorMessageDisplayed = loginPage.isErrorMessageDisplayed();
        Assert.assertTrue(isErrorMessageDisplayed, "Error message should be displayed");
        String errorMessage = loginPage.getErrorMessage();
        logger.info("Error message displayed: {}", errorMessage);
        extentReportManager.logPass(extentReportManager.createTest("Error Message Display"), 
                                 "Error message displayed: " + errorMessage);
    }

    @Then("I should remain on the login page")
    public void iShouldRemainOnTheLoginPage() {
        boolean isOnLoginPage = loginPage.isOnLoginPage();
        Assert.assertTrue(isOnLoginPage, "Should remain on login page");
        logger.info("Remained on login page");
        extentReportManager.logPass(extentReportManager.createTest("Stay on Login Page"), 
                                 "Remained on login page");
    }

    @Then("I should see a validation error for username")
    public void iShouldSeeAValidationErrorForUsername() {
        boolean isUsernameErrorDisplayed = loginPage.isUsernameValidationErrorDisplayed();
        Assert.assertTrue(isUsernameErrorDisplayed, "Username validation error should be displayed");
        String errorMessage = loginPage.getUsernameValidationError();
        logger.info("Username validation error: {}", errorMessage);
        extentReportManager.logPass(extentReportManager.createTest("Username Validation Error"), 
                                 "Username validation error: " + errorMessage);
    }

    @Then("I should see a validation error for password")
    public void iShouldSeeAValidationErrorForPassword() {
        boolean isPasswordErrorDisplayed = loginPage.isPasswordValidationErrorDisplayed();
        Assert.assertTrue(isPasswordErrorDisplayed, "Password validation error should be displayed");
        String errorMessage = loginPage.getPasswordValidationError();
        logger.info("Password validation error: {}", errorMessage);
        extentReportManager.logPass(extentReportManager.createTest("Password Validation Error"), 
                                 "Password validation error: " + errorMessage);
    }

    @Then("the password field should mask the input")
    public void thePasswordFieldShouldMaskTheInput() {
        boolean isPasswordMasked = loginPage.isPasswordFieldMasked();
        Assert.assertTrue(isPasswordMasked, "Password field should mask the input");
        logger.info("Password field masks input correctly");
        extentReportManager.logPass(extentReportManager.createTest("Password Masking"), 
                                 "Password field masks input correctly");
    }

    @Then("the password should not be visible in plain text")
    public void thePasswordShouldNotBeVisibleInPlainText() {
        String passwordValue = loginPage.getPasswordFieldValue();
        Assert.assertNotEquals(passwordValue, currentPassword, "Password should not be visible in plain text");
        logger.info("Password is not visible in plain text");
        extentReportManager.logPass(extentReportManager.createTest("Password Security"), 
                                 "Password is not visible in plain text");
    }

    // Data-driven test using Excel
    @When("I login with data from Excel sheet {string}")
    public void iLoginWithDataFromExcelSheet(String sheetName) {
        List<Map<String, String>> loginData = testDataManager.readExcelData(sheetName);
        
        for (Map<String, String> data : loginData) {
            String username = data.get("Username");
            String password = data.get("Password");
            String expectedResult = data.get("ExpectedResult");
            
            logger.info("Testing login with username: {}, expected result: {}", username, expectedResult);
            
            loginPage.enterUsername(username);
            loginPage.enterPassword(password);
            loginPage.clickLoginButton();
            
            if ("SUCCESS".equals(expectedResult)) {
                Assert.assertTrue(loginPage.isUserLoggedIn(), "Login should be successful");
                logger.info("Login successful for username: {}", username);
            } else {
                Assert.assertTrue(loginPage.isErrorMessageDisplayed(), "Login should fail");
                logger.info("Login failed as expected for username: {}", username);
            }
        }
    }

    // Data-driven test using JSON
    @When("I login with data from JSON")
    public void iLoginWithDataFromJson() {
        Map<String, Object> testData = testDataManager.readJsonAsMap("TestData.json");
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> loginData = (List<Map<String, Object>>) testData.get("loginData");
        
        for (Map<String, Object> data : loginData) {
            String username = (String) data.get("username");
            String password = (String) data.get("password");
            String expectedResult = (String) data.get("expectedResult");
            
            logger.info("Testing login with username: {}, expected result: {}", username, expectedResult);
            
            loginPage.enterUsername(username);
            loginPage.enterPassword(password);
            loginPage.clickLoginButton();
            
            if ("SUCCESS".equals(expectedResult)) {
                Assert.assertTrue(loginPage.isUserLoggedIn(), "Login should be successful");
                logger.info("Login successful for username: {}", username);
            } else {
                Assert.assertTrue(loginPage.isErrorMessageDisplayed(), "Login should fail");
                logger.info("Login failed as expected for username: {}", username);
            }
        }
    }
}