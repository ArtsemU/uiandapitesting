# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project overview

A Java/Selenium UI test automation project. RestAssured is a dependency for future
API tests (hence the project name `uiandapitesting`), but no API tests exist yet.

Page Object Model suites currently cover three demoQA pages: Text Box, Check Box
and Web Tables.

- Java 17, Maven, TestNG, Selenium 4, WebDriverManager, SLF4J + Log4j2.

## Build & test commands

- Compile: `mvn compile`
- Run tests: `mvn test` — runs the suite defined in `testng.xml` at the project
  root, wired through `maven-surefire-plugin`.
- Run a single class from the IDE: use the TestNG run configuration for the
  class (e.g. `TextBoxTest`).
- Run a single class from the CLI: `mvn test -Dtest=TextBoxTest`.

## Architecture

Three-layer Selenium structure: **Steps → Pages → BasePage**, with tests driving
Steps only, never Pages directly.

- `factory.WebDriverFactory` — creates the `WebDriver` instance. `Browser` enum
  (`CHROME`, `EDGE`, `SAFARI`) selects the driver; WebDriverManager handles driver
  binaries automatically (Safari needs none, it is built into macOS).
- `ui.pages.BasePage` — base class for all page objects. Wraps common Selenium
  interactions (`click`, `sendKeys`, `getText`, `isDisplayed`, `waitForPageLoad`)
  with an explicit `WebDriverWait` (10s timeout). All element interaction in page
  objects goes through these helpers rather than calling WebElement methods
  directly, so waits stay consistent.
- `ui.pages.*Page` — page objects hold `By` locators as private fields and expose
  action and getter methods. No assertions live here.
- `ui.steps.BaseSteps` — base class for step objects; holds the shared
  `openUrl(String)` helper.
- `ui.steps.*Steps` — step objects compose one or more page objects into
  business-level actions used by tests. This is the layer tests interact with.
  Step methods own logging (via SLF4J) for key actions.
- `ui.models.*` — domain objects and their builders (e.g. `WebTableRecord`).
  Pages and steps accept and return these directly.
- `testing.ui.BaseUITest` — TestNG base test class. `@BeforeMethod` creates a
  Chrome `WebDriver`, maximizes the window, and instantiates the relevant `*Steps`
  objects as fields for subclasses. `@AfterMethod` does not quit the driver — `driver.quit()` is commented out
  on purpose so the browser stays open for inspection after a run.
  Do not uncomment it.
- Test classes under `src/test/java/testing/ui` extend `BaseUITest`, call only
  `*Steps` methods, and assert with TestNG `Assert` or `SoftAssert`.

### Layer boundaries

- Objects built through a builder are passed whole. Never unpack them into
  positional parameters at a layer boundary.
- Page objects return domain objects, not individual cell values, when the caller
  needs more than one field.
- Type conversion between the domain type and the string form the DOM uses lives
  in the page layer only. Steps, tests and models never see the string form.

### Adding a new page under test

1. `<Foo>Page` in `ui.pages`, extending `BasePage`.
2. `<Foo>Steps` in `ui.steps`, extending `BaseSteps`, wrapping the page and wired
   up in `BaseUITest`.
3. `<Foo>Record` or similar in `ui.models`, if the page has structured data.
4. `<Foo>Test` in `testing.ui`, extending `BaseUITest`.
5. Constants in `testing.testdata`, if the test needs fixed expected values.

## Logging

Log4j2 config is at `src/test/resources/log4j2.xml` — console-only appender with a
colorized pattern (a commented-out file appender is available if file logging is
ever needed). Loggers are obtained per-class via SLF4J
(`LoggerFactory.getLogger(...)`).

## Conventions

### Locators

- Every locator uses `By.xpath()`. Nothing else — not `By.id`, not `By.className`,
  not `By.cssSelector`, not `By.name`, not `By.tagName`. Chosen for uniformity,
  see `docs/decisions/0001-xpath-for-all-locators.md`.
- Always scope locators to a container. demoQA reuses the same id in the form and
  in the output block.
- Never use positional indexes: no `[1]`, no `(...)[2]`. If an element cannot be
  identified without position, say so rather than guessing — a positional locator
  does not stop working, it silently starts matching a different element.

### Tests

- Every test carries `@Test(description = "XX-000: short description")`.
  XX is a two-letter module prefix: TB = Text Box, CB = Check Box,
  WT = Web Tables. 000 is a three-digit number within that module.
  Keep the description under ~60 characters so it stays readable on one line.
- Test IDs are never reused, even after a test is deleted.
- Temporary or exploratory code is never committed. Delete it once it has served
  its purpose.

### Test data

- Test data is fixed and deterministic. No random values, no Faker. Where multiple
  records are needed, vary them with a counter.
- Constants holding values read from the page under test are named
  `EXPECTED_OUTPUT_<SCOPE>`. The name must make clear these are the application's
  internal values, not the labels shown in the UI.

### Assertions

- Every assertion carries a failure message as the third argument. The message
  states which behaviour is broken, not the values — `assertEquals` already prints
  expected and actual.

## Package layout

`src/main/java`

- `factory`    — WebDriverFactory, Browser enum
- `ui.pages`   — page objects, extend BasePage
- `ui.steps`   — step layer, extends BaseSteps
- `ui.models`  — domain objects and builders. Never constants.
  These live under `src/main/java` rather than `src/test/java` because
  `ui.pages` and `ui.steps` accept and return them directly, and main sources
  cannot compile against test-scope classes.

`src/test/java`

- `testing.ui`       — test classes, extend BaseUITest
- `testing.testdata` — constants only, one class per module, named
  `<Module>TestData`

Test data classes never live in the same package as the test classes.

## Git

- Never write to git: no branches, no commits, no stashing, no push, no pull
  requests. Branching and PRs are handled manually.
- Reading is fine and encouraged: `git status`, `git diff`, `git log`.

## This file

- Do not edit CLAUDE.md. If a rule is missing, wrong, or contradicts the code,
  say so and propose the wording — the change is made manually.

## Generated output

- Anything longer than a few lines goes to a file, not the chat — unless the
  prompt explicitly asks for the answer in the chat.
- One-off analysis, comparisons and audits go to `reports/` as
  `report_<topic>.md`, with the date it was produced on the first line.
  These are snapshots, not documentation — they go stale and are deleted
  once acted on.
- Documents meant to be kept live in `docs/`.