package performance;

import api.Config;
import io.gatling.javaapi.core.ChainBuilder;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;
import io.gatling.javaapi.http.HttpRequestActionBuilder;
import testing.testdata.BookstoreLoadTestData;
import testing.testdata.BookstoreTestData;

import static io.gatling.javaapi.core.CoreDsl.StringBody;
import static io.gatling.javaapi.core.CoreDsl.exec;
import static io.gatling.javaapi.core.CoreDsl.global;
import static io.gatling.javaapi.core.CoreDsl.jsonPath;
import static io.gatling.javaapi.core.CoreDsl.pause;
import static io.gatling.javaapi.core.CoreDsl.rampUsers;
import static io.gatling.javaapi.core.CoreDsl.scenario;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

/**
 * Load-testing learning example, Gatling edition: the same BS-001 request flow and load profile
 * as {@link BookstoreLoadTest} (JMeter DSL), so the two tools can be compared side by side.
 * Request names are the JMeter sampler names, so the two reports line up row by row.
 * Not a TestNG test: runs only through `mvn test-compile gatling:test`.
 */
public class BookstoreGatlingSimulation extends Simulation {

    private static final String USER_PATH = "/Account/v1/User";
    private static final String USER_BY_ID_PATH = USER_PATH + "/#{userId}";
    private static final String TOKEN_PATH = "/Account/v1/GenerateToken";
    private static final String BOOK_PATH = "/BookStore/v1/Book";

    private static final String AUTHORIZATION = "Authorization";
    private static final String BEARER_TOKEN = "Bearer #{token}";

    private static final String ITERATION = "iteration";
    private static final String CREDENTIALS_BODY =
            "{\"userName\": \"#{userName}\", \"password\": \"" + Config.userPassword() + "\"}";

    private final HttpProtocolBuilder httpProtocol = http
            .baseUrl(Config.baseUrl())
            // Same reason as in the JMeter test: a cache hit would not reach the server, so the
            // repeated "get user" steps would measure nothing.
            .disableCaching()
            // Gatling otherwise sends a warm-up request to gatling.io before the run.
            .disableWarmUp();

    // Unique per iteration: virtual-user id + iteration index + time, set once at the start of
    // the iteration and read by the create-user and generate-token requests.
    private final ChainBuilder bs001Flow = exec(session -> session.set("userName",
                    "perf_g_u" + session.userId() + "_i" + session.getInt(ITERATION) + "_" + System.currentTimeMillis()))
            .exec(thinkThen(http(BookstoreLoadTestData.STEP_CREATE_USER)
                    .post(USER_PATH)
                    .body(StringBody(CREDENTIALS_BODY)).asJson()
                    .check(status().is(201), jsonPath("$.userID").saveAs("userId"))))
            .exec(thinkThen(http(BookstoreLoadTestData.STEP_GENERATE_TOKEN)
                    .post(TOKEN_PATH)
                    .body(StringBody(CREDENTIALS_BODY)).asJson()
                    .check(status().is(200), jsonPath("$.token").saveAs("token"))))
            .exec(thinkThen(http(BookstoreLoadTestData.STEP_GET_NEW_USER)
                    .get(USER_BY_ID_PATH)
                    .header(AUTHORIZATION, BEARER_TOKEN)
                    .check(status().is(200))))
            .exec(thinkThen(http(BookstoreLoadTestData.STEP_GET_CATALOGUE)
                    .get(BookstoreTestData.CATALOGUE_PATH)
                    .check(status().is(200), jsonPath("$.books[0].isbn").saveAs("isbn"))))
            .exec(thinkThen(http(BookstoreLoadTestData.STEP_ADD_BOOK)
                    .post(BookstoreTestData.CATALOGUE_PATH)
                    .header(AUTHORIZATION, BEARER_TOKEN)
                    .body(StringBody("{\"userId\": \"#{userId}\", \"collectionOfIsbns\": [{\"isbn\": \"#{isbn}\"}]}")).asJson()
                    .check(status().is(201))))
            .exec(thinkThen(http(BookstoreLoadTestData.STEP_GET_USER_AFTER_ADD)
                    .get(USER_BY_ID_PATH)
                    .header(AUTHORIZATION, BEARER_TOKEN)
                    .check(status().is(200))))
            .exec(thinkThen(http(BookstoreLoadTestData.STEP_REMOVE_BOOK)
                    .delete(BOOK_PATH)
                    .header(AUTHORIZATION, BEARER_TOKEN)
                    .body(StringBody("{\"isbn\": \"#{isbn}\", \"userId\": \"#{userId}\"}")).asJson()
                    .check(status().is(204))))
            .exec(thinkThen(http(BookstoreLoadTestData.STEP_GET_USER_AFTER_REMOVE)
                    .get(USER_BY_ID_PATH)
                    .header(AUTHORIZATION, BEARER_TOKEN)
                    .check(status().is(200))))
            .exec(thinkThen(http(BookstoreLoadTestData.STEP_DELETE_USER)
                    .delete(USER_BY_ID_PATH)
                    .header(AUTHORIZATION, BEARER_TOKEN)
                    .check(status().is(204))))
            // The user is gone, so 401 is the expected answer. An explicit status check replaces
            // Gatling's default "2xx or 304" check, so the 401 counts as OK.
            .exec(thinkThen(http(BookstoreLoadTestData.STEP_GET_DELETED_USER)
                    .get(USER_BY_ID_PATH)
                    .header(AUTHORIZATION, BEARER_TOKEN)
                    .check(status().is(401))));

    private final ScenarioBuilder users = scenario(BookstoreLoadTestData.THREAD_GROUP_NAME)
            .repeat(BookstoreLoadTestData.ITERATIONS_PER_THREAD, ITERATION).on(bs001Flow);

    {
        // Open vs closed workload model.
        // Open (Gatling's default): users *arrive* at a rate you set, each runs the scenario once
        // and leaves. Arrivals do not wait for the system — if it slows down, users pile up and
        // concurrency grows. That is how public traffic behaves.
        // Closed (JMeter thread group): a fixed number of users, each starting its next request
        // only after the previous response. If the system slows down, the load it receives drops.
        // Gatling's own closed-model steps (constantConcurrentUsers / rampConcurrentUsers) hold a
        // concurrency level for a *duration*, replacing each finished user with a new one — they
        // cannot express "3 iterations per user". So the JMeter profile is reproduced as:
        // 2 long-lived users started 5 s apart (rampUsers over 10 s, like JMeter's ramp-up), each
        // looping the flow 3 times (repeat). No new users ever arrive: closed-loop in effect,
        // even though rampUsers is formally an open-model injection step.
        setUp(users.injectOpen(rampUsers(BookstoreLoadTestData.THREADS).during(BookstoreLoadTestData.RAMP_UP)))
                .protocols(httpProtocol)
                .assertions(global().failedRequests().count().is(0L));
    }

    // Think time before every request, as the thread-group-scoped timer does in the JMeter test.
    // pause(min, max) draws uniformly between the two bounds.
    private static ChainBuilder thinkThen(HttpRequestActionBuilder request) {
        return pause(BookstoreLoadTestData.THINK_TIME_MIN, BookstoreLoadTestData.THINK_TIME_MAX).exec(request);
    }
}
