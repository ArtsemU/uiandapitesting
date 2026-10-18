package lessonslearned.cucumber.orangehrm.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lessonslearned.cucumber.orangehrm.UiContext;
import lessonslearned.cucumber.orangehrm.pages.Dashboard;
import lessonslearned.cucumber.orangehrm.pages.LoginPage;
import org.testng.Assert;

public class OrangeHRMStepDefenition {

    private static final String VALID_USERNAME = "Admin";
    private static final String VALID_PASSWORD = "admin123";

    private final UiContext context;
    private LoginPage loginPage;
    private Dashboard dashboard;

    public OrangeHRMStepDefenition(UiContext context) {
        this.context = context;
    }

    private LoginPage loginPage() {
        if (loginPage == null) {
            loginPage = new LoginPage(context.getDriver());
        }
        return loginPage;
    }

    private Dashboard dashboard() {
        if (dashboard == null) {
            dashboard = new Dashboard(context.getDriver());
        }
        return dashboard;
    }

    @Given("the user is on the OrangeHRM login page")
    public void theUserIsOnTheOrangeHrmLoginPage() {
        loginPage().open();
    }

    @When("the user logs in with valid credentials")
    public void theUserLogsInWithValidCredentials() {
        loginPage().fillInUserName(VALID_USERNAME);
        loginPage().fillInPassword(VALID_PASSWORD);
        loginPage().clickLoginButton();
    }

    @Then("the user sees the dashboard")
    public void theUserSeesTheDashboard() {
        Assert.assertTrue(dashboard().isDisplayed(), "Dashboard is not displayed after a successful login");
        Assert.assertEquals(dashboard().getPageTitle(), "Dashboard",
                "Dashboard is not shown after a successful login");
    }

    @When("the user logs in as {string} with password {string}")
    public void theUserLogsInAs(String username, String password) {
        loginPage().fillInUserName(username);
        loginPage().fillInPassword(password);
        loginPage().clickLoginButton();
    }

    @Then("user is navigated to home page")
    public void userIsNavigatedToHomePage() {
        Assert.assertEquals(loginPage().getErrorMessage(), "Invalid credentials",
                "User seens an error mesage - Invalid credentials on Login page");
    }

    @Then("the user sees the 'Invalid credentials' error")
    public void theUserSeesTheInvalidCredentialsError() {
        Assert.assertTrue(loginPage().isErrorBlockDisplayed(), "Error block is not displayed");
        Assert.assertEquals(loginPage().getErrorMessage(), "Invalid credentials",
                "User sees an error message - Invalid credentials on Login page");
    }
}
