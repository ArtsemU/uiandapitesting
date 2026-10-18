package lessonslearned.cucumber.orangehrm.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import ui.pages.BasePage;

public class Dashboard extends BasePage {

    private final String pageTitle = "//h6";

    public Dashboard(WebDriver driver) {
        super(driver);
    }

    public String getPageTitle() {
        return getText(driver.findElement(By.xpath(pageTitle)));
    }
}
