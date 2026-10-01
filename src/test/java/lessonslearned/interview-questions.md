# Interview questions — Java / Selenium / RestAssured

Questions only. Answers come later, one section at a time.
Numbering is stable — refer to questions by number (e.g. J-12, S-7, R-3).

---

## Java

### OOP basics

- **J-1.** What are the four pillars of OOP? Give an example of each from a test framework.
- **J-2.** What is the difference between overloading and overriding?
- **J-3.** What is polymorphism? Show it with `WebDriver` and `ChromeDriver`.
- **J-4.** Composition vs inheritance — when do you prefer which? Which one does a Page Object use for `BasePage`, and which for page components?
- **J-5.** What do access modifiers (`private`, package-private, `protected`, `public`) control? Which do you use for locators in a page object, and why?

### Interfaces and abstract classes

- **J-6.** What is an interface? Why "program to an interface, not an implementation"?
- **J-7.** Interface vs abstract class — differences, and when to choose each.
- **J-8.** Can an interface have method bodies? What are `default` and `static` methods in interfaces, and why were they added?
- **J-9.** Can a class implement several interfaces? What happens if two of them have the same `default` method?
- **J-10.** What is a functional interface? Name some from the JDK (`Runnable`, `Supplier`, `Function`, `Predicate`, `Consumer`).
- **J-11.** Which interfaces have you implemented in a test framework, and why? (`ITestListener`, `IRetryAnalyzer`, `AutoCloseable`, ...)
- **J-12.** When would you design your own interface in a test framework? Give an example.
- **J-13.** What is a marker interface? Is it still used?

### `static` and `final`

- **J-14.** What does `static` mean for a field, a method, a nested class, a block?
- **J-15.** Why can't a static method access instance fields?
- **J-16.** Why is a static `WebDriver` field a problem in parallel runs? How do you fix it?
- **J-17.** What does `final` mean for a variable, a method, a class?
- **J-18.** Is a `final` list immutable? What is the difference between a final reference and an immutable object?
- **J-19.** Why is `String` immutable, and what does that give you?

### Exceptions

- **J-20.** What is the exception hierarchy in Java (`Throwable`, `Error`, `Exception`, `RuntimeException`)?
- **J-21.** Checked vs unchecked exceptions — difference, and when to use each.
- **J-22.** Why do test frameworks mostly use unchecked exceptions?
- **J-23.** How do you create your own exception? When is it worth doing in a test framework? Give an example.
- **J-24.** What is exception chaining (`cause`)? Why must you pass the original exception when wrapping it?
- **J-25.** `throw` vs `throws` — what is the difference?
- **J-26.** How does `finally` work? When does it *not* run?
- **J-27.** What happens if you `return` from `finally`? What if `finally` throws?
- **J-28.** What is try-with-resources? What is `AutoCloseable`? What are suppressed exceptions?
- **J-29.** Can you catch several exceptions in one `catch`? In what order do you write `catch` blocks, and why?
- **J-30.** When is it acceptable to catch an exception and not rethrow it? What is wrong with an empty `catch`?
- **J-31.** `Exception` vs `Error` — should you ever catch `Error` (e.g. `OutOfMemoryError`, `AssertionError`)?
- **J-32.** Is a failed TestNG assertion an exception? Which one, and what does that mean for `try/catch` around assertions?

### Collections and generics

- **J-33.** `List` vs `Set` vs `Map` — differences and typical use.
- **J-34.** `ArrayList` vs `LinkedList`; `HashMap` vs `LinkedHashMap` vs `TreeMap`.
- **J-35.** How does `HashMap` work internally? What is the contract between `equals` and `hashCode`, and what breaks if it is violated?
- **J-36.** `HashMap` vs `ConcurrentHashMap` vs `Collections.synchronizedMap`.
- **J-37.** What are generics for? What is type erasure? What does `<? extends T>` vs `<? super T>` mean?
- **J-38.** What is `ConcurrentModificationException`, and how do you avoid it?

### Java 8+

