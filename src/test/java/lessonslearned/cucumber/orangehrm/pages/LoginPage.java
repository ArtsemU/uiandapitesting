package lessonslearned.cucumber.orangehrm.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ui.pages.BasePage;

public class LoginPage extends BasePage {

    private static final String URL = "https://opensource-demo.orangehrmlive.com/";

    private static final By USERNAME_FIELD = By.xpath("//input[@name='username']");
    private static final By PASSWORD_FIELD = By.xpath("//input[@name='password']");
    private static final By LOGIN_BUTTON = By.xpath("//button[@type='submit']");
    private static final By ERROR_MESSAGE = By.xpath("//p[contains(@class,'oxd-alert-content-text')]");
    private static final By ERROR_BLOCK = By.xpath("//div[@class='orangehrm-login-error']");

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public void open() {
        driver.get(URL);
    }

    public void fillInUserName(String username) {
        sendKeys(USERNAME_FIELD, username);
    }

    public void fillInPassword(String password) {
        sendKeys(PASSWORD_FIELD, password);
    }

    public void clickLoginButton() {
        click(LOGIN_BUTTON);
    }

    public String getErrorMessage() {
        return getText(ERROR_MESSAGE);
    }

    public boolean isErrorMessageDisplayed() {
        return isDisplayed(ERROR_MESSAGE);
    }

    public boolean isErrorBlockDisplayed() {
        return isDisplayed(ERROR_BLOCK);
    }
}
