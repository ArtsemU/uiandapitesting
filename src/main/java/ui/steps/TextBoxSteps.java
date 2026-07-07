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

    public void submitForm() {
        textBoxPage.clickSubmitButton();
    }

    public String getFullName() {
        String value = textBoxPage.getFullName();
        String name = value.split(":")[1];
        log.info("Got full name : {}", name);
        return name;
    }



}
