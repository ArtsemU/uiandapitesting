package api;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

/**
 * Single, reusable RestAssured request specification for all API clients.
 * Built once; do not rebuild per call.
 */
public final class ApiSpec {
    public static final RequestSpecification SPEC = new RequestSpecBuilder()
            .setBaseUri(Config.baseUrl())
            .setContentType(ContentType.JSON)
            .addFilter(new ApiLoggingFilter())
            .build();

    private ApiSpec() {
    }
}
