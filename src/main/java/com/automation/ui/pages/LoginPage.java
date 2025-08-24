package com.automation.ui.pages;

import com.automation.ui.BasePage;
import org.openqa.selenium.By;
import org.testng.Assert;

/**
 * Login Page Object (without PageFactory)
 */
public class LoginPage extends BasePage {
    
    // Locators
    private final By usernameField = By.id("username");
    private final By passwordField = By.id("password");
    private final By loginButton = By.id("login-button");
    private final By errorMessage = By.className("error-message");
    private final By usernameValidationError = By.id("username-error");
    private final By passwordValidationError = By.id("password-error");
    private final By dashboardElement = By.className("dashboard");
    private final By loginForm = By.id("login-form");

    public void enterUsername(String username) {
        type(usernameField, username);
    }

    public void enterPassword(String password) {
        type(passwordField, password);
    }

    public void clearUsername() {
        findElement(usernameField).clear();
    }

    public void clearPassword() {
        findElement(passwordField).clear();
    }

    public void clickLoginButton() {
        click(loginButton);
    }

    public boolean isLoginFormDisplayed() {
        return isElementDisplayed(loginForm);
    }

    public boolean isUserLoggedIn() {
        // Check if we're redirected to dashboard or if user-specific elements are present
        return isElementDisplayed(dashboardElement) || 
               getCurrentUrl().contains("/dashboard") ||
               getCurrentUrl().contains("/home");
    }

    public boolean isDashboardDisplayed() {
        return isElementDisplayed(dashboardElement);
    }

    public boolean isErrorMessageDisplayed() {
        return isElementDisplayed(errorMessage);
    }

    public String getErrorMessage() {
        return getText(errorMessage);
    }

    public boolean isUsernameValidationErrorDisplayed() {
        return isElementDisplayed(usernameValidationError);
    }

    public String getUsernameValidationError() {
        return getText(usernameValidationError);
    }

    public boolean isPasswordValidationErrorDisplayed() {
        return isElementDisplayed(passwordValidationError);
    }

    public String getPasswordValidationError() {
        return getText(passwordValidationError);
    }

    public boolean isOnLoginPage() {
        return getCurrentUrl().contains("/login") && isLoginFormDisplayed();
    }

    public boolean isPasswordFieldMasked() {
        String type = getAttribute(passwordField, "type");
        return "password".equals(type);
    }

    public String getPasswordFieldValue() {
        return getAttribute(passwordField, "value");
    }

    public void waitForLoginForm() {
        waitForElementVisible(loginForm);
    }

    public void waitForErrorMessage() {
        waitForElementVisible(errorMessage);
    }

    public void waitForDashboard() {
        waitForElementVisible(dashboardElement);
    }
}