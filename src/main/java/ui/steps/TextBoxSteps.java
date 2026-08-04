package ui.steps;

import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ui.pages.TextBoxPage;

public class TextBoxSteps extends BaseSteps{
    private static final Logger log = LoggerFactory.getLogger(TextBoxSteps.class);
    private static final String TEXT_BOX_PAGE = "https://demoqa.com/text-box";

    private final TextBoxPage textBoxPage;

    public void openTBPage() {
        openUrl(TEXT_BOX_PAGE);
    }

    public TextBoxSteps(WebDriver driver) {
        super(driver);
        this.textBoxPage = new TextBoxPage(driver);
    }

    public void fillFullName(String name) {
        log.info("enter full name : {}", name);
        textBoxPage.enterFullName(name);
    }

    public void fillEmail(String email) {
        log.info("enter Email : {}", email);
        textBoxPage.enterEmail(email);
    }

    public void fillCurrAddress(String curAddress) {
        log.info("enter currAddress : {}", curAddress);
        textBoxPage.enterCurrentAddress(curAddress);
    }

    public void fillPerAddress(String perAddress) {
        log.info("enter currAddress : {}", perAddress);
        textBoxPage.enterPermanentAddress(perAddress);
    }

    public void submitForm() {
        textBoxPage.clickSubmitButton();
    }

    public String getOutputName() {
        String value = textBoxPage.getFullName();
        String name = value.split(":")[1];
        log.info("Got full name : {}", name);
        return name;
    }

    public String getOutputEmail() {
        String value = textBoxPage.getEmail();
        String email = value.split(":")[1];
        log.info("Got Email : {}", email);
        return email;
    }

    public String getOutputCurrAddress() {
        String value = textBoxPage.getCurrentAddress();
        log.info("Value : {}", value);
        String currAddress = value.split(":")[1];
        log.info("Got CurrAddress : {}", currAddress);
        return currAddress;
    }

    public String getOutputPerAddress() {
        String value = textBoxPage.getPermanentAddress();
        String perAddress = value.split(":")[1];
        log.info("Got perAddress : {}", perAddress);
        return perAddress;
    }

}
