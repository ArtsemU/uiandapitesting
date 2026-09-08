package api;

import api.models.AddBook;
import api.models.RemoveBook;
import io.restassured.RestAssured;
import io.restassured.response.Response;

/**
 * Client for the demoQA Bookstore API. Methods build the request and return the
 * raw Response — no deserialisation, no status-code checking. Deserialising
 * and asserting are the steps layer's and test's jobs respectively.
 */
public class BookstoreClient {

    public Response getAllBooks() {
        return RestAssured.given()
                .spec(ApiSpec.SPEC)
                .get("/BookStore/v1/Books");
    }

    public Response getBook(String isbn) {
        return RestAssured.given()
                .spec(ApiSpec.SPEC)
                .queryParam("ISBN", isbn)
                .get("/BookStore/v1/Book");
    }

    public Response addBook(AddBook request, String token) {
        return RestAssured.given()
                .spec(ApiSpec.SPEC)
                .header("Authorization", "Bearer " + token)
                .body(request)
                .post("/BookStore/v1/Books");
    }

    public Response removeBook(RemoveBook request, String token) {
        return RestAssured.given()
                .spec(ApiSpec.SPEC)
                .header("Authorization", "Bearer " + token)
                .body(request)
                .delete("/BookStore/v1/Book");
    }

    public Response removeAllBooks(String userId, String token) {
        return RestAssured.given()
                .spec(ApiSpec.SPEC)
                .header("Authorization", "Bearer " + token)
                .queryParam("UserId", userId)
                .delete("/BookStore/v1/Books");
    }
}
