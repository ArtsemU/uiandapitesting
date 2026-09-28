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

public class UserStepDefs {

    private final ScenarioContext context;
    private final ApiSteps apiSteps;

    public UserStepDefs(ScenarioContext context, ApiSteps apiSteps) {
        this.context = context;
        this.apiSteps = apiSteps;
    }

    @When("a new user registers")
    public void aNewUserRegisters() {
        UserCredentials credentials = UserCredentials.unique();
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

    @When("the user logs in")
    public void theUserLogsIn() {
        context.setLastResponse(apiSteps.generateToken(context.getCredentials()));
    }

    @Then("the user receives a token")
    public void theUserReceivesAToken() {
        Response rs = context.getLastResponse();
        Assert.assertEquals(rs.statusCode(), 200, "Token generation should succeed");
        Token token = rs.as(Token.class);
        Assert.assertNotNull(token.getToken(), "Token generation should return a token");
        context.setToken(token.getToken());
    }

    @When("the user views their account")
    public void theUserViewsTheirAccount() {
        context.setLastResponse(apiSteps.getUserData(context.getUserId(), context.getToken()));
    }

    @When("the user deletes their account")
    public void theUserDeletesTheirAccount() {
        context.setLastResponse(apiSteps.removeUser(context.getUserId(), context.getToken()));
    }

    @Then("the user's account is deleted")
    public void theUsersAccountIsDeleted() {
        Assert.assertEquals(context.getLastResponse().statusCode(), 204, "Deleting the account should succeed");
        // Deleted by the scenario itself: the cleanup hook must not try again.
        context.forgetCreatedUser(context.getUserId());
    }

    @Then("the user's account is not found")
    public void theUsersAccountIsNotFound() {
        Response rs = context.getLastResponse();
        Assert.assertEquals(rs.statusCode(), 401, "Reading a deleted user should be rejected");
        ErrorResponse error = rs.as(ErrorResponse.class);
        Assert.assertEquals(error.getMessage(), "User not found!",
                "Reading a deleted user should report that the user was not found");
    }
}
