package com.automation.core;

import com.automation.core.enums.Browser;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.safari.SafariOptions;

import java.io.File;
import java.time.Duration;

/**
 * Custom Driver Manager for restricted organizations where automatic driver download is not allowed
 * Drivers are expected to be placed in a local folder structure
 */
public class CustomDriverManager {
    private static final Logger logger = LoggerManager.getInstance().getLogger(CustomDriverManager.class);
    private static CustomDriverManager instance;
    private WebDriver driver;
    private final ConfigManager configManager;

    private CustomDriverManager() {
        configManager = ConfigManager.getInstance();
    }

    public static CustomDriverManager getInstance() {
        if (instance == null) {
            instance = new CustomDriverManager();
        }
        return instance;
    }

    public WebDriver getDriver() {
        if (driver == null) {
            driver = createDriver();
            setupDriver();
        }
        return driver;
    }

    private WebDriver createDriver() {
        Browser browser = Browser.fromString(configManager.getBrowser());
        boolean headless = configManager.isHeadless();

        logger.info("Creating WebDriver for browser: {} (headless: {})", browser.getValue(), headless);

        String runEnv = configManager.getProperty("run.env");



        switch (browser) {
            case CHROME:
                return createChromeDriver(headless);
            case FIREFOX:
                return createFirefoxDriver(headless);
            case EDGE:
                return createEdgeDriver(headless);
            case SAFARI:
                return createSafariDriver();
            default:
                throw new IllegalArgumentException("Unsupported browser: " + browser.getValue());
        }
    }

    private WebDriver createChromeDriver(boolean headless) {

        if (runEnv != null && runEnv.equalsIgnoreCase("ci")) {
            WebDriverManager.chromedriver().setup();
            logger.info("Using WebDriverManager to setup ChromeDriver");
        } else {
            String driverPath = getDriverPath("chromedriver");
            System.setProperty("webdriver.chrome.driver", driverPath);
        }

        ChromeOptions options = new ChromeOptions();

        if (headless) {
            options.addArguments("--headless");
        }

        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--remote-allow-origins=*");

        return new ChromeDriver(options);
    }

    private WebDriver createFirefoxDriver(boolean headless) {

        if (runEnv != null && runEnv.equalsIgnoreCase("ci")) {
            WebDriverManager.chromedriver().setup();
            logger.info("Using WebDriverManager to setup ChromeDriver");
        } else {
            String driverPath = getDriverPath("geckodriver");
            System.setProperty("webdriver.gecko.driver", driverPath);
        }


        FirefoxOptions options = new FirefoxOptions();

        if (headless) {
            options.addArguments("--headless");
        }

        options.addArguments("--width=1920");
        options.addArguments("--height=1080");

        return new FirefoxDriver(options);
    }

    private WebDriver createEdgeDriver(boolean headless) {

        if (runEnv != null && runEnv.equalsIgnoreCase("ci")) {
            WebDriverManager.chromedriver().setup();
            logger.info("Using WebDriverManager to setup ChromeDriver");
        } else {
            String driverPath = getDriverPath("msedgedriver");
            System.setProperty("webdriver.edge.driver", driverPath);           }





        EdgeOptions options = new EdgeOptions();

        if (headless) {
            options.addArguments("--headless");
        }

        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--window-size=1920,1080");

        return new EdgeDriver(options);
    }

    private WebDriver createSafariDriver() {
        if (runEnv != null && runEnv.equalsIgnoreCase("ci")) {
            WebDriverManager.chromedriver().setup();
            logger.info("Using WebDriverManager to setup ChromeDriver");
        } else {
            // Safari doesn't support headless mode

            String driverPath = getDriverPath("safaridriver");
            System.setProperty("webdriver.safari.driver", driverPath);
        }
        SafariOptions options = new SafariOptions();
        return new SafariDriver(options);
    }

    private String getDriverPath(String driverName) {
        String driversFolder = configManager.getProperty("drivers.folder.path", "drivers");
        String os = System.getProperty("os.name").toLowerCase();
        String extension = "";

        // Determine file extension based on OS
        if (os.contains("win")) {
            extension = ".exe";
        } else if (os.contains("mac")) {
            extension = "";
        } else if (os.contains("linux")) {
            extension = "";
        }

        String driverPath = driversFolder + File.separator + os + File.separator + driverName + extension;
        File driverFile = new File(driverPath);

        if (!driverFile.exists()) {
            throw new RuntimeException("Driver not found at path: " + driverPath +
                    "\nPlease ensure the driver executable is placed in the correct folder structure:\n" +
                    "drivers/\n" +
                    "├── windows/\n" +
                    "│   ├── chromedriver.exe\n" +
                    "│   ├── geckodriver.exe\n" +
                    "│   └── msedgedriver.exe\n" +
                    "├── mac/\n" +
                    "│   ├── chromedriver\n" +
                    "│   ├── geckodriver\n" +
                    "│   └── msedgedriver\n" +
                    "└── linux/\n" +
                    "    ├── chromedriver\n" +
                    "    ├── geckodriver\n" +
                    "    └── msedgedriver");
        }

        logger.info("Using driver from path: {}", driverPath);
        return driverPath;
    }

    private void setupDriver() {
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(configManager.getImplicitWait()));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(configManager.getPageLoadTimeout()));
        driver.manage().window().maximize();

        logger.info("WebDriver setup completed");
    }

    public void quitDriver() {
        if (driver != null) {
            try {
                driver.quit();
                logger.info("WebDriver quit successfully");
            } catch (Exception e) {
                logger.error("Error quitting WebDriver", e);
            } finally {
                driver = null;
            }
        }
    }

    public void closeDriver() {
        if (driver != null) {
            try {
                driver.close();
                logger.info("WebDriver closed successfully");
            } catch (Exception e) {
                logger.error("Error closing WebDriver", e);
            }
        }
    }

    public void navigateTo(String url) {
        getDriver().get(url);
        logger.info("Navigated to: {}", url);
    }

    public String getCurrentUrl() {
        return getDriver().getCurrentUrl();
    }

    public String getTitle() {
        return getDriver().getTitle();
    }

    public String takeScreenshot(String testName) {
        try {
            String screenshotPath = configManager.getProperty("screenshot.path") + "/" +
                    testName + "_" + System.currentTimeMillis() + ".png";

            // Screenshot logic would be implemented here
            logger.info("Screenshot taken: {}", screenshotPath);

            return screenshotPath;
        } catch (Exception e) {
            logger.error("Failed to take screenshot", e);
            return null;
        }
    }
}
