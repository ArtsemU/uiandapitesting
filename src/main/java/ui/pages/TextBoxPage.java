package ui.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TextBoxPage extends BasePage{
    private static final Logger log = LoggerFactory.getLogger(TextBoxPage.class);

    public TextBoxPage(WebDriver driver) {
        super(driver);
    }

    // input
    private final By fullNameField =
            By.xpath("//input[@id='userName']");

    private final By emailField =
            By.xpath("//input[@id='userEmail']");

    private final By currentAddressField =
            By.xpath("//textarea[@id='currentAddress']");

    private final By permanentAddressField =
            By.xpath("//textarea[@id='permanentAddress']");

    // button
    private final By submitButton =
            By.id("submit");

    // output
    private final By outputName             = By.id("name");
    private final By outputEmail            = By.id("email");
    private final By outputCurrentAddress   = By.id("currentAddress");
    private final By outputPermanentAddress = By.id("permanentAddress");

    // methods
    public void enterFullName(String name) {
        sendKeys(driver.findElement(fullNameField), name);
    }

    public void enterEmail(String email) {
        sendKeys(driver.findElement(emailField), email);
    }

    public void enterCurrentAddress(String address) {
        sendKeys(driver.findElement(currentAddressField), address);
    }

    public void enterPermanentAddress(String address) {
        sendKeys(driver.findElement(permanentAddressField), address);
    }

    // click on button
    public void clickSubmitButton() {
        click(driver.findElement(submitButton));
    }

    // get text methods
    public String getFullName() {
        return getText(driver.findElement(outputName));
    }

    public String getEmail() {
        return getText(driver.findElement(outputEmail));
    }

    public String getCurrentAddress() {
        return getText(driver.findElement(outputCurrentAddress));
    }

    public String getPermanentAddress() {
        return getText(driver.findElement(outputPermanentAddress));
    }
}
