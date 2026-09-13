package api.tests;

import api.Config;
import api.models.*;
import io.restassured.response.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import java.util.LinkedHashMap;
import java.util.Map;


public class BookApiTests {
    private static final Logger log = LoggerFactory.getLogger(BookApiTests.class);
    private final ApiSteps apiSteps = new ApiSteps();
    private final Map<String, String> createdUsers = new LinkedHashMap<>();

    @AfterMethod
    public void cleanupUsers() {
        for (Map.Entry<String, String> entry : createdUsers.entrySet()) {
            try {
                apiSteps.removeUser(entry.getKey(), entry.getValue());
            } catch (Exception e) {
                log.warn("Cleanup failed for user {}", entry.getKey(), e);
            }
        }
        createdUsers.clear();
    }

    @Test(description = "BS-001: Add book to user")
    public void addBookToUserE2ETest() {
        log.info("step #0 - generate username and password");
        UserCredentials userCredentials = UserCredentials.unique();
        log.info("Generated credentials for user {}", userCredentials.getUserName());

        log.info("Step #1 - call createUser");
        Response rs = apiSteps.createUserCall(userCredentials);
        Assert.assertEquals(rs.statusCode(), 201, "Expected status : 201");
        CreateUserResponse user = rs.as(CreateUserResponse.class);
        Assert.assertNotNull(user.getUserID(), "UserID should be not null");
        Assert.assertEquals(user.getBooks().size(), 0, "Books size should be 0");

        log.info("Step #2 - generate token");
        Response rsToken = apiSteps.generateToken(userCredentials);
        Assert.assertEquals(rsToken.statusCode(), 200, "Expected status : 200");
        Token token = rsToken.as(Token.class);
        Assert.assertNotNull(token.getToken(), "Token is null");

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

    @Test(description = "BS-002: duplicate username is rejected")
    public void createUserWithExistingUsernameIsRejectedTest() {
        log.info("Step #1 - call createUser");
        UserCredentials userCredentials = UserCredentials.unique();
        Response rs = apiSteps.createUserCall(userCredentials);
        Assert.assertEquals(rs.statusCode(), 201, "Expected status : 201");
        CreateUserResponse user = rs.as(CreateUserResponse.class);

        log.info("Step #2 - call createUser again with the same credentials");
        Response rsDuplicate = apiSteps.createUserCall(userCredentials);
        Assert.assertEquals(rsDuplicate.statusCode(), 406, "Duplicate username should be rejected");
        ErrorResponse errorResponse = rsDuplicate.as(ErrorResponse.class);
        Assert.assertEquals(errorResponse.getCode(), "1204", "Duplicate username should report code 1204");
        Assert.assertEquals(errorResponse.getMessage(), "User exists!", "Duplicate username should report that the user already exists");

        Response rsToken = apiSteps.generateToken(userCredentials);
        Assert.assertEquals(rsToken.statusCode(), 200, "Expected status : 200");
        Token token = rsToken.as(Token.class);
        createdUsers.put(user.getUserID(), token.getToken());
    }

    @Test(description = "BS-003: empty userName is rejected")
    public void createUserWithEmptyUserNameIsRejectedTest() {
        log.info("Step #1 - call createUser with an empty userName");
        UserCredentials userCredentials = UserCredentials.builder()
                .userName("")
                .password(Config.userPassword())
                .build();

        Response rs = apiSteps.createUserCall(userCredentials);
        Assert.assertEquals(rs.statusCode(), 400, "Empty userName should be rejected");
        ErrorResponse errorResponse = rs.as(ErrorResponse.class);
        Assert.assertEquals(errorResponse.getCode(), "1200", "Empty userName should report code 1200");
        Assert.assertEquals(errorResponse.getMessage(), "UserName and Password required.", "Empty userName should report the required-fields message");
    }

    @Test(description = "BS-004: empty password is rejected")
    public void createUserWithEmptyPasswordIsRejectedTest() {
        log.info("Step #1 - call createUser with an empty password");
        UserCredentials userCredentials = UserCredentials.builder()
                .userName(UserCredentials.unique().getUserName())
                .password("")
                .build();

        Response rs = apiSteps.createUserCall(userCredentials);
        Assert.assertEquals(rs.statusCode(), 400, "Empty password should be rejected");
        ErrorResponse errorResponse = rs.as(ErrorResponse.class);
        Assert.assertEquals(errorResponse.getCode(), "1200", "Empty password should report code 1200");
        Assert.assertEquals(errorResponse.getMessage(), "UserName and Password required.", "Empty password should report the required-fields message");
    }

    @Test(description = "BS-005: get user without a token is rejected")
    public void getUserWithoutTokenIsRejectedTest() {
        log.info("Precondition - create user");
        UserCredentials userCredentials = UserCredentials.unique();
        Response rs = apiSteps.createUserCall(userCredentials);
        Assert.assertEquals(rs.statusCode(), 201, "Precondition failed: user was not created");
        CreateUserResponse user = rs.as(CreateUserResponse.class);

        log.info("Step #1 - call getUser with an empty-string token");
        Response rsUser = apiSteps.getUserData(user.getUserID(), "");
        Assert.assertEquals(rsUser.statusCode(), 401, "Missing token should be rejected");
        ErrorResponse errorResponse = rsUser.as(ErrorResponse.class);
        Assert.assertEquals(errorResponse.getCode(), "1200", "Missing token should report code 1200");
        Assert.assertEquals(errorResponse.getMessage(), "User not authorized!", "Missing token should report that the user is not authorized");

        Response rsToken = apiSteps.generateToken(userCredentials);
        Assert.assertEquals(rsToken.statusCode(), 200, "Expected status : 200");
        Token token = rsToken.as(Token.class);
        createdUsers.put(user.getUserID(), token.getToken());

        // last step is missed in conf. need discuss should I update test case or remove that last step?
        // I got his idea! to clean up data pairs name-token is required. Not bad
    }

    @Test(description = "BS-006: get info about invalid user")
    public void getInvalidUserIsRejectedTest() {
        log.info("Precondition - create user and generate token");
        UserCredentials userCredentials = UserCredentials.unique();
        Response rs = apiSteps.createUserCall(userCredentials);
        Assert.assertEquals(rs.statusCode(), 201, "Precondition failed: user was not created");
        CreateUserResponse user = rs.as(CreateUserResponse.class);

        Response rsToken = apiSteps.generateToken(userCredentials);
        Assert.assertEquals(rsToken.statusCode(), 200, "Precondition failed: token was not generated");
        Token token = rsToken.as(Token.class);
        createdUsers.put(user.getUserID(), token.getToken());

        log.info("Step #1 - call getUser for a fake userID with the valid token");
        Response rsInvalidUser = apiSteps.getUserData("1234567890", token.getToken());
        Assert.assertEquals(rsInvalidUser.statusCode(), 401, "Fake userID should be rejected");
        ErrorResponse errorResponse = rsInvalidUser.as(ErrorResponse.class);
        Assert.assertEquals(errorResponse.getCode(), "1207", "Fake userID should report code 1207");
        Assert.assertEquals(errorResponse.getMessage(), "User not found!", "Fake userID should report that the user was not found");
    }

    @Test(description = "BS-007: token grants access to own user only")
    public void tokenDoesNotGrantAccessToAnotherUserTest() {
        log.info("Precondition - create user A and user B, generate token for A");
        String uniqueBase = UserCredentials.unique().getUserName();
        UserCredentials userACredentials = UserCredentials.builder()
                .userName(uniqueBase + "_A")
                .password(Config.userPassword())
                .build();
        UserCredentials userBCredentials = UserCredentials.builder()
                .userName(uniqueBase + "_B")
                .password(Config.userPassword())
                .build();

        Response rsUserA = apiSteps.createUserCall(userACredentials);
        Assert.assertEquals(rsUserA.statusCode(), 201, "Precondition failed: user A was not created");
        CreateUserResponse userA = rsUserA.as(CreateUserResponse.class);

        Response rsUserB = apiSteps.createUserCall(userBCredentials);
        Assert.assertEquals(rsUserB.statusCode(), 201, "Precondition failed: user B was not created");
        CreateUserResponse userB = rsUserB.as(CreateUserResponse.class);

        Response rsTokenA = apiSteps.generateToken(userACredentials);
        Assert.assertEquals(rsTokenA.statusCode(), 200, "Precondition failed: token for user A was not generated");
        Token tokenA = rsTokenA.as(Token.class);
        createdUsers.put(userA.getUserID(), tokenA.getToken());

        log.info("Step #1 - call getUser for user B with user A's token");
        Response rsUserBWithTokenA = apiSteps.getUserData(userB.getUserID(), tokenA.getToken());
        Assert.assertEquals(rsUserBWithTokenA.statusCode(), 401, "Cross-user access should be rejected");
        ErrorResponse errorResponse = rsUserBWithTokenA.as(ErrorResponse.class);
        Assert.assertEquals(errorResponse.getCode(), "1200", "Cross-user access should report code 1200");
        Assert.assertEquals(errorResponse.getMessage(), "User not authorized!", "Cross-user access should report that the user is not authorized");

        Response rsTokenB = apiSteps.generateToken(userBCredentials);
        Assert.assertEquals(rsTokenB.statusCode(), 200, "Expected status : 200");
        Token tokenB = rsTokenB.as(Token.class);
        createdUsers.put(userB.getUserID(), tokenB.getToken());
    }

    @Test(description = "BS-008: add book not in catalogue is rejected")
    public void addBookNotInCatalogueIsRejectedTest() {
        log.info("Precondition - create user and authenticate");
        UserCredentials userCredentials = UserCredentials.unique();
        Response rs = apiSteps.createUserCall(userCredentials);
        Assert.assertEquals(rs.statusCode(), 201, "Precondition failed: user was not created");
        CreateUserResponse user = rs.as(CreateUserResponse.class);

        Response rsToken = apiSteps.generateToken(userCredentials);
        Assert.assertEquals(rsToken.statusCode(), 200, "Precondition failed: token was not generated");
        Token token = rsToken.as(Token.class);
        createdUsers.put(user.getUserID(), token.getToken());

        log.info("Step #1 - call addBook with an ISBN not in the catalogue");
        String isbnNotInCatalogue = "1234567890";
        Response rsAddBook = apiSteps.addBookToUser(user.getUserID(), isbnNotInCatalogue, token.getToken());
        Assert.assertEquals(rsAddBook.statusCode(), 400, "Adding an unknown ISBN should be rejected");
        ErrorResponse errorResponse = rsAddBook.as(ErrorResponse.class);
        Assert.assertEquals(errorResponse.getCode(), "1205", "Adding an unknown ISBN should report code 1205");
        Assert.assertEquals(errorResponse.getMessage(), "ISBN supplied is not available in Books Collection!", "Adding an unknown ISBN should report the correct message");

        log.info("Step #2 - call getUser");
        Response rsUserData = apiSteps.getUserData(user.getUserID(), token.getToken());
        Assert.assertEquals(rsUserData.statusCode(), 200, "Expected status : 200");
        UserInfo userInfo = rsUserData.as(UserInfo.class);
        Assert.assertEquals(userInfo.getBooks().size(), 0, "Books should remain empty after a rejected add");
    }

    @Test(description = "BS-009: add same book twice is rejected")
    public void addSameBookTwiceIsRejectedTest() {
        log.info("Precondition - create user, authenticate, add one book");
        UserCredentials userCredentials = UserCredentials.unique();
        Response rs = apiSteps.createUserCall(userCredentials);
        Assert.assertEquals(rs.statusCode(), 201, "Precondition failed: user was not created");
        CreateUserResponse user = rs.as(CreateUserResponse.class);

        Response rsToken = apiSteps.generateToken(userCredentials);
        Assert.assertEquals(rsToken.statusCode(), 200, "Precondition failed: token was not generated");
        Token token = rsToken.as(Token.class);
        createdUsers.put(user.getUserID(), token.getToken());

        Response rsBooks = apiSteps.getAllBooks();
        Assert.assertEquals(rsBooks.statusCode(), 200, "Precondition failed: catalogue was not retrieved");
        Books books = rsBooks.as(Books.class);
        Assert.assertFalse(books.getBooks().isEmpty(), "Precondition failed: catalogue is empty");
        String isbn = books.getBooks().get(0).getIsbn();

        Response rsAddBook = apiSteps.addBookToUser(user.getUserID(), isbn, token.getToken());
        Assert.assertEquals(rsAddBook.statusCode(), 201, "Precondition failed: book was not added");

        log.info("Step #1 - call addBook with the same ISBN again");
        Response rsAddBookAgain = apiSteps.addBookToUser(user.getUserID(), isbn, token.getToken());
        Assert.assertEquals(rsAddBookAgain.statusCode(), 400, "Adding a duplicate ISBN should be rejected");
        ErrorResponse errorResponse = rsAddBookAgain.as(ErrorResponse.class);
        Assert.assertEquals(errorResponse.getCode(), "1210", "Adding a duplicate ISBN should report code 1210");
        Assert.assertEquals(errorResponse.getMessage(), "ISBN already present in the User's Collection!", "Adding a duplicate ISBN should report the correct message");

        log.info("Step #2 - call getUser");
        Response rsUserData = apiSteps.getUserData(user.getUserID(), token.getToken());
        Assert.assertEquals(rsUserData.statusCode(), 200, "Expected status : 200");
        UserInfo userInfo = rsUserData.as(UserInfo.class);
        Assert.assertEquals(userInfo.getBooks().size(), 1, "Books should contain exactly one element after a rejected duplicate add");
    }

    @Test(description = "BS-010: remove book not in collection is rejected")
    public void removeBookNotInCollectionIsRejectedTest() {
        log.info("Precondition - create user, authenticate, empty collection");
        UserCredentials userCredentials = UserCredentials.unique();
        Response rs = apiSteps.createUserCall(userCredentials);
        Assert.assertEquals(rs.statusCode(), 201, "Precondition failed: user was not created");
        CreateUserResponse user = rs.as(CreateUserResponse.class);

        Response rsToken = apiSteps.generateToken(userCredentials);
        Assert.assertEquals(rsToken.statusCode(), 200, "Precondition failed: token was not generated");
        Token token = rsToken.as(Token.class);
        createdUsers.put(user.getUserID(), token.getToken());

        Response rsBooks = apiSteps.getAllBooks();
        Assert.assertEquals(rsBooks.statusCode(), 200, "Precondition failed: catalogue was not retrieved");
        Books books = rsBooks.as(Books.class);
        Assert.assertFalse(books.getBooks().isEmpty(), "Precondition failed: catalogue is empty");
        String isbn = books.getBooks().get(0).getIsbn();

        log.info("Step #1 - call removeBook with an ISBN from the catalogue that is not in the collection");
        Response rsRemoveBook = apiSteps.removeBook(user.getUserID(), isbn, token.getToken());
        Assert.assertEquals(rsRemoveBook.statusCode(), 400, "Removing a book absent from the collection should be rejected");
        ErrorResponse errorResponse = rsRemoveBook.as(ErrorResponse.class);
        Assert.assertEquals(errorResponse.getCode(), "1206", "Removing an absent book should report code 1206");
        Assert.assertEquals(errorResponse.getMessage(), "ISBN supplied is not available in User's Collection!", "Removing an absent book should report the correct message");
    }

    @Test(description = "BS-011: clear an empty collection")
    public void clearEmptyCollectionTest() {
        log.info("Precondition - create user, authenticate, empty collection");
        UserCredentials userCredentials = UserCredentials.unique();
        Response rs = apiSteps.createUserCall(userCredentials);
        Assert.assertEquals(rs.statusCode(), 201, "Precondition failed: user was not created");
        CreateUserResponse user = rs.as(CreateUserResponse.class);

        Response rsToken = apiSteps.generateToken(userCredentials);
        Assert.assertEquals(rsToken.statusCode(), 200, "Precondition failed: token was not generated");
        Token token = rsToken.as(Token.class);
        createdUsers.put(user.getUserID(), token.getToken());

        log.info("Step #1 - call removeAllBooks");
        Response rsRemoveAllBooks = apiSteps.removeAllBooksFromUser(user.getUserID(), token.getToken());
        Assert.assertEquals(rsRemoveAllBooks.statusCode(), 204, "Expected status : 204");

        log.info("Step #2 - call getUser");
        Response rsUserData = apiSteps.getUserData(user.getUserID(), token.getToken());
        Assert.assertEquals(rsUserData.statusCode(), 200, "Expected status : 200");
        UserInfo userInfo = rsUserData.as(UserInfo.class);
        Assert.assertEquals(userInfo.getBooks().size(), 0, "Books should be empty after clearing an empty collection");
        // add check that books was empty BEFORE removeAll method - need update test case
    }

    @Test(description = "BS-012: get user with an invalid token")
    public void getUserWithInvalidTokenIsRejectedTest() {
        log.info("Precondition - create user");
        UserCredentials userCredentials = UserCredentials.unique();
        Response rs = apiSteps.createUserCall(userCredentials);
        Assert.assertEquals(rs.statusCode(), 201, "Precondition failed: user was not created");
        CreateUserResponse user = rs.as(CreateUserResponse.class);

        log.info("Step #1 - call getUser with an invalid token");
        String invalidToken = "invalid-token-value";
        Response rsUser = apiSteps.getUserData(user.getUserID(), invalidToken);
        Assert.assertEquals(rsUser.statusCode(), 401, "Invalid token should be rejected");
        ErrorResponse errorResponse = rsUser.as(ErrorResponse.class);
        Assert.assertEquals(errorResponse.getCode(), "1200", "Invalid token should report code 1200");
        Assert.assertEquals(errorResponse.getMessage(), "User not authorized!", "Invalid token should report that the user is not authorized");

        Response rsToken = apiSteps.generateToken(userCredentials);
        Assert.assertEquals(rsToken.statusCode(), 200, "Expected status : 200");
        Token token = rsToken.as(Token.class);
        createdUsers.put(user.getUserID(), token.getToken());
    }
}
