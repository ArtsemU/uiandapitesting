package testing.ui;

import factory.WebDriverFactory;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import ui.steps.CheckBoxSteps;
import ui.steps.TextBoxSteps;
import ui.steps.WebTablesSteps;

public class BaseUITest {
    private static final Logger log = LoggerFactory.getLogger(BaseUITest.class);

    private static final ThreadLocal<WebDriver> driverTL = new ThreadLocal<>();
    private static final ThreadLocal<TextBoxSteps> tbStepsTL = new ThreadLocal<>();
    private static final ThreadLocal<CheckBoxSteps> cbStepsTL = new ThreadLocal<>();
    private static final ThreadLocal<WebTablesSteps> wtStepsTL = new ThreadLocal<>();

    @BeforeMethod
    public void setUp() {
        log.info("Starting browser: CHROME");
        WebDriver driver = WebDriverFactory.createDriver(WebDriverFactory.Browser.CHROME);
        driver.manage().window().maximize();
        driverTL.set(driver);
        tbStepsTL.set(new TextBoxSteps(driver));
        cbStepsTL.set(new CheckBoxSteps(driver));
        wtStepsTL.set(new WebTablesSteps(driver));
        log.info("Browser started");
    }

    protected WebDriver driver() { return driverTL.get(); }
    protected TextBoxSteps tbSteps() { return tbStepsTL.get(); }
    protected CheckBoxSteps cbSteps() { return cbStepsTL.get(); }
    protected WebTablesSteps wtSteps() { return wtStepsTL.get(); }

    @AfterMethod
    public void tearDown() {
        WebDriver driver = driverTL.get();
        if (driver != null) {
            log.info("Closing browser");
            driver.quit();
        }
        driverTL.remove();
        tbStepsTL.remove();
        cbStepsTL.remove();
        wtStepsTL.remove();
    }



    // Before parallel run
   //protected WebDriver driver;

   //TextBoxSteps tbSteps;
   //CheckBoxSteps cbSteps;
   //WebTablesSteps wtSteps;

    //@BeforeMethod
    //public void setUp() {
    //    log.info("Starting browser: CHROME");
    //    driver = WebDriverFactory.createDriver(WebDriverFactory.Browser.CHROME);
    //    driver.manage().window().maximize();
    //    tbSteps = new TextBoxSteps(driver);
    //    cbSteps = new CheckBoxSteps(driver);
    //    wtSteps = new WebTablesSteps(driver);
    //    log.info("Browser started");
    //}

    //@AfterMethod
    //public void tearDown() {
    //    if (driver != null) {
    //        log.info("Closing browser");
    //        //driver.quit();
    //    }
    //}
}
