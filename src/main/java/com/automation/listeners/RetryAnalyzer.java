package com.automation.listeners;

import com.automation.core.ConfigManager;
import com.automation.core.LoggerManager;
import org.apache.logging.log4j.Logger;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

/**
 * Retry Analyzer for TestNG to retry failed tests
 * Implements IRetryAnalyzer interface to provide retry functionality
 */
public class RetryAnalyzer implements IRetryAnalyzer {
    private static final Logger logger = LoggerManager.getInstance().getLogger(RetryAnalyzer.class);
    private final ConfigManager configManager;
    private int retryCount = 0;
    private final int maxRetryCount;

    public RetryAnalyzer() {
        this.configManager = ConfigManager.getInstance();
        this.maxRetryCount = configManager.getIntProperty("retry.count", 2);
    }

    @Override
    public boolean retry(ITestResult result) {
        if (retryCount < maxRetryCount) {
            retryCount++;
            logger.warn("Retrying test: {} - Attempt: {}/{}", 
                       result.getName(), retryCount, maxRetryCount + 1);
            
            // Log retry information to Allure
            logRetryToAllure(result, retryCount);
            
            return true;
        }
        
        logger.error("Test: {} failed after {} retry attempts", result.getName(), maxRetryCount);
        return false;
    }

    private void logRetryToAllure(ITestResult result, int attempt) {
        try {
            // Add retry information to Allure report
            // This will be automatically captured by Allure TestNG listener
            result.setAttribute("retry_attempt", attempt);
            result.setAttribute("max_retries", maxRetryCount);
            
            logger.info("Retry attempt {} logged for Allure reporting", attempt);
        } catch (Exception e) {
            logger.warn("Failed to log retry information to Allure", e);
        }
    }

    public int getRetryCount() {
        return retryCount;
    }

    public int getMaxRetryCount() {
        return maxRetryCount;
    }

    public void resetRetryCount() {
        this.retryCount = 0;
    }
}
