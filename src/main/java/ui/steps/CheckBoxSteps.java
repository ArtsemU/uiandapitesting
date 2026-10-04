package ui.steps;

import io.qameta.allure.Step;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ui.pages.CheckBoxPage;

import java.util.List;

public class CheckBoxSteps extends BaseSteps {
    private static final Logger log = LoggerFactory.getLogger(CheckBoxSteps.class);
    private static final String CHECK_BOX_PAGE = "https://demoqa.com/checkbox";

    private final CheckBoxPage checkBoxPage;

    public CheckBoxSteps(WebDriver driver) {
        super(driver);
        this.checkBoxPage = new CheckBoxPage(driver);
    }

    @Step("Open Check Box page")
    public void openCheckBoxPage() {
        openUrl(CHECK_BOX_PAGE);
    }

    @Step("Expand node {label}")
    public void expandNode(String label) {
        log.info("Expanding node: {}", label);
        checkBoxPage.expandNode(label);
    }

    @Step("Select checkbox {label}")
    public void selectCheckbox(String label) {
        log.info("Selecting checkbox: {}", label);
        checkBoxPage.selectItem(label);
    }

    @Step("Get selected items")
    public List<String> getSelectedItems() {
        List<String> items = checkBoxPage.getSelectedItems();
        log.info("Selected items: {}", items);
        return items;
    }
}
