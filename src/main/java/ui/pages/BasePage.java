package ui.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class BasePage  {


    protected WebDriver driver;

    protected WebDriverWait wait;


    public BasePage(WebDriver driver) {

        this.driver = driver;
        this.wait = new WebDriverWait(
                driver,
                Duration.ofSeconds(10)
        );
    }

    protected void click(WebElement element) {
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView(true);", element);
        wait.until(
                ExpectedConditions.elementToBeClickable(element)
        );

        element.click();
    }

    protected void sendKeys(WebElement element, String text) {
        wait.until(
                ExpectedConditions.visibilityOf(element)
        );
        element.clear();
        element.sendKeys(text);
    }

    protected String getText(WebElement element) {
        wait.until(
                ExpectedConditions.visibilityOf(element)
        );
        return element.getText();
    }

    protected boolean isDisplayed(WebElement element) {
        try {
            wait.until(
                    ExpectedConditions.visibilityOf(element)
            );
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    protected void waitForPageLoad() {
        wait.until(
                driver ->
                        ((JavascriptExecutor)driver)
                                .executeScript(
                                        "return document.readyState"
                                )
                                .equals("complete")
        );
    }
}