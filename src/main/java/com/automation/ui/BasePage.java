package com.automation.ui;

import com.automation.core.ConfigManager;
import com.automation.core.CustomDriverManager;
import com.automation.core.LoggerManager;
import com.automation.reporting.ExtentReportManager;
import com.aventstack.extentreports.ExtentTest;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Map;

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
        this.driver = CustomDriverManager.getInstance().getDriver();
        this.configManager = ConfigManager.getInstance();
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(configManager.getExplicitWait()));
        this.actions = new Actions(driver);
        this.extentTest = ExtentReportManager.getInstance().createTest(this.getClass().getSimpleName());
    }

    // ===========================================
    // ELEMENT LOCATION METHODS
    // ===========================================

    protected WebElement findElement(By locator) {
        return findElementWithRetry(locator, 3);
    }

    protected WebElement findElementWithRetry(By locator, int maxRetries) {
        int attempts = 0;
        while (attempts < maxRetries) {
            try {
                WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(locator));
                logger.debug("Element found: {} (attempt {})", locator, attempts + 1);
                return element;
            } catch (StaleElementReferenceException e) {
                attempts++;
                if (attempts >= maxRetries) {
                    logger.error("Stale element encountered, max retries reached: {}", locator);
                    throw e;
                }
                logger.warn("Stale element encountered, retrying... Attempt: {}/{}", attempts, maxRetries);
                try {
                    Thread.sleep(1000); // Brief pause before retry
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    break;
                }
            } catch (TimeoutException e) {
                logger.error("Element not found within timeout: {}", locator);
                throw e;
            }
        }
        throw new RuntimeException("Element not found after " + maxRetries + " attempts: " + locator);
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
        clickWithRetry(locator, 3);
    }

    protected void clickWithRetry(By locator, int maxRetries) {
        clickWithFallback(locator, maxRetries, true);
    }

    protected void clickWithFallback(By locator, int maxRetries, boolean enableFallbacks) {
        int attempts = 0;
        Exception lastException = null;

        while (attempts < maxRetries) {
            try {
                WebElement element = findClickableElement(locator);
                element.click();
                logger.info("Clicked element: {} (attempt {})", locator, attempts + 1);
                ExtentReportManager.getInstance().logInfo(extentTest, "Clicked element: " + locator);
                return;
            } catch (StaleElementReferenceException e) {
                lastException = e;
                attempts++;
                if (attempts >= maxRetries) {
                    logger.warn("Stale element on click, trying fallback mechanisms...");
                    break;
                }
                logger.warn("Stale element on click, retrying... Attempt: {}/{}", attempts, maxRetries);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    break;
                }
            } catch (Exception e) {
                lastException = e;
                attempts++;
                if (attempts >= maxRetries) {
                    logger.warn("Click failed, trying fallback mechanisms...");
                    break;
                }
                logger.warn("Click failed, retrying... Attempt: {}/{}", attempts, maxRetries);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }

        // Try fallback mechanisms if enabled
        if (enableFallbacks) {
            try {
                clickWithFallbackMechanisms(locator);
                return;
            } catch (Exception fallbackException) {
                logger.error("All fallback mechanisms failed for: {}", locator);
                ExtentReportManager.getInstance().logFail(extentTest, "All click mechanisms failed: " + locator, fallbackException);
                throw new RuntimeException("Failed to click element after all attempts and fallbacks: " + locator, fallbackException);
            }
        }

        // If no fallbacks enabled, throw original exception
        logger.error("Failed to click element after {} attempts: {}", maxRetries, locator);
        ExtentReportManager.getInstance().logFail(extentTest, "Failed to click element: " + locator, lastException);
        throw new RuntimeException("Failed to click element after " + maxRetries + " attempts: " + locator, lastException);
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

    /**
     * Comprehensive fallback mechanisms for clicking elements
     * Tries multiple strategies when standard click fails
     */
    protected void clickWithFallbackMechanisms(By locator) {
        logger.info("Attempting fallback mechanisms for clicking: {}", locator);
        
        // Strategy 1: Try to find element with alternative locators
        try {
            WebElement element = findElementWithAlternativeLocators(locator);
            if (element != null) {
                element.click();
                logger.info("Successfully clicked using alternative locator strategy");
                return;
            }
        } catch (Exception e) {
            logger.debug("Alternative locator strategy failed: {}", e.getMessage());
        }

        // Strategy 2: Try JavaScript click
        try {
            clickWithJavaScript(locator);
            logger.info("Successfully clicked using JavaScript");
            return;
        } catch (Exception e) {
            logger.debug("JavaScript click failed: {}", e.getMessage());
        }

        // Strategy 3: Try Actions class click
        try {
            clickWithActions(locator);
            logger.info("Successfully clicked using Actions class");
            return;
        } catch (Exception e) {
            logger.debug("Actions class click failed: {}", e.getMessage());
        }

        // Strategy 4: Try scrolling to element first, then click
        try {
            scrollToElement(locator);
            WebElement element = findElement(locator);
            element.click();
            logger.info("Successfully clicked after scrolling to element");
            return;
        } catch (Exception e) {
            logger.debug("Scroll and click strategy failed: {}", e.getMessage());
        }

        // Strategy 5: Try Robot class (last resort)
        try {
            clickWithRobot(locator);
            logger.info("Successfully clicked using Robot class");
            return;
        } catch (Exception e) {
            logger.debug("Robot class click failed: {}", e.getMessage());
        }

        // If all strategies fail, throw exception
        throw new RuntimeException("All fallback mechanisms failed for clicking element: " + locator);
    }

    /**
     * Find element using alternative locator strategies
     */
    protected WebElement findElementWithAlternativeLocators(By originalLocator) {
        try {
            // Try to extract information from original locator
            String locatorString = originalLocator.toString();
            
            // If it's an ID locator, try name and other attributes
            if (locatorString.contains("By.id:")) {
                String id = locatorString.replaceAll(".*By\\.id: (.+)\\]", "$1");
                try {
                    return findElement(By.name(id));
                } catch (Exception e) {
                    try {
                        return findElement(By.cssSelector("[data-testid='" + id + "']"));
                    } catch (Exception e2) {
                        try {
                            return findElement(By.xpath("//*[@id='" + id + "' or @name='" + id + "' or contains(@class, '" + id + "')]"));
                        } catch (Exception e3) {
                            // Try partial text match
                            return findElement(By.xpath("//*[contains(text(), '" + id + "')]"));
                        }
                    }
                }
            }
            
            // If it's a CSS locator, try XPath
            if (locatorString.contains("By.cssSelector:")) {
                String css = locatorString.replaceAll(".*By\\.cssSelector: (.+)\\]", "$1");
                try {
                    return findElement(By.xpath("//*[contains(@class, '" + css.replaceAll("\\..*", "").replaceAll("\\.", "") + "')]"));
                } catch (Exception e) {
                    // Try other strategies
                }
            }
            
            // If it's an XPath, try to simplify it
            if (locatorString.contains("By.xpath:")) {
                String xpath = locatorString.replaceAll(".*By\\.xpath: (.+)\\]", "$1");
                try {
                    // Try simplified XPath
                    String simplifiedXPath = xpath.replaceAll("\\[\\d+\\]", ""); // Remove indices
                    return findElement(By.xpath(simplifiedXPath));
                } catch (Exception e) {
                    // Try to find by tag name only
                    try {
                        String tagName = xpath.replaceAll(".*//([a-zA-Z]+).*", "$1");
                        return findElement(By.tagName(tagName));
                    } catch (Exception e2) {
                        // Last resort: try to find any element with similar text
                        return findElement(By.xpath("//*[contains(text(), '')]"));
                    }
                }
            }
            
        } catch (Exception e) {
            logger.debug("Alternative locator strategy failed: {}", e.getMessage());
        }
        
        return null;
    }

    /**
     * Click element using JavaScript
     */
    protected void clickWithJavaScript(By locator) {
        try {
            WebElement element = findElement(locator);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
            logger.info("JavaScript click successful for: {}", locator);
        } catch (Exception e) {
            logger.debug("JavaScript click failed: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Click element using Actions class
     */
    protected void clickWithActions(By locator) {
        try {
            WebElement element = findElement(locator);
            actions.moveToElement(element).click().perform();
            logger.info("Actions class click successful for: {}", locator);
        } catch (Exception e) {
            logger.debug("Actions class click failed: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Click element using Robot class (coordinates-based)
     */
    protected void clickWithRobot(By locator) {
        try {
            WebElement element = findElement(locator);
            Point location = element.getLocation();
            Dimension size = element.getSize();
            
            // Calculate center point of element
            int centerX = location.getX() + (size.getWidth() / 2);
            int centerY = location.getY() + (size.getHeight() / 2);
            
            // Use Robot to click at coordinates
            try {
                java.awt.Robot robot = new java.awt.Robot();
                robot.mouseMove(centerX, centerY);
                robot.mousePress(java.awt.event.InputEvent.BUTTON1_DOWN_MASK);
                robot.mouseRelease(java.awt.event.InputEvent.BUTTON1_DOWN_MASK);
                
                logger.info("Robot click successful for: {} at coordinates ({}, {})", locator, centerX, centerY);
            } catch (java.awt.AWTException awtException) {
                logger.debug("Robot AWT exception: {}", awtException.getMessage());
                throw new RuntimeException("Robot click failed due to AWT exception", awtException);
            }
        } catch (Exception e) {
            logger.debug("Robot click failed: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Comprehensive fallback mechanisms for typing text
     */
    protected void typeWithFallbackMechanisms(By locator, String text) {
        logger.info("Attempting fallback mechanisms for typing: {}", locator);
        
        // Strategy 1: Try to find element with alternative locators
        try {
            WebElement element = findElementWithAlternativeLocators(locator);
            if (element != null) {
                element.clear();
                element.sendKeys(text);
                logger.info("Successfully typed using alternative locator strategy");
                return;
            }
        } catch (Exception e) {
            logger.debug("Alternative locator strategy failed: {}", e.getMessage());
        }

        // Strategy 2: Try JavaScript to set value
        try {
            typeWithJavaScript(locator, text);
            logger.info("Successfully typed using JavaScript");
            return;
        } catch (Exception e) {
            logger.debug("JavaScript type failed: {}", e.getMessage());
        }

        // Strategy 3: Try Actions class to type
        try {
            typeWithActions(locator, text);
            logger.info("Successfully typed using Actions class");
            return;
        } catch (Exception e) {
            logger.debug("Actions class type failed: {}", e.getMessage());
        }

        // Strategy 4: Try scrolling to element first, then type
        try {
            scrollToElement(locator);
            WebElement element = findElement(locator);
            element.clear();
            element.sendKeys(text);
            logger.info("Successfully typed after scrolling to element");
            return;
        } catch (Exception e) {
            logger.debug("Scroll and type strategy failed: {}", e.getMessage());
        }

        // Strategy 5: Try Robot class (last resort)
        try {
            typeWithRobot(locator, text);
            logger.info("Successfully typed using Robot class");
            return;
        } catch (Exception e) {
            logger.debug("Robot class type failed: {}", e.getMessage());
        }

        // If all strategies fail, throw exception
        throw new RuntimeException("All fallback mechanisms failed for typing text: " + locator);
    }

    /**
     * Type text using JavaScript
     */
    protected void typeWithJavaScript(By locator, String text) {
        try {
            WebElement element = findElement(locator);
            ((JavascriptExecutor) driver).executeScript("arguments[0].value = '';", element);
            ((JavascriptExecutor) driver).executeScript("arguments[0].value = arguments[1];", element, text);
            logger.info("JavaScript type successful for: {}", locator);
        } catch (Exception e) {
            logger.debug("JavaScript type failed: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Type text using Actions class
     */
    protected void typeWithActions(By locator, String text) {
        try {
            WebElement element = findElement(locator);
            actions.moveToElement(element).click().sendKeys(text).perform();
            logger.info("Actions class type successful for: {}", locator);
        } catch (Exception e) {
            logger.debug("Actions class type failed: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Type text using Robot class (coordinates-based)
     */
    protected void typeWithRobot(By locator, String text) {
        try {
            WebElement element = findElement(locator);
            Point location = element.getLocation();
            Dimension size = element.getSize();
            
            // Calculate center point of element
            int centerX = location.getX() + (size.getWidth() / 2);
            int centerY = location.getY() + (size.getHeight() / 2);
            
            // Use Robot to click and type
            try {
                java.awt.Robot robot = new java.awt.Robot();
                robot.mouseMove(centerX, centerY);
                robot.mousePress(java.awt.event.InputEvent.BUTTON1_DOWN_MASK);
                robot.mouseRelease(java.awt.event.InputEvent.BUTTON1_DOWN_MASK);
                
                // Type the text character by character
                for (char c : text.toCharArray()) {
                    if (Character.isUpperCase(c)) {
                        robot.keyPress(java.awt.event.KeyEvent.VK_SHIFT);
                        robot.keyPress(Character.toUpperCase(c));
                        robot.keyRelease(Character.toUpperCase(c));
                        robot.keyRelease(java.awt.event.KeyEvent.VK_SHIFT);
                    } else {
                        robot.keyPress(Character.toUpperCase(c));
                        robot.keyRelease(Character.toUpperCase(c));
                    }
                    robot.delay(50); // Small delay between characters
                }
                
                logger.info("Robot type successful for: {} at coordinates ({}, {})", locator, centerX, centerY);
            } catch (java.awt.AWTException awtException) {
                logger.debug("Robot AWT exception: {}", awtException.getMessage());
                throw new RuntimeException("Robot type failed due to AWT exception", awtException);
            }
        } catch (Exception e) {
            logger.debug("Robot type failed: {}", e.getMessage());
            throw e;
        }
    }

    protected void type(By locator, String text) {
        typeWithRetry(locator, text, 3);
    }

    protected void typeWithRetry(By locator, String text, int maxRetries) {
        typeWithFallback(locator, text, maxRetries, true);
    }

    protected void typeWithFallback(By locator, String text, int maxRetries, boolean enableFallbacks) {
        int attempts = 0;
        Exception lastException = null;

        while (attempts < maxRetries) {
            try {
                WebElement element = findVisibleElement(locator);
                element.clear();
                element.sendKeys(text);
                logger.info("Typed '{}' into element: {} (attempt {})", text, locator, attempts + 1);
                ExtentReportManager.getInstance().logInfo(extentTest, "Typed text into element: " + locator);
                return;
            } catch (StaleElementReferenceException e) {
                lastException = e;
                attempts++;
                if (attempts >= maxRetries) {
                    logger.warn("Stale element on type, trying fallback mechanisms...");
                    break;
                }
                logger.warn("Stale element on type, retrying... Attempt: {}/{}", attempts, maxRetries);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    break;
                }
            } catch (Exception e) {
                lastException = e;
                attempts++;
                if (attempts >= maxRetries) {
                    logger.warn("Type failed, trying fallback mechanisms...");
                    break;
                }
                logger.warn("Type failed, retrying... Attempt: {}/{}", attempts, maxRetries);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }

        // Try fallback mechanisms if enabled
        if (enableFallbacks) {
            try {
                typeWithFallbackMechanisms(locator, text);
                return;
            } catch (Exception fallbackException) {
                logger.error("All fallback mechanisms failed for typing: {}", locator);
                ExtentReportManager.getInstance().logFail(extentTest, "All type mechanisms failed: " + locator, fallbackException);
                throw new RuntimeException("Failed to type text after all attempts and fallbacks: " + locator, fallbackException);
            }
        }

        // If no fallbacks enabled, throw original exception
        logger.error("Failed to type text after {} attempts: {}", maxRetries, locator);
        ExtentReportManager.getInstance().logFail(extentTest, "Failed to type text: " + locator, lastException);
        throw new RuntimeException("Failed to type text after " + maxRetries + " attempts: " + locator, lastException);
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
            
            // Save screenshot to file
            java.nio.file.Files.write(java.nio.file.Paths.get(screenshotPath), screenshot);
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

    // ===========================================
    // ALTERNATIVE ELEMENT IDENTIFICATION METHODS
    // ===========================================

    /**
     * Try multiple locator strategies to find an element
     */
    protected WebElement findElementByMultipleStrategies(String identifier) {
        // Try ID first
        try {
            return findElement(By.id(identifier));
        } catch (Exception e) {
            logger.debug("Element not found by ID: {}", identifier);
        }
        
        // Try name
        try {
            return findElement(By.name(identifier));
        } catch (Exception e) {
            logger.debug("Element not found by name: {}", identifier);
        }
        
        // Try partial text
        try {
            return findElement(By.xpath("//*[contains(text(),'" + identifier + "')]"));
        } catch (Exception e) {
            logger.debug("Element not found by partial text: {}", identifier);
        }
        
        // Try CSS selector with data attributes
        try {
            return findElement(By.cssSelector("[data-testid='" + identifier + "']"));
        } catch (Exception e) {
            logger.debug("Element not found by data-testid: {}", identifier);
        }
        
        // Try aria-label
        try {
            return findElement(By.cssSelector("[aria-label='" + identifier + "']"));
        } catch (Exception e) {
            logger.debug("Element not found by aria-label: {}", identifier);
        }
        
        throw new RuntimeException("Element not found using any strategy for identifier: " + identifier);
    }

    /**
     * Find element by dynamic XPath with multiple attributes
     */
    protected WebElement findElementByAttributes(String tag, Map<String, String> attributes) {
        StringBuilder xpath = new StringBuilder("//" + tag);
        for (Map.Entry<String, String> attr : attributes.entrySet()) {
            xpath.append("[@").append(attr.getKey()).append("='").append(attr.getValue()).append("']");
        }
        return findElement(By.xpath(xpath.toString()));
    }

    /**
     * Find element by partial attribute value
     */
    protected WebElement findElementByPartialAttribute(String tag, String attribute, String partialValue) {
        String xpath = String.format("//%s[contains(@%s, '%s')]", tag, attribute, partialValue);
        return findElement(By.xpath(xpath));
    }

    /**
     * Find element by text content (exact match)
     */
    protected WebElement findElementByExactText(String text) {
        return findElement(By.xpath("//*[text()='" + text + "']"));
    }

    /**
     * Find element by text content (contains)
     */
    protected WebElement findElementByContainsText(String text) {
        return findElement(By.xpath("//*[contains(text(),'" + text + "')]"));
    }

    /**
     * Find element by parent-child relationship
     */
    protected WebElement findElementByParentChild(String parentTag, String parentAttribute, String parentValue, String childTag) {
        String xpath = String.format("//%s[@%s='%s']//%s", parentTag, parentAttribute, parentValue, childTag);
        return findElement(By.xpath(xpath));
    }

    /**
     * Execute JavaScript to find element
     */
    protected WebElement findElementByJavaScript(String script) {
        try {
            return (WebElement) ((JavascriptExecutor) driver).executeScript(script);
        } catch (Exception e) {
            logger.error("Failed to find element by JavaScript: {}", script, e);
            throw e;
        }
    }

    /**
     * Refresh element reference to avoid stale element issues
     */
    protected WebElement refreshElementReference(By locator) {
        try {
            // Wait a bit for DOM to stabilize
            Thread.sleep(500);
            return findElement(locator);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Thread interrupted while refreshing element", e);
        }
    }

    /**
     * Wait for element to be refreshed and stable
     */
    protected WebElement waitForElementRefresh(By locator, int maxWaitSeconds) {
        long endTime = System.currentTimeMillis() + (maxWaitSeconds * 1000L);
        while (System.currentTimeMillis() < endTime) {
            try {
                WebElement element = findElement(locator);
                // Try to interact with element to verify it's stable
                element.isDisplayed();
                return element;
            } catch (StaleElementReferenceException e) {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
        throw new RuntimeException("Element not stable after " + maxWaitSeconds + " seconds: " + locator);
    }

    // ===========================================
    // SMART EXCEPTION HANDLING WITH FALLBACKS
    // ===========================================

    /**
     * Smart element interaction with comprehensive exception handling
     * Automatically tries multiple strategies when standard methods fail
     */
    protected void smartClick(By locator) {
        try {
            // Try standard click first
            click(locator);
        } catch (Exception e) {
            logger.warn("Standard click failed, trying smart fallbacks: {}", e.getMessage());
            try {
                clickWithFallback(locator, 2, true);
            } catch (Exception fallbackException) {
                logger.error("All click strategies failed for: {}", locator);
                throw new RuntimeException("Smart click failed after all strategies: " + locator, fallbackException);
            }
        }
    }

    /**
     * Smart text input with comprehensive exception handling
     */
    protected void smartType(By locator, String text) {
        try {
            // Try standard type first
            type(locator, text);
        } catch (Exception e) {
            logger.warn("Standard type failed, trying smart fallbacks: {}", e.getMessage());
            try {
                typeWithFallback(locator, text, 2, true);
            } catch (Exception fallbackException) {
                logger.error("All type strategies failed for: {}", locator);
                throw new RuntimeException("Smart type failed after all strategies: " + locator, fallbackException);
            }
        }
    }

    /**
     * Smart element finding with multiple fallback strategies
     */
    protected WebElement smartFindElement(By locator) {
        try {
            return findElement(locator);
        } catch (Exception e) {
            logger.warn("Standard element finding failed, trying alternatives: {}", e.getMessage());
            
            // Try alternative locators
            try {
                return findElementWithAlternativeLocators(locator);
            } catch (Exception e2) {
                // Try JavaScript execution
                try {
                    return findElementByJavaScript("return document.querySelector('" + locator.toString().replaceAll(".*: (.+)\\]", "$1") + "')");
                } catch (Exception e3) {
                    // Try waiting for element refresh
                    try {
                        return waitForElementRefresh(locator, 5);
                    } catch (Exception e4) {
                        throw new RuntimeException("All element finding strategies failed for: " + locator, e);
                    }
                }
            }
        }
    }

    /**
     * Handle any Selenium exception with smart recovery
     */
    protected WebElement handleSeleniumException(By locator, String operation) {
        try {
            return findElement(locator);
        } catch (StaleElementReferenceException e) {
            logger.info("Handling stale element exception for: {}", locator);
            return waitForElementRefresh(locator, 3);
        } catch (org.openqa.selenium.NoSuchElementException e) {
            logger.info("Handling no such element exception for: {}", locator);
            return findElementWithAlternativeLocators(locator);
        } catch (org.openqa.selenium.TimeoutException e) {
            logger.info("Handling timeout exception for: {}", locator);
            return waitForElementRefresh(locator, 5);
        } catch (org.openqa.selenium.ElementNotInteractableException e) {
            logger.info("Handling element not interactable exception for: {}", locator);
            scrollToElement(locator);
            return findElement(locator);
        } catch (Exception e) {
            logger.error("Unhandled exception for: {}", locator, e);
            throw e;
        }
    }

    /**
     * Universal element interaction with automatic exception handling
     */
    protected void universalClick(By locator) {
        try {
            WebElement element = handleSeleniumException(locator, "click");
            element.click();
            logger.info("Universal click successful for: {}", locator);
        } catch (Exception e) {
            logger.warn("Universal click failed, trying fallback mechanisms: {}", e.getMessage());
            clickWithFallbackMechanisms(locator);
        }
    }

    /**
     * Universal text input with automatic exception handling
     */
    protected void universalType(By locator, String text) {
        try {
            WebElement element = handleSeleniumException(locator, "type");
            element.clear();
            element.sendKeys(text);
            logger.info("Universal type successful for: {}", locator);
        } catch (Exception e) {
            logger.warn("Universal type failed, trying fallback mechanisms: {}", e.getMessage());
            typeWithFallbackMechanisms(locator, text);
        }
    }
}