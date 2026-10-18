package lessonslearned.cucumber.orangehrm;

import org.openqa.selenium.WebDriver;

public class UiContext {
    private WebDriver driver;

    public WebDriver getDriver() {
        if (driver == null) {
            throw new IllegalStateException("No browser: the scenario is missing the @ui tag");
        }
        return driver;
    }

    void setDriver(WebDriver driver) { this.driver = driver; }

    boolean hasDriver() { return driver != null; }
}
