package com.automation.utils;

import com.automation.constants.FrameworkConstants;
import com.automation.core.DriverManager;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Screenshot utility for capturing and managing screenshots during test execution
 * Supports full page screenshots and element-specific screenshots
 */
public class ScreenshotManager {
    
    private static final Logger logger = LogManager.getLogger(ScreenshotManager.class);
    private static ScreenshotManager instance;
    private final ConfigReader configReader;
    
    private ScreenshotManager() {
        this.configReader = ConfigReader.getInstance();
        createScreenshotDirectory();
    }
    
    public static ScreenshotManager getInstance() {
        if (instance == null) {
            synchronized (ScreenshotManager.class) {
                if (instance == null) {
                    instance = new ScreenshotManager();
                }
            }
        }
        return instance;
    }
    
    /**
     * Create screenshot directory if it doesn't exist
     */
    private void createScreenshotDirectory() {
        File screenshotDir = new File(FrameworkConstants.SCREENSHOTS_PATH);
        if (!screenshotDir.exists()) {
            screenshotDir.mkdirs();
            logger.info("Screenshot directory created: {}", FrameworkConstants.SCREENSHOTS_PATH);
        }
    }
    
    /**
     * Capture full page screenshot
     * @param testName Test name for screenshot filename
     * @return Path to the saved screenshot
     */
    public String captureScreenshot(String testName) {
        try {
            WebDriver driver = DriverManager.getInstance().getDriver();
            
            if (driver == null) {
                logger.warn("WebDriver is null, cannot capture screenshot");
                return null;
            }
            
            TakesScreenshot takesScreenshot = (TakesScreenshot) driver;
            File sourceFile = takesScreenshot.getScreenshotAs(OutputType.FILE);
            
            String fileName = generateScreenshotFileName(testName);
            String filePath = FrameworkConstants.SCREENSHOTS_PATH + fileName;
            File destinationFile = new File(filePath);
            
            FileUtils.copyFile(sourceFile, destinationFile);
            
            logger.info("Screenshot captured successfully: {}", filePath);
            return filePath;
            
        } catch (Exception e) {
            logger.error("Failed to capture screenshot for test: {}", testName, e);
            return null;
        }
    }
    
    /**
     * Capture screenshot of specific web element
     * @param element WebElement to capture
     * @param testName Test name for screenshot filename
     * @return Path to the saved screenshot
     */
    public String captureElementScreenshot(WebElement element, String testName) {
        try {
            if (element == null) {
                logger.warn("WebElement is null, cannot capture element screenshot");
                return null;
            }
            
            File sourceFile = element.getScreenshotAs(OutputType.FILE);
            
            String fileName = generateScreenshotFileName(testName + "_element");
            String filePath = FrameworkConstants.SCREENSHOTS_PATH + fileName;
            File destinationFile = new File(filePath);
            
            FileUtils.copyFile(sourceFile, destinationFile);
            
            logger.info("Element screenshot captured successfully: {}", filePath);
            return filePath;
            
        } catch (Exception e) {
            logger.error("Failed to capture element screenshot for test: {}", testName, e);
            return null;
        }
    }
    
    /**
     * Capture screenshot with custom filename
     * @param customFileName Custom filename without extension
     * @return Path to the saved screenshot
     */
    public String captureScreenshotWithCustomName(String customFileName) {
        try {
            WebDriver driver = DriverManager.getInstance().getDriver();
            
            if (driver == null) {
                logger.warn("WebDriver is null, cannot capture screenshot");
                return null;
            }
            
            TakesScreenshot takesScreenshot = (TakesScreenshot) driver;
            File sourceFile = takesScreenshot.getScreenshotAs(OutputType.FILE);
            
            String fileName = sanitizeFileName(customFileName) + "_" + getCurrentTimestamp() + ".png";
            String filePath = FrameworkConstants.SCREENSHOTS_PATH + fileName;
            File destinationFile = new File(filePath);
            
            FileUtils.copyFile(sourceFile, destinationFile);
            
            logger.info("Screenshot captured with custom name: {}", filePath);
            return filePath;
            
        } catch (Exception e) {
            logger.error("Failed to capture screenshot with custom name: {}", customFileName, e);
            return null;
        }
    }
    
    /**
     * Capture screenshot on test failure
     * @param testName Test name
     * @return Path to the saved screenshot
     */
    public String captureFailureScreenshot(String testName) {
        if (configReader.getBooleanProperty("take.screenshot.on.failure", true)) {
            String screenshotPath = captureScreenshot(testName + "_FAILURE");
            if (screenshotPath != null) {
                logger.info("Failure screenshot captured for test: {}", testName);
            }
            return screenshotPath;
        }
        return null;
    }
    
