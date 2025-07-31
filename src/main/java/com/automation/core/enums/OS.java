package com.automation.core.enums;

/**
 * Enum for supported operating systems
 */
public enum OS {
    WINDOWS("windows"),
    MAC("mac"),
    LINUX("linux");

    private final String value;

    OS(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static OS fromString(String osName) {
        for (OS os : OS.values()) {
            if (os.value.equalsIgnoreCase(osName)) {
                return os;
            }
        }
        throw new IllegalArgumentException("Unsupported OS: " + osName);
    }

    public static OS getCurrentOS() {
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("win")) {
            return WINDOWS;
        } else if (osName.contains("mac")) {
            return MAC;
        } else {
            return LINUX;
        }
    }
}