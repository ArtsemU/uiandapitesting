package ui.steps;

import org.openqa.selenium.WebDriver;

public class BaseSteps {
    protected WebDriver driver;

    public BaseSteps(WebDriver driver) {
        this.driver = driver;
    }

    protected void openUrl(String url) {
        driver.get(url);
    }
}
