package bdd;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

@CucumberOptions(
        features = "classpath:bdd/features",
        glue = "bdd",
        plugin = {"pretty", "html:target/cucumber-report.html"}
)
public class CucumberRunner extends AbstractTestNGCucumberTests {
}
