package api;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

/**
 * Single, reusable RestAssured request specification for all API clients.
 * Built once, on first use; do not rebuild per call. Lazy rather than a static field,
 * so a config error (e.g. an unknown -Denv) reaches the run as its own message instead
 * of an ExceptionInInitializerError / NoClassDefFoundError.
 */
public final class ApiSpec {
    private static volatile RequestSpecification spec;

    private ApiSpec() {
    }

    public static RequestSpecification spec() {
        RequestSpecification built = spec;
        if (built == null) {
            synchronized (ApiSpec.class) {
                built = spec;
                if (built == null) {
                    built = build();
                    spec = built;
                }
            }
        }
        return built;
    }

    private static RequestSpecification build() {
        return new RequestSpecBuilder()
                .setBaseUri(Config.baseUrl())
                .setContentType(ContentType.JSON)
                .addFilter(new ApiLoggingFilter())
                // Request/response of every call as an Allure attachment; token masked as in ApiLoggingFilter
                .addFilter(new AllureRestAssured().configureHttpExchange(exchange -> exchange.redactHeader("Authorization")))
                .build();
    }
}
