package com.automation.core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Singleton Logger manager for centralized logging
 */
public class LoggerManager {
    private static LoggerManager instance;

    private LoggerManager() {
        // Private constructor for singleton
    }

    public static LoggerManager getInstance() {
        if (instance == null) {
            instance = new LoggerManager();
        }
        return instance;
    }

    public Logger getLogger(Class<?> clazz) {
        return LogManager.getLogger(clazz);
    }

    public Logger getLogger(String name) {
        return LogManager.getLogger(name);
    }

    public void info(Class<?> clazz, String message) {
        getLogger(clazz).info(message);
    }

    public void info(Class<?> clazz, String message, Object... args) {
        getLogger(clazz).info(message, args);
    }

    public void warn(Class<?> clazz, String message) {
        getLogger(clazz).warn(message);
    }

    public void warn(Class<?> clazz, String message, Object... args) {
        getLogger(clazz).warn(message, args);
    }

    public void error(Class<?> clazz, String message) {
        getLogger(clazz).error(message);
    }

    public void error(Class<?> clazz, String message, Throwable throwable) {
        getLogger(clazz).error(message, throwable);
    }

    public void debug(Class<?> clazz, String message) {
        getLogger(clazz).debug(message);
    }

    public void debug(Class<?> clazz, String message, Object... args) {
        getLogger(clazz).debug(message, args);
    }
}