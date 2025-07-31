package com.automation.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Singleton Logger Manager for framework-wide logging
 * Provides centralized logging functionality with different log levels
 */
public final class LoggerManager {
    
    private static LoggerManager instance;
    private final Logger logger;
    private final Logger testResultsLogger;
    
    private LoggerManager() {
        this.logger = LogManager.getLogger(LoggerManager.class);
        this.testResultsLogger = LogManager.getLogger("TestResults");
    }
    
    public static LoggerManager getInstance() {
        if (instance == null) {
            synchronized (LoggerManager.class) {
                if (instance == null) {
                    instance = new LoggerManager();
                }
            }
        }
        return instance;
    }
    
    /**
     * Get logger for specific class
     * @param clazz The class for which logger is needed
     * @return Logger instance
     */
    public static Logger getLogger(Class<?> clazz) {
        return LogManager.getLogger(clazz);
    }
    
    /**
     * Log info message
     * @param message Message to log
     */
    public void info(String message) {
        logger.info(message);
    }
    
    /**
     * Log info message with parameters
     * @param message Message template
     * @param params Parameters to substitute
     */
    public void info(String message, Object... params) {
        logger.info(message, params);
    }
    
    /**
     * Log debug message
     * @param message Message to log
     */
    public void debug(String message) {
        logger.debug(message);
    }
    
    /**
     * Log debug message with parameters
     * @param message Message template
     * @param params Parameters to substitute
     */
    public void debug(String message, Object... params) {
        logger.debug(message, params);
    }
    
    /**
     * Log warning message
     * @param message Message to log
     */
    public void warn(String message) {
        logger.warn(message);
    }
    
    /**
     * Log warning message with parameters
     * @param message Message template
     * @param params Parameters to substitute
     */
    public void warn(String message, Object... params) {
        logger.warn(message, params);
    }
    
    /**
     * Log error message
     * @param message Message to log
     */
    public void error(String message) {
        logger.error(message);
    }
    
    /**
     * Log error message with exception
     * @param message Message to log
     * @param throwable Exception to log
     */
    public void error(String message, Throwable throwable) {
        logger.error(message, throwable);
    }
    
    /**
     * Log error message with parameters
     * @param message Message template
     * @param params Parameters to substitute
     */
    public void error(String message, Object... params) {
        logger.error(message, params);
    }
    
    /**
     * Log fatal message
     * @param message Message to log
     */
    public void fatal(String message) {
        logger.fatal(message);
    }
    
    /**
     * Log fatal message with exception
     * @param message Message to log
     * @param throwable Exception to log
     */
    public void fatal(String message, Throwable throwable) {
        logger.fatal(message, throwable);
    }
    
    /**
     * Log test step information
     * @param stepName Name of the test step
     * @param description Description of the step
     */
    public void logTestStep(String stepName, String description) {
        String logMessage = String.format("STEP: %s - %s", stepName, description);
        logger.info(logMessage);
        testResultsLogger.info(logMessage);
    }
    
    /**
     * Log test result
     * @param testName Name of the test
     * @param status Test status (PASS/FAIL/SKIP)
     * @param duration Test execution duration
     */
    public void logTestResult(String testName, String status, long duration) {
        String logMessage = String.format("TEST: %s | STATUS: %s | DURATION: %dms", 
                                         testName, status, duration);
        logger.info(logMessage);
        testResultsLogger.info(logMessage);
    }
    
    /**
     * Log API request details
     * @param method HTTP method
     * @param url Request URL
     * @param statusCode Response status code
     * @param responseTime Response time in milliseconds
     */
    public void logApiRequest(String method, String url, int statusCode, long responseTime) {
        String logMessage = String.format("API: %s %s | STATUS: %d | TIME: %dms", 
                                         method, url, statusCode, responseTime);
        logger.info(logMessage);
        testResultsLogger.info(logMessage);
    }
    
    /**
     * Log framework initialization
     * @param component Component being initialized
     * @param details Additional details
     */
    public void logFrameworkInfo(String component, String details) {
        String logMessage = String.format("FRAMEWORK: %s - %s", component, details);
        logger.info(logMessage);
    }
}