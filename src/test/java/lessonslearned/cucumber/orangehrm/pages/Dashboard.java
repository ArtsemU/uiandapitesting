package lessonslearned.cucumber.orangehrm.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import ui.pages.BasePage;

public class Dashboard extends BasePage {

    private final String PAGE_Title = "//h6";

    public Dashboard(WebDriver driver) {
        super(driver);
    }

    public String getPageTitle() {
        return getText(driver.findElement(By.xpath(PAGE_Title)));
    }
    public boolean isDisplayed() {
        return isDisplayed(By.xpath(PAGE_Title));      // By-версия — ждёт до 10 с, потом false
    }
}
