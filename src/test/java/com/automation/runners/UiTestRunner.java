package com.automation.runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.DataProvider;

/**
 * TestNG runner for UI-specific Cucumber tests
 */
@CucumberOptions(
    features = "src/test/resources/features",
    glue = {"com.automation.steps"},
    tags = "@ui",
    plugin = {
        "pretty",
        "html:target/cucumber-reports/ui-html",
        "json:target/cucumber-reports/ui-cucumber.json",
        "junit:target/cucumber-reports/ui-cucumber.xml"
    },
    monochrome = true,
    dryRun = false
)
public class UiTestRunner extends AbstractTestNGCucumberTests {
    
    @Override
    @DataProvider(parallel = true)
    public Object[][] scenarios() {
        return super.scenarios();
    }
}