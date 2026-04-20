package testorbit.runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.DataProvider;

/**
 * Cucumber TestNG Runner
 * Executes Cucumber scenarios with TestNG
 * Supports parallel execution
 */
@CucumberOptions(
    features = "src/test/resources/features",
    glue = {
        "testorbit.stepdefinitions",
        "testorbit.tests.base"
    },
    plugin = {
        "pretty",
        "html:reports/cucumber/cucumber-report.html",
        "json:reports/cucumber/cucumber-report.json",
        "junit:reports/cucumber/cucumber-report.xml"
        // ExtentCucumberAdapter removed - using ExtentReportManager instead
        // "com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter:"
    },
    monochrome = true,
    tags = "@TC001"
)
public class CucumberTestRunner extends AbstractTestNGCucumberTests {

    /**
     * Enable parallel execution
     * Each scenario runs in parallel
     */
    @Override
    @DataProvider(parallel = true)
    public Object[][] scenarios() {
        return super.scenarios();
    }
}
