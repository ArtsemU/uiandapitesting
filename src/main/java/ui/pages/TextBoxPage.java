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
            By.xpath("//*[@id='submit']");

    // output
    private final By outputName             = By.xpath("//*[@id='name']");
    private final By outputEmail            = By.xpath("//*[@id='email']");
    private final By outputCurrentAddress   = By.xpath("//p[@id='currentAddress']");
    private final By outputPermanentAddress = By.xpath("//p[@id='permanentAddress']");

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

    // demoQA renders each output line as "<Label>:<value>" in one element;
    // parsing that shape is a page-markup concern, not a domain one
    private String parseOutputValue(String fieldName, String text) {
        int separatorIndex = text.indexOf(':');
        if (separatorIndex < 0) {
            throw new IllegalStateException(
                    "Field '" + fieldName + "' output did not contain the expected ':' separator, actual text: '" + text + "'");
        }
        return text.substring(separatorIndex + 1).trim();
    }

    // get text methods
    public String getFullName() {
        return parseOutputValue("Full Name", getText(driver.findElement(outputName)));
    }

    public String getEmail() {
        return parseOutputValue("Email", getText(driver.findElement(outputEmail)));
    }

    public String getCurrentAddress() {
        return parseOutputValue("Current Address", getText(driver.findElement(outputCurrentAddress)));
    }

    public String getPermanentAddress() {
        return parseOutputValue("Permanent Address", getText(driver.findElement(outputPermanentAddress)));
    }
}
