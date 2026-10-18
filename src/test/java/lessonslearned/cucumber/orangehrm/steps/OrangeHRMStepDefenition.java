package lessonslearned.cucumber.orangehrm.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import lessonslearned.cucumber.orangehrm.UiContext;
import lessonslearned.cucumber.orangehrm.pages.LoginPage;

public class OrangeHRMStepDefenition {

    private static final String VALID_USERNAME = "Admin";
    private static final String VALID_PASSWORD = "admin123";
    private static final String ORANGE_LOGIN_PAGE = "https://opensource-demo.orangehrmlive.com/web/index.php/auth/login";

    private final UiContext context;
    private LoginPage loginPage;


    public OrangeHRMStepDefenition(UiContext context) {
        this.context = context;
    }

    @Given("the user is on the OrangeHRM login page")
    public void theUserIsOnTheOrangeHRMLoginPage() {
        context.getDriver().get(ORANGE_LOGIN_PAGE);
    }

    @When("user enters username and password")
    public void theUserEntersValidCredentials() {
        loginPage.fillInUserName(VALID_USERNAME);
        loginPage.fillInPassword(VALID_PASSWORD);
        loginPage.clickLoginButton();
    }
}
