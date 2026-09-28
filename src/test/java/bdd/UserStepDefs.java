package bdd;

import api.models.CreateUserResponse;
import api.models.ErrorResponse;
import api.models.UserCredentials;
import api.steps.ApiSteps;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import org.testng.Assert;
import testing.testdata.BookstoreTestData;

public class UserStepDefs {

    private final ScenarioContext context;
    private final ApiSteps apiSteps;

    public UserStepDefs(ScenarioContext context, ApiSteps apiSteps) {
        this.context = context;
        this.apiSteps = apiSteps;
    }

    // Regex rather than a Cucumber Expression: one binding covers both phrasings,
    // the password is null when the optional part is absent.
    @When("^a new user registers(?: with the password \"([^\"]*)\")?$")
    public void aNewUserRegisters(String password) {
        UserCredentials credentials = UserCredentials.unique();
        if (password != null) {
            credentials = UserCredentials.builder()
                    .userName(credentials.getUserName())
                    .password(password)
                    .build();
        }
        context.setCredentials(credentials);
        context.setLastResponse(apiSteps.createUserCall(credentials));
    }

    @Then("the user is registered with an empty collection")
    public void theUserIsRegisteredWithAnEmptyCollection() {
        Response rs = context.getLastResponse();
        Assert.assertEquals(rs.statusCode(), 201, "User registration should succeed");
        CreateUserResponse user = rs.as(CreateUserResponse.class);
        Assert.assertNotNull(user.getUserID(), "Registration should return a userID");
        context.setUserId(user.getUserID());
        context.recordCreatedUser(user.getUserID(), context.getCredentials());
        Assert.assertEquals(user.getBooks().size(), 0, "A newly registered user should have no books");
    }

    @Then("the registration is rejected for breaking the password rules")
    public void theRegistrationIsRejectedForBreakingThePasswordRules() {
        Response rs = context.getLastResponse();
        if (rs.statusCode() == 201) {
            // Unexpectedly created: hand the user to the cleanup hook before the assertion fails.
            context.recordCreatedUser(rs.as(CreateUserResponse.class).getUserID(), context.getCredentials());
        }
        Assert.assertEquals(rs.statusCode(), 400, "A password breaking the rules should be rejected");
        ErrorResponse error = rs.as(ErrorResponse.class);
        Assert.assertEquals(error.getCode(), "1300", "A rejected password should report code 1300");
        Assert.assertEquals(error.getMessage(), BookstoreTestData.PASSWORD_RULES_MESSAGE,
                "A rejected password should report the password-rules message");
    }

    @When("the user logs in")
    public void theUserLogsIn() {
        context.setLastResponse(apiSteps.generateToken(context.getCredentials()));
    }

    @When("the user deletes their account")
    public void theUserDeletesTheirAccount() {
        context.setLastResponse(apiSteps.removeUser(context.getUserId(), context.getToken()));
    }

    @Then("the user's account can no longer be found")
    public void theUsersAccountCanNoLongerBeFound() {
        Assert.assertEquals(context.getLastResponse().statusCode(), 204, "Deleting the account should succeed");
        // Deleted by the scenario itself: the cleanup hook must not try again.
        context.forgetCreatedUser(context.getUserId());

        Response rs = apiSteps.getUserData(context.getUserId(), context.getToken());
        Assert.assertEquals(rs.statusCode(), 401, "Reading a deleted user should be rejected");
        ErrorResponse error = rs.as(ErrorResponse.class);
        Assert.assertEquals(error.getMessage(), "User not found!",
                "Reading a deleted user should report that the user was not found");
    }
}
