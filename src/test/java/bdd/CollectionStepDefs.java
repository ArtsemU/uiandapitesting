package bdd;

import api.models.Book;
import api.models.UserInfo;
import api.steps.ApiSteps;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import org.testng.Assert;

public class CollectionStepDefs {

    private final ScenarioContext context;
    private final ApiSteps apiSteps;

    public CollectionStepDefs(ScenarioContext context, ApiSteps apiSteps) {
        this.context = context;
        this.apiSteps = apiSteps;
    }

    @When("the user adds the first book of the catalogue to their collection")
    public void theUserAddsTheFirstBookOfTheCatalogue() {
        Book book = context.getCatalogue().get(0);
        context.setSelectedBook(book);
        context.setLastResponse(
                apiSteps.addBookToUser(context.getUserId(), book.getIsbn(), context.getToken()));
    }

    @Then("the book is added to the user's collection")
    public void theBookIsAddedToTheUsersCollection() {
        Assert.assertEquals(context.getLastResponse().statusCode(), 201,
                "Adding a catalogue book to the collection should succeed");
    }

    @When("the user removes the book from their collection")
    public void theUserRemovesTheBookFromTheirCollection() {
        context.setLastResponse(apiSteps.removeBook(
                context.getUserId(), context.getSelectedBook().getIsbn(), context.getToken()));
    }

    @Then("the book is removed from the user's collection")
    public void theBookIsRemovedFromTheUsersCollection() {
        Assert.assertEquals(context.getLastResponse().statusCode(), 204,
                "Removing a book from the collection should succeed");
    }

    @Then("the user's collection is empty")
    public void theUsersCollectionIsEmpty() {
        UserInfo user = viewedUser();
        Assert.assertEquals(user.getBooks().size(), 0, "User's collection should be empty");
    }

    @Then("the user's collection holds only that book")
    public void theUsersCollectionHoldsOnlyThatBook() {
        UserInfo user = viewedUser();
        Assert.assertEquals(user.getBooks().size(), 1, "User should have exactly one book");
        Assert.assertEquals(user.getBooks().get(0), context.getSelectedBook(),
                "Book in collection does not match the one selected from the catalogue");
    }

    // Reads the account fetched by the preceding "the user views their account" step.
    private UserInfo viewedUser() {
        Response rs = context.getLastResponse();
        Assert.assertEquals(rs.statusCode(), 200, "Reading the user should succeed");
        return rs.as(UserInfo.class);
    }
}
