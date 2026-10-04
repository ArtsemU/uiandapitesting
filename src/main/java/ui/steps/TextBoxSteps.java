package ui.steps;

import io.qameta.allure.Step;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ui.pages.TextBoxPage;

public class TextBoxSteps extends BaseSteps{
    private static final Logger log = LoggerFactory.getLogger(TextBoxSteps.class);
    private static final String TEXT_BOX_PAGE = "https://demoqa.com/text-box";

    private final TextBoxPage textBoxPage;

    @Step("Open Text Box page")
    public void openTBPage() {
        openUrl(TEXT_BOX_PAGE);
    }

    public TextBoxSteps(WebDriver driver) {
        super(driver);
        this.textBoxPage = new TextBoxPage(driver);
    }

    @Step("Enter full name {name}")
    public void fillFullName(String name) {
        log.info("enter full name : {}", name);
        textBoxPage.enterFullName(name);
    }

    @Step("Enter email {email}")
    public void fillEmail(String email) {
        log.info("enter Email : {}", email);
        textBoxPage.enterEmail(email);
    }

    @Step("Enter current address {curAddress}")
    public void fillCurrAddress(String curAddress) {
        log.info("enter currAddress : {}", curAddress);
        textBoxPage.enterCurrentAddress(curAddress);
    }

    @Step("Enter permanent address {perAddress}")
    public void fillPerAddress(String perAddress) {
        log.info("enter currAddress : {}", perAddress);
        textBoxPage.enterPermanentAddress(perAddress);
    }

    @Step("Submit form")
    public void submitForm() {
        textBoxPage.clickSubmitButton();
    }

    @Step("Get output full name")
    public String getOutputName() {
        String name = textBoxPage.getFullName();
        log.info("Got full name : {}", name);
        return name;
    }

    @Step("Get output email")
    public String getOutputEmail() {
        String email = textBoxPage.getEmail();
        log.info("Got Email : {}", email);
        return email;
    }

    @Step("Get output current address")
    public String getOutputCurrAddress() {
        String currAddress = textBoxPage.getCurrentAddress();
        log.info("Got CurrAddress : {}", currAddress);
        return currAddress;
    }

    @Step("Get output permanent address")
    public String getOutputPerAddress() {
        String perAddress = textBoxPage.getPermanentAddress();
        log.info("Got perAddress : {}", perAddress);
        return perAddress;
    }

}
