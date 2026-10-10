package testing.testdata;

public final class BookstoreTestData {

    private BookstoreTestData() {
    }

    public static final String USER_NOT_FOUND_MESSAGE = "User not found!";

    public static final String BOOKS_SCHEMA = "schemas/books-schema.json";

    public static final String USER_SCHEMA = "schemas/user-schema.json";

    public static final String CATALOGUE_PATH = "/BookStore/v1/Books";

    // Fake single-book catalogue returned in place of GET /BookStore/v1/Books.
    // Same fields and types as the real response; none of the values exist in demoQA.
    public static final String STUBBED_CATALOGUE_BODY = """
            {
              "books": [
                {
                  "isbn": "0000000000000",
                  "title": "Stubbed Book Title",
                  "subTitle": "Stubbed Book Subtitle",
                  "author": "Stubbed Author",
                  "publish_date": "2000-01-01T00:00:00.000Z",
                  "publisher": "Stubbed Publisher",
                  "pages": 100,
                  "description": "Stubbed book description",
                  "website": "http://stubbed.example.com"
                }
              ]
            }
            """;

    // Single-book catalogue returned in place of GET /BookStore/v1/Books when the book is
    // later added to a real user: the ISBN is a real demoQA catalogue ISBN, every other
    // value is fake.
    public static final String STUBBED_CATALOGUE_REAL_ISBN_BODY = """
            {
              "books": [
                {
                  "isbn": "9781449325862",
                  "title": "Stubbed Book Title",
                  "subTitle": "Stubbed Book Subtitle",
                  "author": "Stubbed Author",
                  "publish_date": "2000-01-01T00:00:00.000Z",
                  "publisher": "Stubbed Publisher",
                  "pages": 100,
                  "description": "Stubbed book description",
                  "website": "http://stubbed.example.com"
                }
              ]
            }
            """;

    public static final String PASSWORD_RULES_CODE = "1300";

    public static final String PASSWORD_RULES_MESSAGE =
            "Passwords must have at least one non alphanumeric character, one digit ('0'-'9'), one uppercase ('A'-'Z'), one lowercase ('a'-'z'), one special character and Password must be eight characters or longer.";
}
