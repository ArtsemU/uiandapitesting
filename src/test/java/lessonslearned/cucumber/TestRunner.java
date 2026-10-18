package lessonslearned.cucumber;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

@CucumberOptions(
        features = "src/test/java/lessonslearned/cucumber/features/login.feature",
        glue = "lessonslearned.cucumber",
        plugin = {"pretty"},
        tags = "@fakelogin"
)
public class TestRunner extends AbstractTestNGCucumberTests {
}