- **J-39.** What is a lambda? How is it related to functional interfaces?
- **J-40.** Streams: intermediate vs terminal operations; `map` vs `flatMap`; `filter`, `collect`, `reduce`.
- **J-41.** What is `Optional`, and when should (and shouldn't) you use it?
- **J-42.** What is a record, and where would you use it in a test framework?

### Memory and concurrency

- **J-43.** Stack vs heap — what lives where?
- **J-44.** How does garbage collection work, at a high level?
- **J-45.** `ThreadLocal` — what is it, where do you use it in test automation, and why must you call `remove()`?
- **J-46.** `volatile` vs `synchronized` vs atomic classes — what does each guarantee?
- **J-47.** What is a race condition? Give an example you have met in tests.

### Misc

- **J-48.** `==` vs `equals()` for objects and strings.
- **J-49.** `String` vs `StringBuilder` vs `StringBuffer`.
- **J-50.** Pass-by-value in Java — what actually gets passed when you pass an object?

---

## Selenium

### Architecture

- **S-1.** How does Selenium WebDriver work under the hood? What is the W3C WebDriver protocol? What changes with WebDriver BiDi?
- **S-2.** What is `WebDriver` (interface) vs `ChromeDriver` / `RemoteWebDriver` (implementations)?
- **S-3.** What is Selenium Grid? When do you need it?
- **S-4.** What does WebDriverManager (or Selenium Manager) do?
- **S-5.** Headless mode — what is different, and what problems can it cause?

### Locators

- **S-6.** What locator strategies exist? Which do you prefer, and why?
- **S-7.** XPath vs CSS selectors — pros and cons.
- **S-8.** Absolute vs relative XPath. Useful XPath axes (`ancestor`, `following-sibling`, ...).
- **S-9.** What makes a locator stable or brittle?
- **S-10.** `findElement` vs `findElements` — behaviour when nothing is found.

### Waits

- **S-11.** Implicit vs explicit vs fluent wait — differences.
- **S-12.** Why should you not mix implicit and explicit waits?
- **S-13.** Which `ExpectedConditions` do you use most, and why?
- **S-14.** Why is `Thread.sleep` bad? Is it ever acceptable?

### Exceptions and failures

- **S-15.** `NoSuchElementException` vs `TimeoutException` vs `StaleElementReferenceException` vs `ElementNotInteractableException` vs `ElementClickInterceptedException` — when does each happen?
- **S-16.** How do you handle a `StaleElementReferenceException`?
- **S-17.** A test passes locally but fails in CI with "element not found". How do you investigate? List the likely causes.
- **S-18.** How do you collect evidence on failure (screenshot, page source, browser logs)? Where does that code live?
- **S-19.** What is a flaky test? Common causes in UI tests and how you deal with them.

### Design patterns

- **S-20.** What is the Page Object Model? What belongs in a page object, and what does not?
- **S-21.** Page Object Model vs Page Factory (`@FindBy`, `PageFactory.initElements`) — what is the difference? Why is Page Factory discouraged?
- **S-22.** What is the Factory pattern, and how is it used for driver creation? How is it different from Page Factory?
- **S-23.** What is a Steps / business layer above page objects, and why add it?
- **S-24.** Where do assertions belong — page objects or tests? Why?
- **S-25.** What is the Singleton pattern? Is a singleton `WebDriver` a good idea?

### Interactions

- **S-26.** `Actions` class — what is it for (hover, drag-and-drop, key combinations)?
- **S-27.** How do you work with frames, windows / tabs, and alerts?
- **S-28.** `JavascriptExecutor` — when do you use it, and why is it a last resort?
- **S-29.** How do you work with dropdowns (`Select` vs custom dropdowns)?
- **S-30.** How do you upload a file? How do you deal with a download?

### Framework and parallelism

- **S-31.** How do you run UI tests in parallel safely? Where does the driver live?
- **S-32.** How do you choose the browser and headless mode at runtime?
- **S-33.** How do you manage test data for UI tests?
- **S-34.** Soft vs hard assertions in UI tests — when to use each.
- **S-35.** How do you integrate UI tests into CI?

---

## RestAssured / API testing

### Basics

- **R-1.** What is RestAssured, and what does the `given()` / `when()` / `then()` structure mean?
- **R-2.** HTTP methods — what are they for? Which are idempotent? Which are safe?
- **R-3.** Main HTTP status code classes (2xx, 3xx, 4xx, 5xx), and the codes you check most often.
- **R-4.** Path parameters vs query parameters vs request body vs headers.
- **R-5.** What is the difference between 401 and 403? Between 400 and 422?

### Requests and responses

- **R-6.** How do you send a JSON body — string, `Map`, POJO? Pros and cons.
- **R-7.** How do you extract a value from a response (`jsonPath`, `extract().path(...)`, `as(...)`)?
- **R-8.** Serialization and deserialization — how does RestAssured do it? What do Jackson annotations (`@JsonProperty`, `@JsonIgnoreProperties`) do?
- **R-9.** What happens on deserialization when the response has an extra field? A missing field? A wrong type?
- **R-10.** How do you validate a response: status, headers, body fields, response time?

### Reuse and structure

- **R-11.** What are `RequestSpecification` and `ResponseSpecification`? What are `RequestSpecBuilder` and `ResponseSpecBuilder` for?
- **R-12.** What are filters in RestAssured? Give examples (logging, auth, custom).
- **R-13.** How do you log requests and responses — always, or only on failure?
- **R-14.** How do you structure an API test framework (clients, models, steps, tests)? Where do assertions live?
- **R-15.** How do you manage base URLs and environments?

### Authentication

- **R-16.** Basic auth vs bearer token vs OAuth 2.0 vs API key — how do you handle each in RestAssured?
- **R-17.** How do you get a token once and reuse it across tests? What are the risks?

### Advanced validation

- **R-18.** What is JSON Schema validation? What does it catch that field assertions don't? What are its limits?
- **R-19.** What is contract testing? How is it different from schema validation?
- **R-20.** How do you test an endpoint that depends on another service that is not available (stubs, mocks)?

### Test design

- **R-21.** What would you test for a "create user" endpoint? Positive, negative, boundary cases.
- **R-22.** How do you make API tests independent of each other? How do you clean up test data?
- **R-23.** How do you run API tests in parallel safely?
- **R-24.** API tests vs UI tests — what belongs where in the test pyramid?
- **R-25.** A test is green locally and intermittently fails in CI with a 401. How do you investigate?
