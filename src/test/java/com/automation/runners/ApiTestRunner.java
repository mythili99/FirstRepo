package com.automation.runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.DataProvider;

/**
 * TestNG runner for API-specific Cucumber tests
 */
@CucumberOptions(
    features = "src/test/resources/features",
    glue = {"com.automation.steps"},
    tags = "@api",
    plugin = {
        "pretty",
        "html:target/cucumber-reports/api-html",
        "json:target/cucumber-reports/api-cucumber.json",
        "junit:target/cucumber-reports/api-cucumber.xml"
    },
    monochrome = true,
    dryRun = false
)
public class ApiTestRunner extends AbstractTestNGCucumberTests {
    
    @Override
    @DataProvider(parallel = true)
    public Object[][] scenarios() {
        return super.scenarios();
    }
}