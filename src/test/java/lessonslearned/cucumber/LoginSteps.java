package lessonslearned.cucumber;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class LoginSteps {

    @Given("user is on login page")
    public void userIsOnLoginPage() {
        System.out.println("user is on login page");
    }

    @When("user enters username and password")
    public void userEntersUsernameAndPassword() {
        System.out.println("user enters username and password");
    }

    @When("click on login button")
    public void clickOnLoginButton() {
        System.out.println("click on login button");
    }

    @Then("user is navigated to home page")
    public void userIsNavigatedToHomePage() {
        System.out.println("user is navigated to home page");
    }
}
