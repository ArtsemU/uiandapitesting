package lessonslearned.cucumber.orangehrm.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import lessonslearned.cucumber.orangehrm.UiContext;

public class OrangeHRMStepDefenition {

    private final String ORANGE_LOGIN_PAGE = "https://opensource-demo.orangehrmlive.com/";

    private final UiContext context;

    public OrangeHRMStepDefenition(UiContext context) {
        this.context = context;
    }

    @Given("the user is on the OrangeHRM login page")
    public void theUserIsOnTheOrangeHRMLoginPage() {
        context.getDriver().get(ORANGE_LOGIN_PAGE);
    }

    @When("user enters username and password")
    public void theUserEntersValidCredentials() {
        // Implementation for this step
    }
}
