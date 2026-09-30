package performance;

import api.Config;
import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;
import testing.testdata.BookstoreLoadTestData;
import testing.testdata.BookstoreTestData;
import us.abstracta.jmeter.javadsl.core.DslTestPlan;
import us.abstracta.jmeter.javadsl.core.TestPlanStats;
import us.abstracta.jmeter.javadsl.core.assertions.DslResponseAssertion;
import us.abstracta.jmeter.javadsl.core.assertions.DslResponseAssertion.TargetField;
import us.abstracta.jmeter.javadsl.core.stats.StatsSummary;
import us.abstracta.jmeter.javadsl.core.threadgroups.DslDefaultThreadGroup;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static us.abstracta.jmeter.javadsl.JmeterDsl.htmlReporter;
import static us.abstracta.jmeter.javadsl.JmeterDsl.httpCache;
import static us.abstracta.jmeter.javadsl.JmeterDsl.httpDefaults;
import static us.abstracta.jmeter.javadsl.JmeterDsl.httpSampler;
import static us.abstracta.jmeter.javadsl.JmeterDsl.jsonExtractor;
import static us.abstracta.jmeter.javadsl.JmeterDsl.responseAssertion;
import static us.abstracta.jmeter.javadsl.JmeterDsl.testPlan;
import static us.abstracta.jmeter.javadsl.JmeterDsl.threadGroup;
import static us.abstracta.jmeter.javadsl.JmeterDsl.uniformRandomTimer;

/**
 * Load-testing learning example: the BS-001 request flow as a JMeter DSL plan, run against
 * demoQA at a deliberately tiny load. The point is the mechanics, not the numbers.
 * Everything inside the plan is a stock JMeter element (no Java lambdas), so the saved
 * .jmx opens in the JMeter GUI.
 */
public class BookstoreLoadTest {
    private static final Logger log = LoggerFactory.getLogger(BookstoreLoadTest.class);

    private static final String OUTPUT_ROOT = "target/performance";
    private static final String HTML_REPORT_DIR = "html-report";
    private static final String JMX_FILE = "BS-001-load.jmx";

    private static final String USER_PATH = "/Account/v1/User";
    private static final String USER_BY_ID_PATH = USER_PATH + "/${userId}";
    private static final String TOKEN_PATH = "/Account/v1/GenerateToken";
    private static final String BOOK_PATH = "/BookStore/v1/Book";

    private static final String AUTHORIZATION = "Authorization";
    private static final String BEARER_TOKEN = "Bearer ${token}";

    // Unique per iteration: thread number + iteration index + time. The time is taken once,
    // by the first sampler of the iteration, and stored in a JMeter variable for the second.
    private static final String USER_NAME_PREFIX = "perf_t${__threadNum}_i${__jm__"
            + BookstoreLoadTestData.THREAD_GROUP_NAME + "__idx}_";
    private static final String NEW_USER_NAME = USER_NAME_PREFIX + "${__time(,iterationTime)}";
    private static final String USER_NAME = USER_NAME_PREFIX + "${iterationTime}";

