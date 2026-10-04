package reporting;

import io.qameta.allure.Allure;
import io.qameta.allure.AttachmentOptions;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestListener;
import org.testng.ITestResult;
import testing.ui.BaseUITest;

import java.io.ByteArrayInputStream;

/**
 * Attaches a browser screenshot to the Allure result of a failed UI test.
 *
 * Registered through META-INF/services/org.testng.ITestNGListener. TestNG calls
 * onTestFailure on the test's own thread and before @AfterMethod, so the browser
 * held by BaseUITest is still open. Failures of tests that are not BaseUITest
 * subclasses (API, BDD) are ignored silently.
 *
 * A screenshot problem is logged and never replaces the original test failure.
 */
public class ScreenshotOnFailureListener implements ITestListener {
    private static final Logger log = LoggerFactory.getLogger(ScreenshotOnFailureListener.class);

    @Override
    public void onTestFailure(ITestResult result) {
        if (!(result.getInstance() instanceof BaseUITest)) {
            return;
        }

        String testName = result.getMethod().getMethodName();
        WebDriver driver = BaseUITest.currentDriver();
        if (driver == null) {
            log.warn("No screenshot for failed test {}: no browser on this thread", testName);
            return;
        }

        try {
            byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            Allure.attachment("Screenshot on failure", "image/png", new ByteArrayInputStream(screenshot),
                    AttachmentOptions.withFileExtension("png"));
            log.info("Screenshot attached for failed test {}", testName);
        } catch (Exception e) {
            log.warn("No screenshot for failed test {}: {}", testName, e.toString());
        }
    }
}
