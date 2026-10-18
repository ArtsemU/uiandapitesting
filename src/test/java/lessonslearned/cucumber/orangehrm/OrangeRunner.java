package lessonslearned.cucumber.orangehrm;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

@CucumberOptions(
        features = "src/test/java/lessonslearned/cucumber/features/orangehrm.feature",
        glue = "lessonslearned.cucumber.orangehrm",
        plugin = {"pretty"},
        tags = "@ui"
)
public class OrangeRunner extends AbstractTestNGCucumberTests {
}
