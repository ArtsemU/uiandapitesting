package lessonslearned.cucumber;

import factory.WebDriverFactory;
import io.cucumber.java.After;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import java.time.Duration;

public class GoogleSearchSteps {

    private static final String GOOGLE_URL = "https://www.google.com";
    private static final String SEARCH_TEXT = "Selenium WebDriver";
    private static final By SEARCH_BOX = By.name("q");
    private static final Duration WAIT_TIMEOUT = Duration.ofSeconds(10);

    private WebDriver driver;

    @Given("browser is open")
    public void browser_is_open() {
        driver = WebDriverFactory.createDriver();
    }

    @Given("user is on Google Search page")
    public void user_is_on_google_search_page() {
        driver.get(GOOGLE_URL);
        new WebDriverWait(driver, WAIT_TIMEOUT)
                .until(ExpectedConditions.visibilityOfElementLocated(SEARCH_BOX));
    }

    @When("user enters a text in Search box")
    public void user_enters_a_text_in_search_box() {
        WebElement searchBox = new WebDriverWait(driver, WAIT_TIMEOUT)
                .until(ExpectedConditions.elementToBeClickable(SEARCH_BOX));
        searchBox.sendKeys(SEARCH_TEXT);
    }

    @When("hits enter")
    public void hits_enter() {
        driver.findElement(SEARCH_BOX).sendKeys(Keys.ENTER);
    }

    @Then("user is navigated to search results")
    public void user_is_navigated_to_search_results() {
        boolean onSearchResults = new WebDriverWait(driver, WAIT_TIMEOUT)
                .until(ExpectedConditions.urlContains("/search"));
        Assert.assertTrue(onSearchResults, "The browser did not navigate to Google search results");
    }

    @After
    public void close_browser() {
        if (driver != null) {
            driver.quit();
        }
    }
}
