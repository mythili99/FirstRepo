package com.automation.constants;

/**
 * Enum for different operating system types
 */
public enum OSType {
    WINDOWS("windows"),
    MAC("mac"),
    LINUX("linux");

    private final String osName;

    OSType(String osName) {
        this.osName = osName;
    }

    public String getOsName() {
        return osName;
    }

    public static OSType getOSType() {
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("win")) {
            return WINDOWS;
        } else if (osName.contains("mac")) {
            return MAC;
        } else if (osName.contains("nix") || osName.contains("nux") || osName.contains("aix")) {
            return LINUX;
        } else {
            throw new RuntimeException("Unsupported operating system: " + osName);
        }
    }

    public static OSType fromString(String osName) {
        for (OSType os : OSType.values()) {
            if (os.getOsName().equalsIgnoreCase(osName)) {
                return os;
            }
        }
        throw new IllegalArgumentException("Unknown OS: " + osName);
    }
}