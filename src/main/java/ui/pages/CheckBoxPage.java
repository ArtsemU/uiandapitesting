package ui.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

public class CheckBoxPage extends BasePage {
    private static final Logger log = LoggerFactory.getLogger(CheckBoxPage.class);

    public CheckBoxPage(WebDriver driver) {
        super(driver);
    }

    // containers - node/result elements carry no unique ids, so lookups
    // are always scoped inside one of these two rather than page-wide
    private final By treeContainer = By.xpath("//*[contains(concat(' ', normalize-space(@class), ' '), ' rc-tree ')]");
    private final By resultContainer = By.xpath("//*[@id='result']");

    private final By selectedResultItems = By.xpath(".//*[contains(concat(' ', normalize-space(@class), ' '), ' text-success ')]");

    private By checkboxForLabel(String label) {
        return By.xpath(
                ".//span[@class='rc-tree-title' and text()='" + label + "']" +
                        "/ancestor::div[contains(@class,'rc-tree-treenode')][1]" +
                        "//span[contains(@class,'rc-tree-checkbox')]"
        );
    }

    private By switcherForLabel(String label) {
        return By.xpath(
                ".//span[@class='rc-tree-title' and text()='" + label + "']" +
                        "/ancestor::div[contains(@class,'rc-tree-treenode')][1]" +
                        "//span[contains(@class,'rc-tree-switcher')]"
        );
    }

    public void expandNode(String label) {
        WebElement tree = driver.findElement(treeContainer);
        click(tree.findElement(switcherForLabel(label)));
    }

    public void selectItem(String label) {
        WebElement tree = driver.findElement(treeContainer);
        click(tree.findElement(checkboxForLabel(label)));
    }

    public List<String> getSelectedItems() {
        WebElement result = driver.findElement(resultContainer);
        isDisplayed(result);
        return result.findElements(selectedResultItems).stream()
                .map(WebElement::getText)
                .collect(Collectors.toList());
    }
}
