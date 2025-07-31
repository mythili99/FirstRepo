package com.automation.constants;

/**
 * Framework constants for paths, timeouts, and other configuration values
 */
public final class FrameworkConstants {
    
    // File paths
    public static final String PROJECT_PATH = System.getProperty("user.dir");
    public static final String CONFIG_PROPERTIES_PATH = PROJECT_PATH + "/src/test/resources/properties/config.properties";
    public static final String TEST_DATA_PATH = PROJECT_PATH + "/src/test/resources/testdata/";
    public static final String EXTENT_REPORT_PATH = PROJECT_PATH + "/reports/ExtentReport.html";
    public static final String EXCEL_REPORT_PATH = PROJECT_PATH + "/reports/TestResults.xlsx";
    public static final String LOG_FILE_PATH = PROJECT_PATH + "/logs/application.log";
    public static final String SCREENSHOTS_PATH = PROJECT_PATH + "/test-output/screenshots/";
    
    // Timeouts
    public static final int IMPLICIT_WAIT = 10;
    public static final int EXPLICIT_WAIT = 20;
    public static final int PAGE_LOAD_TIMEOUT = 30;
    
    // Browser window settings
    public static final String BROWSER_WINDOW_SIZE = "1920,1080";
    
    // API constants
    public static final int API_TIMEOUT = 30000;
    public static final String JSON_CONTENT_TYPE = "application/json";
    
    // Excel sheet names
    public static final String TEST_DATA_SHEET = "TestData";
    public static final String TEST_RESULTS_SHEET = "TestResults";
    
    // Database constants
    public static final String DB_CONFIG_FILE = "database.properties";
    
    private FrameworkConstants() {
        // Private constructor to prevent instantiation
    }
}