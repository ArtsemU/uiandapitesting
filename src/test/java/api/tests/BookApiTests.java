package api.tests;

import api.models.CreateUserResponse;
import api.models.Token;
import api.models.UserCredentials;
import io.restassured.response.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;


public class BookApiTests {
    private static final Logger log = LoggerFactory.getLogger(BookApiTests.class);
    private final ApiSteps apiSteps = new ApiSteps();

    @Test(description = "BS-001: Add book to user")
    public void addBookToUserE2ETest() {
        log.info("step #0 - generate username and password");
        UserCredentials userCredentials = UserCredentials.unique();
        log.info("User {} created", userCredentials.getUserName());

        log.info("Step #1 - call createUser");
        Response rs = apiSteps.createUserCall(userCredentials);
        Assert.assertEquals(rs.statusCode(), 201, "Expected status : 201");
        CreateUserResponse user = rs.as(CreateUserResponse.class);
        Assert.assertEquals(user.getUsername(), userCredentials.getUserName(), "Username not matched");
        Assert.assertNotNull(user.getUserID(), "UserID should be not null");
        Assert.assertEquals(user.getBooks().size(), 0, "Books size should be 0");

        log.info("Step #2 - generate token");
        Response rsToken = apiSteps.generateToken(userCredentials);
        Assert.assertEquals(rsToken.statusCode(), 200, "Expected status : 200");
        Token token = rsToken.as(Token.class);
        Assert.assertNotNull(token.getToken(), "Token is null");
        Assert.assertEquals(token.getStatus(), "Success", "Status is not Success");
    }

}
