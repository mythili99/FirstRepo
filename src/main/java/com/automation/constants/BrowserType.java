package com.automation.constants;

/**
 * Enum for different browser types supported by the framework
 */
public enum BrowserType {
    CHROME("chrome"),
    FIREFOX("firefox"),
    EDGE("edge"),
    SAFARI("safari"),
    CHROME_HEADLESS("chrome_headless"),
    FIREFOX_HEADLESS("firefox_headless"),
    EDGE_HEADLESS("edge_headless");

    private final String browserName;

    BrowserType(String browserName) {
        this.browserName = browserName;
    }

    public String getBrowserName() {
        return browserName;
    }

    public static BrowserType fromString(String browserName) {
        for (BrowserType browser : BrowserType.values()) {
            if (browser.getBrowserName().equalsIgnoreCase(browserName)) {
                return browser;
            }
        }
        throw new IllegalArgumentException("Unknown browser: " + browserName);
    }

    public boolean isHeadless() {
        return this.name().contains("HEADLESS");
    }
}