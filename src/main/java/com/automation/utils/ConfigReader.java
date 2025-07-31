package com.automation.utils;

import com.automation.constants.FrameworkConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Configuration reader that supports properties files and environment variables
 * Environment variables take precedence over properties file values
 */
public final class ConfigReader {
    
    private static final Logger logger = LogManager.getLogger(ConfigReader.class);
    private static Properties properties;
    private static ConfigReader instance;
    
    private ConfigReader() {
        loadProperties();
    }
    
    public static ConfigReader getInstance() {
        if (instance == null) {
            synchronized (ConfigReader.class) {
                if (instance == null) {
                    instance = new ConfigReader();
                }
            }
        }
        return instance;
    }
    
    private void loadProperties() {
        properties = new Properties();
        try (FileInputStream fis = new FileInputStream(FrameworkConstants.CONFIG_PROPERTIES_PATH)) {
            properties.load(fis);
            logger.info("Configuration properties loaded successfully from: {}", 
                       FrameworkConstants.CONFIG_PROPERTIES_PATH);
        } catch (IOException e) {
            logger.error("Failed to load configuration properties: {}", e.getMessage());
            throw new RuntimeException("Configuration file not found: " + 
                                     FrameworkConstants.CONFIG_PROPERTIES_PATH, e);
        }
    }
    
    /**
     * Get property value with environment variable override support
     * @param key Property key
     * @return Property value (environment variable takes precedence)
     */
    public String getProperty(String key) {
        // Check environment variable first (convert to uppercase and replace dots with underscores)
        String envKey = key.toUpperCase().replace(".", "_");
        String envValue = System.getenv(envKey);
        
        if (envValue != null && !envValue.trim().isEmpty()) {
            logger.debug("Using environment variable {} = {}", envKey, envValue);
            return envValue;
        }
        
        // Fallback to properties file
        String propValue = properties.getProperty(key);
        if (propValue != null) {
            logger.debug("Using property {} = {}", key, propValue);
            return propValue;
        }
        
        logger.warn("Property '{}' not found in configuration", key);
        return null;
    }
    
    /**
     * Get property value with default fallback
     * @param key Property key
     * @param defaultValue Default value if property not found
     * @return Property value or default value
     */
    public String getProperty(String key, String defaultValue) {
        String value = getProperty(key);
        return value != null ? value : defaultValue;
    }
    
    /**
     * Get boolean property value
     * @param key Property key
     * @param defaultValue Default value if property not found
     * @return Boolean property value
     */
    public boolean getBooleanProperty(String key, boolean defaultValue) {
        String value = getProperty(key);
        return value != null ? Boolean.parseBoolean(value) : defaultValue;
    }
    
    /**
     * Get integer property value
     * @param key Property key
     * @param defaultValue Default value if property not found
     * @return Integer property value
     */
    public int getIntProperty(String key, int defaultValue) {
        String value = getProperty(key);
        try {
            return value != null ? Integer.parseInt(value) : defaultValue;
        } catch (NumberFormatException e) {
            logger.warn("Invalid integer value for property '{}': {}", key, value);
            return defaultValue;
        }
    }
    
    // Specific configuration getters
    public String getBrowser() {
        return getProperty("browser", "chrome");
    }
    
    public boolean isHeadless() {
        return getBooleanProperty("headless", false);
    }
    
    public String getBaseUrl() {
        return getProperty("base.url", "https://example.com");
    }
    
    public String getApiBaseUrl() {
        return getProperty("api.base.url", "https://api.example.com");
    }
    
    public String getEnvironment() {
        return getProperty("environment", "dev");
    }
    
    public int getImplicitWait() {
        return getIntProperty("implicit.wait", FrameworkConstants.IMPLICIT_WAIT);
    }
    
    public int getExplicitWait() {
        return getIntProperty("explicit.wait", FrameworkConstants.EXPLICIT_WAIT);
    }
    
    public int getPageLoadTimeout() {
        return getIntProperty("page.load.timeout", FrameworkConstants.PAGE_LOAD_TIMEOUT);
    }
    
    public boolean isRemoteExecution() {
        return getBooleanProperty("remote.execution", false);
    }
    
    public String getRemoteUrl() {
        return getProperty("remote.url", "http://localhost:4444");
    }
    
    public String getDatabaseUrl() {
        return getProperty("db.url");
    }
    
    public String getDatabaseUsername() {
        return getProperty("db.username");
    }
    
    public String getDatabasePassword() {
        return getProperty("db.password");
    }
}