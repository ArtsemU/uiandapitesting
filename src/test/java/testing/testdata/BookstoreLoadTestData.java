package testing.testdata;

import java.time.Duration;
import java.util.List;

public final class BookstoreLoadTestData {

    private BookstoreLoadTestData() {
    }

    // Load profile: deliberately tiny, demoQA is a shared public site.
    public static final int THREADS = 2;
    public static final Duration RAMP_UP = Duration.ofSeconds(10);
    public static final int ITERATIONS_PER_THREAD = 3;
    public static final Duration THINK_TIME_MIN = Duration.ofSeconds(1);
    public static final Duration THINK_TIME_MAX = Duration.ofSeconds(2);

    public static final String THREAD_GROUP_NAME = "Bookstore users";

    // Sampler names = BS-001 steps; they are the row labels in the report and the stats keys.
    public static final String STEP_CREATE_USER = "01 Create user";
    public static final String STEP_GENERATE_TOKEN = "02 Generate token";
    public static final String STEP_GET_NEW_USER = "03 Get user (new)";
    public static final String STEP_GET_CATALOGUE = "04 Get catalogue";
    public static final String STEP_ADD_BOOK = "05 Add first book";
    public static final String STEP_GET_USER_AFTER_ADD = "06 Get user (after add)";
    public static final String STEP_REMOVE_BOOK = "07 Remove book";
    public static final String STEP_GET_USER_AFTER_REMOVE = "08 Get user (after remove)";
    public static final String STEP_DELETE_USER = "09 Delete user";
    public static final String STEP_GET_DELETED_USER = "10 Get user (deleted)";

    public static final List<String> STEPS = List.of(
            STEP_CREATE_USER,
            STEP_GENERATE_TOKEN,
            STEP_GET_NEW_USER,
            STEP_GET_CATALOGUE,
            STEP_ADD_BOOK,
            STEP_GET_USER_AFTER_ADD,
            STEP_REMOVE_BOOK,
            STEP_GET_USER_AFTER_REMOVE,
            STEP_DELETE_USER,
            STEP_GET_DELETED_USER);
}
