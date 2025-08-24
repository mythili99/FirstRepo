package com.automation.core.enums;

/**
 * Enum for supported browsers
 */
public enum Browser {
    CHROME("chrome"),
    FIREFOX("firefox"),
    EDGE("edge"),
    SAFARI("safari");

    private final String value;

    Browser(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static Browser fromString(String browserName) {
        for (Browser browser : Browser.values()) {
            if (browser.value.equalsIgnoreCase(browserName)) {
                return browser;
            }
        }
        throw new IllegalArgumentException("Unsupported browser: " + browserName);
    }
}