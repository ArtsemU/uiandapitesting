package bdd;

import api.models.Books;
import api.steps.ApiSteps;
import io.cucumber.java.en.Given;
import io.restassured.response.Response;
import org.testng.Assert;

public class CatalogueStepDefs {

    private final ScenarioContext context;
    private final ApiSteps apiSteps;

    public CatalogueStepDefs(ScenarioContext context, ApiSteps apiSteps) {
        this.context = context;
        this.apiSteps = apiSteps;
    }

    @Given("the bookstore catalogue has books")
    public void theBookstoreCatalogueHasBooks() {
        Response rs = apiSteps.getAllBooks();
        Assert.assertEquals(rs.statusCode(), 200, "Reading the catalogue should succeed");
        Books books = rs.as(Books.class);
        Assert.assertFalse(books.getBooks().isEmpty(), "Catalogue should not be empty");
        context.setCatalogue(books.getBooks());
    }
}
