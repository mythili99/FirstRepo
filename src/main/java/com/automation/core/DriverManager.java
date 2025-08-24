package com.automation.core;

import com.automation.constants.BrowserType;
import com.automation.constants.FrameworkConstants;
import com.automation.constants.OSType;
import com.automation.utils.ConfigReader;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.safari.SafariOptions;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * Singleton WebDriver manager that handles browser initialization and management
 * Supports multiple browsers, headless mode, and remote execution
 */
public final class DriverManager {
    
    private static final Logger logger = LogManager.getLogger(DriverManager.class);
    private static final ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();
    private static DriverManager instance;
    private final ConfigReader configReader;
    
    private DriverManager() {
        this.configReader = ConfigReader.getInstance();
    }
    
    public static DriverManager getInstance() {
        if (instance == null) {
            synchronized (DriverManager.class) {
                if (instance == null) {
                    instance = new DriverManager();
                }
            }
        }
        return instance;
    }
    
    /**
     * Initialize WebDriver based on configuration
     */
    public void initializeDriver() {
        String browserName = configReader.getBrowser();
        boolean isHeadless = configReader.isHeadless();
        boolean isRemote = configReader.isRemoteExecution();
        
        BrowserType browserType = BrowserType.fromString(browserName);
        
        // Override headless if browser type specifies it
        if (browserType.isHeadless()) {
            isHeadless = true;
        }
        
        WebDriver driver;
        
        if (isRemote) {
            driver = createRemoteDriver(browserType, isHeadless);
        } else {
            driver = createLocalDriver(browserType, isHeadless);
        }
        
        setupDriver(driver);
        driverThreadLocal.set(driver);
        
        logger.info("WebDriver initialized successfully - Browser: {}, Headless: {}, Remote: {}", 
                   browserType.getBrowserName(), isHeadless, isRemote);
    }
    
    /**
     * Get current WebDriver instance
     * @return WebDriver instance
     */
    public WebDriver getDriver() {
        WebDriver driver = driverThreadLocal.get();
        if (driver == null) {
            throw new IllegalStateException("WebDriver is not initialized. Call initializeDriver() first.");
        }
        return driver;
    }
    
    /**
     * Quit WebDriver and clean up
     */
    public void quitDriver() {
        WebDriver driver = driverThreadLocal.get();
        if (driver != null) {
            try {
                driver.quit();
                logger.info("WebDriver quit successfully");
            } catch (Exception e) {
                logger.error("Error while quitting WebDriver: {}", e.getMessage());
            } finally {
                driverThreadLocal.remove();
            }
        }
    }
    
    /**
     * Create local WebDriver instance
     */
    private WebDriver createLocalDriver(BrowserType browserType, boolean isHeadless) {
        OSType osType = OSType.getOSType();
        logger.info("Creating local driver for OS: {}", osType.getOsName());
        
        switch (browserType) {
            case CHROME:
            case CHROME_HEADLESS:
                WebDriverManager.chromedriver().setup();
                return new ChromeDriver(getChromeOptions(isHeadless));
                
            case FIREFOX:
            case FIREFOX_HEADLESS:
                WebDriverManager.firefoxdriver().setup();
                return new FirefoxDriver(getFirefoxOptions(isHeadless));
                
            case EDGE:
            case EDGE_HEADLESS:
                WebDriverManager.edgedriver().setup();
                return new EdgeDriver(getEdgeOptions(isHeadless));
                
            case SAFARI:
                if (osType != OSType.MAC) {
                    throw new RuntimeException("Safari is only supported on macOS");
                }
                WebDriverManager.safaridriver().setup();
                return new SafariDriver(getSafariOptions());
                
            default:
                throw new IllegalArgumentException("Unsupported browser: " + browserType.getBrowserName());
        }
    }
    
    /**
     * Create remote WebDriver instance
     */
    private WebDriver createRemoteDriver(BrowserType browserType, boolean isHeadless) {
        try {
            URL remoteUrl = new URL(configReader.getRemoteUrl());
            
            switch (browserType) {
                case CHROME:
                case CHROME_HEADLESS:
                    return new RemoteWebDriver(remoteUrl, getChromeOptions(isHeadless));
                    
                case FIREFOX:
                case FIREFOX_HEADLESS:
                    return new RemoteWebDriver(remoteUrl, getFirefoxOptions(isHeadless));
                    
                case EDGE:
                case EDGE_HEADLESS:
                    return new RemoteWebDriver(remoteUrl, getEdgeOptions(isHeadless));
                    
                case SAFARI:
                    return new RemoteWebDriver(remoteUrl, getSafariOptions());
                    
                default:
                    throw new IllegalArgumentException("Unsupported browser for remote execution: " + 
                                                     browserType.getBrowserName());
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Invalid remote URL: " + configReader.getRemoteUrl(), e);
        }
    }
    
    /**
     * Configure Chrome options
     */
    private ChromeOptions getChromeOptions(boolean isHeadless) {
        ChromeOptions options = new ChromeOptions();
        
        if (isHeadless) {
            options.addArguments("--headless=new");
        }
        
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-web-security");
        options.addArguments("--ignore-certificate-errors");
        options.addArguments("--disable-extensions");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=" + FrameworkConstants.BROWSER_WINDOW_SIZE);
        
        // Performance optimization
        Map<String, Object> prefs = new HashMap<>();
        prefs.put("profile.default_content_setting_values.notifications", 2);
        prefs.put("profile.default_content_settings.popups", 0);
        options.setExperimentalOption("prefs", prefs);
        
        logger.debug("Chrome options configured - Headless: {}", isHeadless);
        return options;
    }
    
    /**
     * Configure Firefox options
     */
    private FirefoxOptions getFirefoxOptions(boolean isHeadless) {
        FirefoxOptions options = new FirefoxOptions();
        
        if (isHeadless) {
            options.addArguments("--headless");
        }
        
        options.addArguments("--width=1920");
        options.addArguments("--height=1080");
        options.addPreference("dom.webnotifications.enabled", false);
        options.addPreference("dom.push.enabled", false);
        
        logger.debug("Firefox options configured - Headless: {}", isHeadless);
        return options;
    }
    
    /**
     * Configure Edge options
     */
    private EdgeOptions getEdgeOptions(boolean isHeadless) {
        EdgeOptions options = new EdgeOptions();
        
        if (isHeadless) {
            options.addArguments("--headless=new");
        }
        
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-web-security");
        options.addArguments("--ignore-certificate-errors");
        options.addArguments("--window-size=" + FrameworkConstants.BROWSER_WINDOW_SIZE);
        
        logger.debug("Edge options configured - Headless: {}", isHeadless);
        return options;
    }
    
    /**
     * Configure Safari options
     */
    private SafariOptions getSafariOptions() {
        SafariOptions options = new SafariOptions();
        options.setAutomaticInspection(false);
        options.setAutomaticProfiling(false);
        
        logger.debug("Safari options configured");
        return options;
    }
    
    /**
     * Setup driver with timeouts and other configurations
     */
    private void setupDriver(WebDriver driver) {
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(configReader.getImplicitWait()));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(configReader.getPageLoadTimeout()));
        driver.manage().window().maximize();
    }
}