package com.automation.steps;

import com.automation.utils.ConfigReader;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * UI step definitions for Cucumber scenarios
 * Provides reusable steps for web UI testing
 */
public class UiSteps extends BaseSteps {
    
    private static final Logger logger = LoggerManager.getLogger(UiSteps.class);
    private WebDriver driver;
    private WebDriverWait wait;
    
    @Given("I navigate to the application")
    public void i_navigate_to_the_application() {
        driver = driverManager.getDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(configReader.getExplicitWait()));
        
        String baseUrl = configReader.getBaseUrl();
        driver.get(baseUrl);
        
        extentReportManager.logInfo("Navigated to application: " + baseUrl);
        logger.info("Navigated to application URL: {}", baseUrl);
    }
    
    @Given("I navigate to {string}")
    public void i_navigate_to(String url) {
        driver = driverManager.getDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(configReader.getExplicitWait()));
        
        driver.get(url);
        
        extentReportManager.logInfo("Navigated to URL: " + url);
        logger.info("Navigated to URL: {}", url);
    }
    
    @When("I click on element with xpath {string}")
    public void i_click_on_element_with_xpath(String xpath) {
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpath)));
        element.click();
        
        extentReportManager.logInfo("Clicked on element with xpath: " + xpath);
        logger.info("Clicked on element with xpath: {}", xpath);
    }
    
    @When("I click on element with id {string}")
    public void i_click_on_element_with_id(String id) {
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(By.id(id)));
        element.click();
        
        extentReportManager.logInfo("Clicked on element with id: " + id);
        logger.info("Clicked on element with id: {}", id);
    }
    
    @When("I enter {string} in field with xpath {string}")
    public void i_enter_in_field_with_xpath(String text, String xpath) {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath)));
        element.clear();
        element.sendKeys(text);
        
        extentReportManager.logInfo("Entered text '" + text + "' in field with xpath: " + xpath);
        logger.info("Entered text '{}' in field with xpath: {}", text, xpath);
    }
    
    @When("I enter {string} in field with id {string}")
    public void i_enter_in_field_with_id(String text, String id) {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(id)));
        element.clear();
        element.sendKeys(text);
        
        extentReportManager.logInfo("Entered text '" + text + "' in field with id: " + id);
        logger.info("Entered text '{}' in field with id: {}", text, id);
    }
    
    @Then("I should see element with xpath {string}")
    public void i_should_see_element_with_xpath(String xpath) {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath)));
        assertThat(element.isDisplayed()).isTrue();
        
        extentReportManager.logPass("Element with xpath is visible: " + xpath);
        logger.info("Element with xpath is visible: {}", xpath);
    }
    
    @Then("I should see element with id {string}")
    public void i_should_see_element_with_id(String id) {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(id)));
        assertThat(element.isDisplayed()).isTrue();
        
        extentReportManager.logPass("Element with id is visible: " + id);
        logger.info("Element with id is visible: {}", id);
    }
    
    @Then("I should see text {string} on the page")
    public void i_should_see_text_on_the_page(String expectedText) {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[contains(text(),'" + expectedText + "')]")));
        assertThat(element.isDisplayed()).isTrue();
        
        extentReportManager.logPass("Text is visible on page: " + expectedText);
        logger.info("Text is visible on page: {}", expectedText);
    }
    
    @Then("the page title should be {string}")
    public void the_page_title_should_be(String expectedTitle) {
        String actualTitle = driver.getTitle();
        assertThat(actualTitle).isEqualTo(expectedTitle);
        
        extentReportManager.logPass("Page title validation passed: " + actualTitle);
        logger.info("Page title validation passed. Expected: {}, Actual: {}", expectedTitle, actualTitle);
    }
    
    @Then("the page title should contain {string}")
    public void the_page_title_should_contain(String expectedText) {
        String actualTitle = driver.getTitle();
        assertThat(actualTitle).contains(expectedText);
        
        extentReportManager.logPass("Page title contains expected text: " + expectedText);
        logger.info("Page title contains expected text. Title: {}, Expected: {}", actualTitle, expectedText);
    }
    
    @Then("the current URL should contain {string}")
    public void the_current_url_should_contain(String expectedUrlPart) {
        String actualUrl = driver.getCurrentUrl();
        assertThat(actualUrl).contains(expectedUrlPart);
        
        extentReportManager.logPass("URL contains expected text: " + expectedUrlPart);
        logger.info("URL contains expected text. URL: {}, Expected: {}", actualUrl, expectedUrlPart);
    }
    
    @When("I wait for {int} seconds")
    public void i_wait_for_seconds(int seconds) {
        try {
            Thread.sleep(seconds * 1000L);
            extentReportManager.logInfo("Waited for " + seconds + " seconds");
            logger.info("Waited for {} seconds", seconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("Wait interrupted", e);
        }
    }
}