package bdd;

import api.models.CreateUserResponse;
import api.models.ErrorResponse;
import api.models.Token;
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

    @When("a new user registers")
    public void aNewUserRegisters() {
        register(UserCredentials.unique());
    }

    @When("a new user registers with the password {string}")
    public void aNewUserRegistersWithThePassword(String password) {
        register(UserCredentials.builder()
                .userName(UserCredentials.unique().getUserName())
                .password(password)
                .build());
    }

    @Then("the user is registered with an empty collection")
    public void theUserIsRegisteredWithAnEmptyCollection() {
        Response rs = context.getLastResponse();
        Assert.assertEquals(rs.statusCode(), 201, "User registration should succeed");
        CreateUserResponse user = rs.as(CreateUserResponse.class);
        Assert.assertNotNull(user.getUserID(), "Registration should return a userID");
        Assert.assertEquals(user.getBooks().size(), 0, "A newly registered user should have no books");
    }

    @Then("the registration is rejected for breaking the password rules")
    public void theRegistrationIsRejectedForBreakingThePasswordRules() {
        Response rs = context.getLastResponse();
        Assert.assertEquals(rs.statusCode(), 400, "A password breaking the rules should be rejected");
        ErrorResponse error = rs.as(ErrorResponse.class);
        Assert.assertEquals(error.getCode(), BookstoreTestData.PASSWORD_RULES_CODE, "A rejected password should report the password-rules error code");
        Assert.assertEquals(error.getMessage(), BookstoreTestData.PASSWORD_RULES_MESSAGE,
                "A rejected password should report the password-rules message");
    }

    @When("the user logs in")
    public void theUserLogsIn() {
        Response rs = apiSteps.generateToken(context.getCredentials());
        context.setLastResponse(rs);
        // Stored here so later steps never depend on a Then; the status is asserted in the Then that follows.
        if (rs.statusCode() == 200) {
            context.setToken(rs.as(Token.class).getToken());
        }
    }

    @When("the user deletes their account")
    public void theUserDeletesTheirAccount() {
        Response rs = apiSteps.removeUser(context.getUserId(), context.getToken());
        context.setLastResponse(rs);
        // Deleted by the scenario itself: the cleanup hook must not try again.
        if (rs.statusCode() == 204) {
            context.forgetCreatedUser(context.getUserId());
        }
    }

    @Then("the user's account can no longer be found")
    public void theUsersAccountCanNoLongerBeFound() {
        Assert.assertEquals(context.getLastResponse().statusCode(), 204, "Deleting the account should succeed");

        Response rs = apiSteps.getUserData(context.getUserId(), context.getToken());
        Assert.assertEquals(rs.statusCode(), 401, "Reading a deleted user should be rejected");
        ErrorResponse error = rs.as(ErrorResponse.class);
        Assert.assertEquals(error.getMessage(), BookstoreTestData.USER_NOT_FOUND_MESSAGE,
                "Reading a deleted user should report that the user was not found");
    }

    // Records the user for cleanup as soon as it exists, so a failing Then cannot leak it.
    private void register(UserCredentials credentials) {
        context.setCredentials(credentials);
        Response rs = apiSteps.createUserCall(credentials);
        context.setLastResponse(rs);
        if (rs.statusCode() == 201) {
            String userId = rs.as(CreateUserResponse.class).getUserID();
            context.setUserId(userId);
            context.recordCreatedUser(userId, credentials);
        }
    }
}