    @Test(priority = 1, testName = "BS-001: Add book to user under light load", groups = {"performance"})
    public void addBookToUserLoadTest() throws IOException {
        Path runDir = Path.of(OUTPUT_ROOT, LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss")));
        Files.createDirectories(runDir);

        DslDefaultThreadGroup users = threadGroup(BookstoreLoadTestData.THREAD_GROUP_NAME)
                .rampTo(BookstoreLoadTestData.THREADS, BookstoreLoadTestData.RAMP_UP)
                .holdIterating(BookstoreLoadTestData.ITERATIONS_PER_THREAD)
                .children(
                        httpDefaults().url(Config.baseUrl()),
                        // The DSL adds an HTTP cache by default, and JMeter records no sample at all
                        // for a cache hit: the repeated "get user" steps would vanish from the results.
                        httpCache().disable(),
                        // Think time: in thread-group scope a timer runs before every sampler.
                        uniformRandomTimer(BookstoreLoadTestData.THINK_TIME_MIN, BookstoreLoadTestData.THINK_TIME_MAX),

                        httpSampler(BookstoreLoadTestData.STEP_CREATE_USER, USER_PATH)
                                .post(credentialsBody(NEW_USER_NAME), ContentType.APPLICATION_JSON)
                                .children(statusIs("201"), jsonExtractor("userId", "userID")),

                        httpSampler(BookstoreLoadTestData.STEP_GENERATE_TOKEN, TOKEN_PATH)
                                .post(credentialsBody(USER_NAME), ContentType.APPLICATION_JSON)
                                .children(statusIs("200"), jsonExtractor("token", "token")),

                        httpSampler(BookstoreLoadTestData.STEP_GET_NEW_USER, USER_BY_ID_PATH)
                                .header(AUTHORIZATION, BEARER_TOKEN)
                                .children(statusIs("200")),

                        httpSampler(BookstoreLoadTestData.STEP_GET_CATALOGUE, BookstoreTestData.CATALOGUE_PATH)
                                .children(statusIs("200"), jsonExtractor("isbn", "books[0].isbn")),

                        httpSampler(BookstoreLoadTestData.STEP_ADD_BOOK, BookstoreTestData.CATALOGUE_PATH)
                                .header(AUTHORIZATION, BEARER_TOKEN)
                                .post("{\"userId\": \"${userId}\", \"collectionOfIsbns\": [{\"isbn\": \"${isbn}\"}]}",
                                        ContentType.APPLICATION_JSON)
                                .children(statusIs("201")),

                        httpSampler(BookstoreLoadTestData.STEP_GET_USER_AFTER_ADD, USER_BY_ID_PATH)
                                .header(AUTHORIZATION, BEARER_TOKEN)
                                .children(statusIs("200")),

                        httpSampler(BookstoreLoadTestData.STEP_REMOVE_BOOK, BOOK_PATH)
                                .method("DELETE")
                                .header(AUTHORIZATION, BEARER_TOKEN)
                                .contentType(ContentType.APPLICATION_JSON)
                                .body("{\"isbn\": \"${isbn}\", \"userId\": \"${userId}\"}")
                                .children(statusIs("204")),

                        httpSampler(BookstoreLoadTestData.STEP_GET_USER_AFTER_REMOVE, USER_BY_ID_PATH)
                                .header(AUTHORIZATION, BEARER_TOKEN)
                                .children(statusIs("200")),

                        httpSampler(BookstoreLoadTestData.STEP_DELETE_USER, USER_BY_ID_PATH)
                                .method("DELETE")
                                .header(AUTHORIZATION, BEARER_TOKEN)
                                .children(statusIs("204")),

                        // The user is gone, so 401 is the expected answer. ignoreStatus() stops JMeter
                        // from failing the sample on a 4xx before the assertion has its say.
                        httpSampler(BookstoreLoadTestData.STEP_GET_DELETED_USER, USER_BY_ID_PATH)
                                .header(AUTHORIZATION, BEARER_TOKEN)
                                .children(statusIs("401").ignoreStatus())
                );

        // Saved without the HTML reporter: that listener is a DSL class the JMeter GUI does not have.
        DslTestPlan plan = testPlan(users);
        plan.saveAsJmx(runDir.resolve(JMX_FILE).toString());
        log.info("Test plan saved to {}", runDir.resolve(JMX_FILE).toAbsolutePath());

        TestPlanStats stats = testPlan(users, htmlReporter(runDir.toString(), HTML_REPORT_DIR)).run();
        log.info("HTML report: {}", runDir.resolve(HTML_REPORT_DIR).resolve("index.html").toAbsolutePath());

        log.info(String.format("%-28s %7s %6s %9s %7s %7s %9s", "sampler", "samples", "errors", "median ms", "p95 ms", "max ms", "req/s"));
        for (String step : BookstoreLoadTestData.STEPS) {
            logStats(step, stats.byLabel(step));
        }
        logStats("TOTAL", stats.overall());

        Assert.assertEquals(stats.overall().errorsCount(), 0L, "Load run produced failed samples");
    }

    private static String credentialsBody(String userName) {
        return "{\"userName\": \"" + userName + "\", \"password\": \"" + Config.userPassword() + "\"}";
    }

    private static DslResponseAssertion statusIs(String statusCode) {
        return responseAssertion().fieldToTest(TargetField.RESPONSE_CODE).equalsToStrings(statusCode);
    }

    private void logStats(String label, StatsSummary summary) {
        log.info(String.format("%-28s %7d %6d %9d %7d %7d %9.2f", label,
                summary.samplesCount(),
                summary.errorsCount(),
                summary.sampleTime().median().toMillis(),
                summary.sampleTime().perc95().toMillis(),
                summary.sampleTime().max().toMillis(),
                summary.samples().perSecond()));
    }
}
