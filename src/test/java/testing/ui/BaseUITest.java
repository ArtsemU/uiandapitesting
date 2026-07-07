package testing.ui;

import factory.WebDriverFactory;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import ui.steps.TextBoxSteps;

public class BaseUITest {
    private static final Logger log = LoggerFactory.getLogger(BaseUITest.class);

    protected WebDriver driver;

    TextBoxSteps tbSteps;

    @BeforeMethod
    public void setUp() {
        log.info("Starting browser: CHROME");
        driver = WebDriverFactory.createDriver(WebDriverFactory.Browser.CHROME);
        driver.manage().window().maximize();
        tbSteps = new TextBoxSteps(driver);
        log.info("Browser started");
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            log.info("Closing browser");
            //driver.quit();
        }
    }
}
