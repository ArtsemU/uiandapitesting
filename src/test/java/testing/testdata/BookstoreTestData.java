package testing.testdata;

public final class BookstoreTestData {

    private BookstoreTestData() {
    }

    public static final String USER_NOT_FOUND_MESSAGE = "User not found!";

    public static final String BOOKS_SCHEMA = "schemas/books-schema.json";

    public static final String USER_SCHEMA = "schemas/user-schema.json";

    public static final String PASSWORD_RULES_MESSAGE =
            "Passwords must have at least one non alphanumeric character, one digit ('0'-'9'), one uppercase ('A'-'Z'), one lowercase ('a'-'z'), one special character and Password must be eight characters or longer.";
}
