package com.automation.ui;

import com.automation.core.ConfigManager;
import com.automation.core.LoggerManager;
import com.automation.core.WebDriverManager;
import com.automation.reporting.ExtentReportManager;
import com.aventstack.extentreports.ExtentTest;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * Base page class with common UI automation methods (without PageFactory)
 */
public abstract class BasePage {
    protected final Logger logger = LoggerManager.getInstance().getLogger(this.getClass());
    protected final WebDriver driver;
    protected final WebDriverWait wait;
    protected final Actions actions;
    protected final ExtentTest extentTest;
    protected final ConfigManager configManager;

    public BasePage() {
        this.driver = WebDriverManager.getInstance().getDriver();
        this.configManager = ConfigManager.getInstance();
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(configManager.getExplicitWait()));
        this.actions = new Actions(driver);
        this.extentTest = ExtentReportManager.getInstance().createTest(this.getClass().getSimpleName());
    }

    // ===========================================
    // ELEMENT LOCATION METHODS
    // ===========================================

    protected WebElement findElement(By locator) {
        try {
            WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(locator));
            logger.debug("Element found: {}", locator);
            return element;
        } catch (TimeoutException e) {
            logger.error("Element not found within timeout: {}", locator);
            throw e;
        }
    }

    protected List<WebElement> findElements(By locator) {
        try {
            List<WebElement> elements = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(locator));
            logger.debug("Found {} elements: {}", elements.size(), locator);
            return elements;
        } catch (TimeoutException e) {
            logger.error("Elements not found within timeout: {}", locator);
            throw e;
        }
    }

    protected WebElement findClickableElement(By locator) {
        try {
            WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
            logger.debug("Clickable element found: {}", locator);
            return element;
        } catch (TimeoutException e) {
            logger.error("Clickable element not found within timeout: {}", locator);
            throw e;
        }
    }

    protected WebElement findVisibleElement(By locator) {
        try {
            WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
            logger.debug("Visible element found: {}", locator);
            return element;
        } catch (TimeoutException e) {
            logger.error("Visible element not found within timeout: {}", locator);
            throw e;
        }
    }

    // ===========================================
    // ELEMENT INTERACTION METHODS
    // ===========================================

    protected void click(By locator) {
        try {
            WebElement element = findClickableElement(locator);
            element.click();
            logger.info("Clicked element: {}", locator);
            ExtentReportManager.getInstance().logInfo(extentTest, "Clicked element: " + locator);
        } catch (Exception e) {
            logger.error("Failed to click element: {}", locator, e);
            ExtentReportManager.getInstance().logFail(extentTest, "Failed to click element: " + locator, e);
            throw e;
        }
    }

    protected void click(WebElement element) {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(element));
            element.click();
            logger.info("Clicked element: {}", element);
            ExtentReportManager.getInstance().logInfo(extentTest, "Clicked element");
        } catch (Exception e) {
            logger.error("Failed to click element", e);
            ExtentReportManager.getInstance().logFail(extentTest, "Failed to click element", e);
            throw e;
        }
    }

    protected void type(By locator, String text) {
        try {
            WebElement element = findVisibleElement(locator);
            element.clear();
            element.sendKeys(text);
            logger.info("Typed '{}' into element: {}", text, locator);
            ExtentReportManager.getInstance().logInfo(extentTest, "Typed text into element: " + locator);
        } catch (Exception e) {
            logger.error("Failed to type text into element: {}", locator, e);
            ExtentReportManager.getInstance().logFail(extentTest, "Failed to type text into element: " + locator, e);
            throw e;
        }
    }

    protected void type(WebElement element, String text) {
        try {
            wait.until(ExpectedConditions.visibilityOf(element));
            element.clear();
            element.sendKeys(text);
            logger.info("Typed '{}' into element", text);
            ExtentReportManager.getInstance().logInfo(extentTest, "Typed text into element");
        } catch (Exception e) {
            logger.error("Failed to type text into element", e);
            ExtentReportManager.getInstance().logFail(extentTest, "Failed to type text into element", e);
            throw e;
        }
    }

    protected String getText(By locator) {
        try {
            WebElement element = findVisibleElement(locator);
            String text = element.getText();
            logger.info("Got text '{}' from element: {}", text, locator);
            return text;
        } catch (Exception e) {
            logger.error("Failed to get text from element: {}", locator, e);
            ExtentReportManager.getInstance().logFail(extentTest, "Failed to get text from element: " + locator, e);
            throw e;
        }
    }

    protected String getAttribute(By locator, String attribute) {
        try {
            WebElement element = findElement(locator);
            String value = element.getAttribute(attribute);
            logger.info("Got attribute '{}' = '{}' from element: {}", attribute, value, locator);
            return value;
        } catch (Exception e) {
            logger.error("Failed to get attribute '{}' from element: {}", attribute, locator, e);
            ExtentReportManager.getInstance().logFail(extentTest, "Failed to get attribute from element: " + locator, e);
            throw e;
        }
    }

    // ===========================================
    // DROPDOWN METHODS
    // ===========================================

    protected void selectByVisibleText(By locator, String text) {
        try {
            WebElement element = findElement(locator);
            Select select = new Select(element);
            select.selectByVisibleText(text);
            logger.info("Selected '{}' from dropdown: {}", text, locator);
            ExtentReportManager.getInstance().logInfo(extentTest, "Selected from dropdown: " + text);
        } catch (Exception e) {
            logger.error("Failed to select '{}' from dropdown: {}", text, locator, e);
            ExtentReportManager.getInstance().logFail(extentTest, "Failed to select from dropdown: " + text, e);
            throw e;
        }
    }

    protected void selectByValue(By locator, String value) {
        try {
            WebElement element = findElement(locator);
            Select select = new Select(element);
            select.selectByValue(value);
            logger.info("Selected value '{}' from dropdown: {}", value, locator);
            ExtentReportManager.getInstance().logInfo(extentTest, "Selected value from dropdown: " + value);
        } catch (Exception e) {
            logger.error("Failed to select value '{}' from dropdown: {}", value, locator, e);
            ExtentReportManager.getInstance().logFail(extentTest, "Failed to select value from dropdown: " + value, e);
            throw e;
        }
    }

    protected void selectByIndex(By locator, int index) {
        try {
            WebElement element = findElement(locator);
            Select select = new Select(element);
            select.selectByIndex(index);
            logger.info("Selected index '{}' from dropdown: {}", index, locator);
            ExtentReportManager.getInstance().logInfo(extentTest, "Selected index from dropdown: " + index);
        } catch (Exception e) {
            logger.error("Failed to select index '{}' from dropdown: {}", index, locator, e);
            ExtentReportManager.getInstance().logFail(extentTest, "Failed to select index from dropdown: " + index, e);
            throw e;
        }
    }

    // ===========================================
    // WAIT METHODS
    // ===========================================

    protected void waitForElementVisible(By locator) {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
            logger.debug("Element is visible: {}", locator);
        } catch (TimeoutException e) {
            logger.error("Element not visible within timeout: {}", locator);
            throw e;
        }
    }

    protected void waitForElementClickable(By locator) {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(locator));
            logger.debug("Element is clickable: {}", locator);
        } catch (TimeoutException e) {
            logger.error("Element not clickable within timeout: {}", locator);
            throw e;
        }
    }

    protected void waitForElementInvisible(By locator) {
        try {
            wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
            logger.debug("Element is invisible: {}", locator);
        } catch (TimeoutException e) {
            logger.error("Element still visible within timeout: {}", locator);
            throw e;
        }
    }

    // ===========================================
    // VERIFICATION METHODS
    // ===========================================

    protected boolean isElementDisplayed(By locator) {
        try {
            WebElement element = findElement(locator);
            boolean displayed = element.isDisplayed();
            logger.debug("Element display status: {} = {}", locator, displayed);
            return displayed;
        } catch (Exception e) {
            logger.debug("Element not displayed: {}", locator);
            return false;
        }
    }

    protected boolean isElementEnabled(By locator) {
        try {
            WebElement element = findElement(locator);
            boolean enabled = element.isEnabled();
            logger.debug("Element enabled status: {} = {}", locator, enabled);
            return enabled;
        } catch (Exception e) {
            logger.debug("Element not enabled: {}", locator);
            return false;
        }
    }

    protected boolean isElementSelected(By locator) {
        try {
            WebElement element = findElement(locator);
            boolean selected = element.isSelected();
            logger.debug("Element selected status: {} = {}", locator, selected);
            return selected;
        } catch (Exception e) {
            logger.debug("Element not selected: {}", locator);
            return false;
        }
    }

    // ===========================================
    // NAVIGATION METHODS
    // ===========================================

    protected void navigateTo(String url) {
        try {
            driver.get(url);
            logger.info("Navigated to: {}", url);
            ExtentReportManager.getInstance().logInfo(extentTest, "Navigated to: " + url);
        } catch (Exception e) {
            logger.error("Failed to navigate to: {}", url, e);
            ExtentReportManager.getInstance().logFail(extentTest, "Failed to navigate to: " + url, e);
            throw e;
        }
    }

    protected String getCurrentUrl() {
        String url = driver.getCurrentUrl();
        logger.debug("Current URL: {}", url);
        return url;
    }

    protected String getPageTitle() {
        String title = driver.getTitle();
        logger.debug("Page title: {}", title);
        return title;
    }

    // ===========================================
    // SCREENSHOT METHODS
    // ===========================================

    protected String takeScreenshot(String testName) {
        try {
            String screenshotPath = configManager.getProperty("screenshot.path") + "/" + 
                                  testName + "_" + System.currentTimeMillis() + ".png";
            
            TakesScreenshot ts = (TakesScreenshot) driver;
            byte[] screenshot = ts.getScreenshotAs(OutputType.BYTES);
            
            // Save screenshot logic would go here
            logger.info("Screenshot taken: {}", screenshotPath);
            ExtentReportManager.getInstance().addScreenshot(extentTest, screenshotPath, "Screenshot");
            
            return screenshotPath;
        } catch (Exception e) {
            logger.error("Failed to take screenshot", e);
            return null;
        }
    }

    // ===========================================
    // UTILITY METHODS
    // ===========================================

    protected void scrollToElement(By locator) {
        try {
            WebElement element = findElement(locator);
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
            logger.debug("Scrolled to element: {}", locator);
        } catch (Exception e) {
            logger.error("Failed to scroll to element: {}", locator, e);
            throw e;
        }
    }

    protected void scrollToElement(WebElement element) {
        try {
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
            logger.debug("Scrolled to element");
        } catch (Exception e) {
            logger.error("Failed to scroll to element", e);
            throw e;
        }
    }

    protected void hoverOverElement(By locator) {
        try {
            WebElement element = findElement(locator);
            actions.moveToElement(element).perform();
            logger.debug("Hovered over element: {}", locator);
        } catch (Exception e) {
            logger.error("Failed to hover over element: {}", locator, e);
            throw e;
        }
    }

    protected void acceptAlert() {
        try {
            wait.until(ExpectedConditions.alertIsPresent());
            driver.switchTo().alert().accept();
            logger.info("Alert accepted");
        } catch (Exception e) {
            logger.error("Failed to accept alert", e);
            throw e;
        }
    }

    protected void dismissAlert() {
        try {
            wait.until(ExpectedConditions.alertIsPresent());
            driver.switchTo().alert().dismiss();
            logger.info("Alert dismissed");
        } catch (Exception e) {
            logger.error("Failed to dismiss alert", e);
            throw e;
        }
    }

    protected String getAlertText() {
        try {
            wait.until(ExpectedConditions.alertIsPresent());
            String text = driver.switchTo().alert().getText();
            logger.info("Alert text: {}", text);
            return text;
        } catch (Exception e) {
            logger.error("Failed to get alert text", e);
            throw e;
        }
    }
}