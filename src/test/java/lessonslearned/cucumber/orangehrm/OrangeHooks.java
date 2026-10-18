package lessonslearned.cucumber.orangehrm;

import factory.WebDriverFactory;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import org.openqa.selenium.WebDriver;

public class OrangeHooks {
    private final UiContext context;

    public OrangeHooks(UiContext context) {
        this.context = context;
    }

    @Before("@ui")
    public void startBrowser() {
        WebDriver driver = WebDriverFactory.createDriver();
        context.setDriver(driver);
        System.out.println(">>> Browser started");
    }

    @After("@ui")
    public void closeBrowser() {
        if (context.hasDriver()) {
            context.getDriver().quit();
            System.out.println(">>> Browser closed");
        }
    }
}
