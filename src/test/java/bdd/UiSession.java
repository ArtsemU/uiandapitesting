package bdd;

import factory.WebDriverFactory;
import org.openqa.selenium.WebDriver;
import ui.steps.TextBoxSteps;
import ui.steps.WebTablesSteps;

/**
 * The browser of one @ui scenario and the *Steps objects bound to it. PicoContainer
 * creates a fresh instance per scenario; UiHooks opens and closes the browser.
 */
public class UiSession {

    private WebDriver driver;
    private TextBoxSteps textBoxSteps;
    private WebTablesSteps webTablesSteps;

    public void start() {
        // No-arg factory method: reads -Dbrowser and -Dheadless.
        driver = WebDriverFactory.createDriver();
        textBoxSteps = new TextBoxSteps(driver);
        webTablesSteps = new WebTablesSteps(driver);
    }

    public void quit() {
        if (driver != null) {
            driver.quit();
        }
    }

    public TextBoxSteps textBoxSteps() {
        return textBoxSteps;
    }

    public WebTablesSteps webTablesSteps() {
        return webTablesSteps;
    }
}
