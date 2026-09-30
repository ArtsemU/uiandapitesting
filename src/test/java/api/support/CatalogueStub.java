package api.support;

import api.Config;
import api.steps.ApiSteps;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import io.restassured.builder.ResponseBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import testing.testdata.BookstoreTestData;

/**
 * Learning examples: two ways to stub the catalogue call. Both emulate
 * GET /BookStore/v1/Books being unavailable; every other call still goes to the
 * real demoQA. The catalogue only supplies an ISBN to the tests that use it, so
 * a stub does not change what those tests verify.
 *
 * <ul>
 *   <li>{@link #wireMock()} — HTTP-level stub: a local WireMock server serves one
 *       book (fake fields, real ISBN, so the book can be added to a real user).
 *       Switched by {@code stub.catalogue.wiremock}.</li>
 *   <li>{@link #mockito()} — Java-level stub: a Mockito spy of ApiSteps returns
 *       one fully fake book from getAllBooks(). Switched by
 *       {@code stub.catalogue.mockito}.</li>
 * </ul>
 *
 * With its flag off, a factory returns a handle around a plain ApiSteps: real
 * catalogue, no server, and {@link #verifyStubUsed()} / {@link #close()} do nothing.
 *
 * One handle per test, used in try-with-resources. The class holds no mutable
 * static state and handles share nothing, so they are safe under parallel="methods".
 */
public final class CatalogueStub implements AutoCloseable {
    private static final Logger log = LoggerFactory.getLogger(CatalogueStub.class);

    private final ApiSteps apiSteps;
    private final Runnable verification;
    private final Runnable shutdown;

    private CatalogueStub(ApiSteps apiSteps, Runnable verification, Runnable shutdown) {
        this.apiSteps = apiSteps;
        this.verification = verification;
        this.shutdown = shutdown;
    }

    public static CatalogueStub wireMock() {
        if (!Config.stubCatalogueWireMock()) {
            return real();
        }

        WireMockServer server = new WireMockServer(WireMockConfiguration.options().dynamicPort());
        server.start();
        try {
            server.stubFor(WireMock.get(WireMock.urlPathEqualTo(BookstoreTestData.CATALOGUE_PATH))
                    .willReturn(WireMock.okJson(BookstoreTestData.STUBBED_CATALOGUE_REAL_ISBN_BODY)));
        } catch (RuntimeException e) {
            server.stop();
            throw e;
        }
        log.info("Catalogue is stubbed with WireMock at {}", server.baseUrl());

        return new CatalogueStub(
                ApiSteps.withCatalogueBaseUrl(server.baseUrl()),
                () -> server.verify(1, WireMock.getRequestedFor(WireMock.urlPathEqualTo(BookstoreTestData.CATALOGUE_PATH))),
                server::stop);
    }

    public static CatalogueStub mockito() {
        if (!Config.stubCatalogueMockito()) {
            return real();
        }

        ApiSteps spy = Mockito.spy(new ApiSteps());
        Response stubbedCatalogue = new ResponseBuilder()
                .setStatusCode(200)
                .setContentType(ContentType.JSON)
                .setBody(BookstoreTestData.STUBBED_CATALOGUE_BODY)
                .build();
        // doReturn(...).when(spy), not when(spy.getAllBooks()): the latter would call the real endpoint.
        Mockito.doReturn(stubbedCatalogue).when(spy).getAllBooks();
        log.info("Catalogue is stubbed with a Mockito spy");

        return new CatalogueStub(spy, () -> Mockito.verify(spy).getAllBooks(), () -> { });
    }

    private static CatalogueStub real() {
        return new CatalogueStub(new ApiSteps(), () -> { }, () -> { });
    }

    public ApiSteps apiSteps() {
        return apiSteps;
    }

    /** Fails if the stub is on and the catalogue was not requested from it exactly once. */
    public void verifyStubUsed() {
        verification.run();
    }

    @Override
    public void close() {
        shutdown.run();
    }
}