    /**
     * Capture screenshot on test pass (if enabled)
     * @param testName Test name
     * @return Path to the saved screenshot
     */
    public String capturePassScreenshot(String testName) {
        if (configReader.getBooleanProperty("take.screenshot.on.pass", false)) {
            String screenshotPath = captureScreenshot(testName + "_PASS");
            if (screenshotPath != null) {
                logger.info("Pass screenshot captured for test: {}", testName);
            }
            return screenshotPath;
        }
        return null;
    }
    
    /**
     * Generate screenshot filename with timestamp
     * @param testName Test name
     * @return Generated filename
     */
    private String generateScreenshotFileName(String testName) {
        String sanitizedTestName = sanitizeFileName(testName);
        String timestamp = getCurrentTimestamp();
        return sanitizedTestName + "_" + timestamp + ".png";
    }
    
    /**
     * Sanitize filename by removing invalid characters
     * @param fileName Original filename
     * @return Sanitized filename
     */
    private String sanitizeFileName(String fileName) {
        if (fileName == null || fileName.trim().isEmpty()) {
            return "screenshot";
        }
        
        // Remove or replace invalid characters for filename
        return fileName.replaceAll("[^a-zA-Z0-9._-]", "_")
                      .replaceAll("_{2,}", "_")  // Replace multiple underscores with single
                      .trim();
    }
    
    /**
     * Get current timestamp for filename
     * @return Formatted timestamp string
     */
    private String getCurrentTimestamp() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss-SSS"));
    }
    
    /**
     * Get screenshot as Base64 string (useful for embedding in reports)
     * @param testName Test name
     * @return Base64 encoded screenshot string
     */
    public String captureScreenshotAsBase64(String testName) {
        try {
            WebDriver driver = DriverManager.getInstance().getDriver();
            
            if (driver == null) {
                logger.warn("WebDriver is null, cannot capture screenshot");
                return null;
            }
            
            TakesScreenshot takesScreenshot = (TakesScreenshot) driver;
            String base64Screenshot = takesScreenshot.getScreenshotAs(OutputType.BASE64);
            
            logger.info("Screenshot captured as Base64 for test: {}", testName);
            return base64Screenshot;
            
        } catch (Exception e) {
            logger.error("Failed to capture screenshot as Base64 for test: {}", testName, e);
            return null;
        }
    }
    
    /**
     * Save Base64 screenshot to file
     * @param base64Screenshot Base64 encoded screenshot
     * @param testName Test name
     * @return Path to the saved screenshot
     */
    public String saveBase64Screenshot(String base64Screenshot, String testName) {
        try {
            if (base64Screenshot == null || base64Screenshot.isEmpty()) {
                logger.warn("Base64 screenshot is null or empty");
                return null;
            }
            
            byte[] decodedBytes = java.util.Base64.getDecoder().decode(base64Screenshot);
            
            String fileName = generateScreenshotFileName(testName);
            String filePath = FrameworkConstants.SCREENSHOTS_PATH + fileName;
            File destinationFile = new File(filePath);
            
            FileUtils.writeByteArrayToFile(destinationFile, decodedBytes);
            
            logger.info("Base64 screenshot saved to file: {}", filePath);
            return filePath;
            
        } catch (Exception e) {
            logger.error("Failed to save Base64 screenshot for test: {}", testName, e);
            return null;
        }
    }
    
    /**
     * Clean up old screenshots (older than specified days)
     * @param daysToKeep Number of days to keep screenshots
     */
    public void cleanupOldScreenshots(int daysToKeep) {
        try {
            File screenshotDir = new File(FrameworkConstants.SCREENSHOTS_PATH);
            if (!screenshotDir.exists()) {
                return;
            }
            
            long cutoffTime = System.currentTimeMillis() - (daysToKeep * 24 * 60 * 60 * 1000L);
            File[] files = screenshotDir.listFiles();
            
            if (files != null) {
                int deletedCount = 0;
                for (File file : files) {
                    if (file.isFile() && file.lastModified() < cutoffTime) {
                        if (file.delete()) {
                            deletedCount++;
                        }
                    }
                }
                logger.info("Cleaned up {} old screenshots (older than {} days)", deletedCount, daysToKeep);
            }
            
        } catch (Exception e) {
            logger.error("Failed to cleanup old screenshots", e);
        }
    }
    
    /**
     * Get relative path for screenshot (useful for reports)
     * @param absolutePath Absolute path to screenshot
     * @return Relative path from project root
     */
    public String getRelativePath(String absolutePath) {
        if (absolutePath == null) {
            return null;
        }
        
        String projectPath = System.getProperty("user.dir");
        if (absolutePath.startsWith(projectPath)) {
            return absolutePath.substring(projectPath.length() + 1).replace("\\", "/");
        }
        
        return absolutePath;
    }
}