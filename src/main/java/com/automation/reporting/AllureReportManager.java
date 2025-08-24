package com.automation.reporting;

import com.automation.core.LoggerManager;
import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import io.qameta.allure.Attachment;
import io.qameta.allure.Description;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;

/**
 * Allure Report Manager for enhanced test reporting
 * Provides utilities for Allure integration including steps, attachments, and descriptions
 */
public class AllureReportManager {
    private static final Logger logger = LoggerManager.getInstance().getLogger(AllureReportManager.class);
    private static AllureReportManager instance;

    private AllureReportManager() {}

    public static AllureReportManager getInstance() {
        if (instance == null) {
            instance = new AllureReportManager();
        }
        return instance;
    }

    /**
     * Add step description to Allure report
     */
    @Step("{stepDescription}")
    public void addStep(String stepDescription) {
        logger.info("Allure Step: {}", stepDescription);
    }

    /**
     * Add parameterized step description to Allure report
     */
    @Step("{stepDescription} with value: {value}")
    public void addStepWithValue(String stepDescription, Object value) {
        logger.info("Allure Step: {} with value: {}", stepDescription, value);
    }

    /**
     * Add step description with multiple parameters to Allure report
     */
    @Step("{stepDescription} - Param1: {param1}, Param2: {param2}")
    public void addStepWithParams(String stepDescription, Object param1, Object param2) {
        logger.info("Allure Step: {} - Param1: {}, Param2: {}", stepDescription, param1, param2);
    }

    /**
     * Add screenshot attachment to Allure report
     */
    @Attachment(value = "Screenshot: {screenshotName}", type = "image/png")
    public byte[] addScreenshot(String screenshotName, String screenshotPath) {
        try {
            byte[] screenshotBytes = Files.readAllBytes(Paths.get(screenshotPath));
            logger.info("Screenshot attached to Allure: {} from path: {}", screenshotName, screenshotPath);
            return screenshotBytes;
        } catch (IOException e) {
            logger.error("Failed to attach screenshot to Allure: {}", screenshotPath, e);
            return new byte[0];
        }
    }

    /**
     * Add text attachment to Allure report
     */
    @Attachment(value = "Text Attachment: {attachmentName}", type = "text/plain")
    public String addTextAttachment(String attachmentName, String content) {
        logger.info("Text attachment added to Allure: {} with content length: {}", attachmentName, content.length());
        return content;
    }

    /**
     * Add JSON attachment to Allure report
     */
    @Attachment(value = "JSON Attachment: {attachmentName}", type = "application/json")
    public String addJsonAttachment(String attachmentName, String jsonContent) {
        logger.info("JSON attachment added to Allure: {} with content length: {}", attachmentName, jsonContent.length());
        return jsonContent;
    }

    /**
     * Add HTML attachment to Allure report
     */
    @Attachment(value = "HTML Attachment: {attachmentName}", type = "text/html")
    public String addHtmlAttachment(String attachmentName, String htmlContent) {
        logger.info("HTML attachment added to Allure: {} with content length: {}", attachmentName, htmlContent.length());
        return htmlContent;
    }

    /**
     * Add file attachment to Allure report
     */
    @Attachment(value = "File Attachment: {fileName}", type = "application/octet-stream")
    public byte[] addFileAttachment(String fileName, String filePath) {
        try {
            byte[] fileBytes = Files.readAllBytes(Paths.get(filePath));
            logger.info("File attachment added to Allure: {} from path: {}", fileName, filePath);
            return fileBytes;
        } catch (IOException e) {
            logger.error("Failed to attach file to Allure: {}", filePath, e);
            return new byte[0];
        }
    }

    /**
     * Add test description to Allure report
     */
    @Description("Test Description: {description}")
    public void addTestDescription(String description) {
        logger.info("Test description added to Allure: {}", description);
    }

    /**
     * Add environment information to Allure report
     */
    public void addEnvironmentInfo(String key, String value) {
        Allure.getLifecycle().updateTestCase(testResult -> {
            // Add environment info as a parameter
            testResult.setParameters(Collections.singletonList(
                new io.qameta.allure.model.Parameter().setName(key).setValue(value)
            ));
        });
        logger.info("Environment info added to Allure: {} = {}", key, value);
    }

    /**
     * Add test severity to Allure report
     */
    public void addTestSeverity(io.qameta.allure.SeverityLevel severity) {
        Allure.getLifecycle().updateTestCase(testResult -> {
            testResult.setStatusDetails(new io.qameta.allure.model.StatusDetails().setMessage("Severity: " + severity.value()));
        });
        logger.info("Test severity added to Allure: {}", severity.value());
    }

    /**
     * Add test story to Allure report
     */
    public void addTestStory(String story) {
        Allure.getLifecycle().updateTestCase(testResult -> {
            testResult.setFullName(testResult.getFullName() + " - Story: " + story);
        });
        logger.info("Test story added to Allure: {}", story);
    }

    /**
     * Add test feature to Allure report
     */
    public void addTestFeature(String feature) {
        Allure.getLifecycle().updateTestCase(testResult -> {
            testResult.setFullName(testResult.getFullName() + " - Feature: " + feature);
        });
        logger.info("Test feature added to Allure: {}", feature);
    }

    /**
     * Add test epic to Allure report
     */
    public void addTestEpic(String epic) {
        Allure.getLifecycle().updateTestCase(testResult -> {
            testResult.setFullName(testResult.getFullName() + " - Epic: " + epic);
        });
        logger.info("Test epic added to Allure: {}", epic);
    }

    /**
     * Add test link to Allure report
     */
    public void addTestLink(String name, String url) {
        Allure.getLifecycle().updateTestCase(testResult -> {
            testResult.setLinks(Collections.singletonList(
                new io.qameta.allure.model.Link().setName(name).setUrl(url)
            ));
        });
        logger.info("Test link added to Allure: {} = {}", name, url);
    }

    /**
     * Add test issue link to Allure report
     */
    public void addTestIssue(String issueKey) {
        Allure.getLifecycle().updateTestCase(testResult -> {
            testResult.setLinks(Collections.singletonList(
                new io.qameta.allure.model.Link().setName("Issue: " + issueKey).setType("issue")
            ));
        });
        logger.info("Test issue added to Allure: {}", issueKey);
    }

    /**
     * Add test requirement link to Allure report
     */
    public void addTestRequirement(String requirementKey) {
        Allure.getLifecycle().updateTestCase(testResult -> {
            testResult.setLinks(Collections.singletonList(
                new io.qameta.allure.model.Link().setName("Requirement: " + requirementKey).setType("requirement")
            ));
        });
        logger.info("Test requirement added to Allure: {}", requirementKey);
    }

    /**
     * Add test tag to Allure report
     */
    public void addTestTag(String tag) {
        Allure.getLifecycle().updateTestCase(testResult -> {
            testResult.setLabels(Collections.singletonList(
                new io.qameta.allure.model.Label().setName("tag").setValue(tag)
            ));
        });
        logger.info("Test tag added to Allure: {}", tag);
    }

    /**
     * Add test label to Allure report
     */
    public void addTestLabel(String name, String value) {
        Allure.getLifecycle().updateTestCase(testResult -> {
            testResult.setLabels(Collections.singletonList(
                new io.qameta.allure.model.Label().setName(name).setValue(value)
            ));
        });
        logger.info("Test label added to Allure: {} = {}", name, value);
    }
}
