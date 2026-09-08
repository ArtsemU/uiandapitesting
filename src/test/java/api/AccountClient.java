package api;

import api.models.UserCredentials;
import io.restassured.RestAssured;
import io.restassured.response.Response;

/**
 * Client for the demoQA Account API. Methods build the request and return the
 * raw Response — no deserialisation, no status-code checking. Deserialising
 * and asserting are the steps layer's and test's jobs respectively.
 */
public class AccountClient {

    public Response createUser(UserCredentials credentials) {
        return RestAssured.given()
                .spec(ApiSpec.SPEC)
                .body(credentials)
                .post("/Account/v1/User");
    }

    public Response generateToken(UserCredentials credentials) {
        return RestAssured.given()
                .spec(ApiSpec.SPEC)
                .body(credentials)
                .post("/Account/v1/GenerateToken");
    }

    public Response getUser(String userId, String token) {
        return RestAssured.given()
                .spec(ApiSpec.SPEC)
                .header("Authorization", "Bearer " + token)
                .get("/Account/v1/User/{userId}", userId);
    }

    public Response deleteUser(String userId, String token) {
        return RestAssured.given()
                .spec(ApiSpec.SPEC)
                .header("Authorization", "Bearer " + token)
                .delete("/Account/v1/User/{userId}", userId);
    }
}
