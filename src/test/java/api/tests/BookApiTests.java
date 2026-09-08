package api.tests;

import api.models.*;
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
        log.info("Generated credentials for user {}", userCredentials.getUserName());

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

        log.info("Step #3 - get user data");
        Response rsUserData = apiSteps.getUserData(user.getUserID(), token.getToken());
        Assert.assertEquals(rsUserData.statusCode(), 200, "Expected status : 200");
        UserInfo userBeforeAdd = rsUserData.as(UserInfo.class);
        Assert.assertEquals(userBeforeAdd.getBooks().size(), 0, "Books should be empty before adding");

        log.info("Step #4 - get all Books");
        Response rsBooks = apiSteps.getAllBooks();
        Assert.assertEquals(rsBooks.statusCode(), 200, "Expected status : 200");
        Books books = rsBooks.as(Books.class);
        Assert.assertFalse(books.getBooks().isEmpty(), "Catalogue should not be empty");

        log.info("Step #5 - choose isbn");
        String isbn = books.getBooks().get(0).getIsbn();
        log.info("Selected isbn : {}", isbn);

        log.info("Step #6 - add book to user");
        Response rsAddBookToUser = apiSteps.addBookToUser(user.getUserID(), isbn, token.getToken());
        Assert.assertEquals(rsAddBookToUser.statusCode(), 201, "Expected status : 201");
        Books booksAfterAddedStep = rsAddBookToUser.as(Books.class);
        Assert.assertEquals(booksAfterAddedStep.getBooks().get(0).getIsbn(), isbn, "Collection Of isbn not matched");

        log.info("Step #7 - get user data");
        Response userAfterAddBookRs = apiSteps.getUserData(user.getUserID(), token.getToken());
        Assert.assertEquals(userAfterAddBookRs.statusCode(), 200, "Expected status : 200");
        UserInfo userAfterAdd = userAfterAddBookRs.as(UserInfo.class);
        Assert.assertEquals(userAfterAdd.getBooks().size(), 1, "User should have exactly one book");
        Assert.assertEquals(userAfterAdd.getBooks().get(0), books.getBooks().get(0),
                "Book in collection does not match the one selected from the catalogue");

        log.info("Step #8 - remove book from user");
        Response bookRemoved = apiSteps.removeBook(user.getUserID(), isbn, token.getToken());
        Assert.assertEquals(bookRemoved.statusCode(), 204, "Expected status : 204");

        log.info("Step #9 - get user data");
        Response userAfterRemovingBookRs = apiSteps.getUserData(user.getUserID(), token.getToken());
        Assert.assertEquals(userAfterRemovingBookRs.statusCode(), 200, "Expected status : 200");
        UserInfo userAfterRemoving = userAfterRemovingBookRs.as(UserInfo.class);
        Assert.assertEquals(userAfterRemoving.getBooks().size(), 0, "User should have no any books");

        log.info("Step #10 - remove user");
        Response removeUser = apiSteps.removeUser(user.getUserID(), token.getToken());
        Assert.assertEquals(removeUser.statusCode(), 204, "Expected status : 204");

        log.info("Step #11 - get user");
        Response removedUser = apiSteps.getUserData(user.getUserID(), token.getToken());
        Assert.assertEquals(removedUser.statusCode(), 401, "Expected status : 401");
        ErrorResponse errorResponse = removedUser.as(ErrorResponse.class);
        Assert.assertEquals(errorResponse.getMessage(), "User not found!", "Reading a deleted user should report that the user was not found");
    }
}
