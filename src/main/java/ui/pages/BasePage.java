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

    public void click(WebElement element) {
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView(true);", element);
        wait.until(
                ExpectedConditions.elementToBeClickable(element)
        );

        element.click();
    }

    public void sendKeys(WebElement element, String text) {
        wait.until(
                ExpectedConditions.visibilityOf(element)
        );
        element.clear();
        element.sendKeys(text);
    }

    public String getText(WebElement element) {
        wait.until(
                ExpectedConditions.visibilityOf(element)
        );
        return element.getText();
    }

    public boolean isDisplayed(WebElement element) {
        try {
            wait.until(
                    ExpectedConditions.visibilityOf(element)
            );
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    public void waitForPageLoad() {
        wait.until(
                driver ->
                        ((JavascriptExecutor)driver)
                                .executeScript(
                                        "return document.readyState"
                                )
                                .equals("complete")
        );
    }

    // added for https://opensource-demo.orangehrmlive.com/
    public void click(By locator) {
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
        element.click();
    }

    public void sendKeys(By locator, String text) {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        element.clear();
        element.sendKeys(text);
    }

    public String getText(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator)).getText();
    }

    public boolean isDisplayed() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }
}