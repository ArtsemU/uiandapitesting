package lessonslearned.cucumber.orangehrm.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ui.pages.BasePage;

public class LoginPage extends BasePage {
    private static final Logger log = LoggerFactory.getLogger(LoginPage.class);

    private final String loginField = "//input[@placeholder= 'Username']";
    private final String passwordField = "//input[@name= 'password']";
    private final String loginButton = "//button[@type= 'submit']";


    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public void fillInUserName(String username) {
        sendKeys(driver.findElement(By.xpath(loginField)), username);
    }

    public void fillInPassword(String password) {
        sendKeys(driver.findElement(By.xpath(passwordField)), password);
    }

    public void clickLoginButton() {
        click(driver.findElement(By.xpath(loginButton)));
    }
}
