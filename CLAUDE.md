# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code
in this repository.

## Project overview

A Java/Selenium UI test automation project. RestAssured is a dependency for future
API tests (hence the project name `uiandapitesting`), but no API tests exist yet.

Page Object Model suites currently cover three demoQA pages: Text Box, Check Box
and Web Tables.

Java 17, Maven, TestNG, Selenium 4, WebDriverManager, SLF4J + Log4j2.

## Build and test commands

- Compile: `mvn compile`
- Run tests: `mvn test` — runs the suite defined in `testng.xml` at the project
  root, wired through `maven-surefire-plugin`.
- Run a single class from the IDE: use the TestNG run configuration for the class.
- Run a single class from the CLI: `mvn test -Dtest=TextBoxTest`.

## Architecture

Three layers, driven strictly top-down: **Tests → Steps → Pages → BasePage**.
Tests call Steps only, never Pages directly.

### Layers

- `factory.WebDriverFactory` — creates the `WebDriver` instance. `Browser` enum
  (`CHROME`, `EDGE`, `SAFARI`) selects the driver; WebDriverManager resolves driver
  binaries automatically.
- `ui.pages.BasePage` — shared Selenium plumbing: `click`, `sendKeys`, `getText`,
  `isDisplayed`, `waitForPageLoad`, all built on an explicit `WebDriverWait` with a
  10-second timeout. Element interaction in page objects goes through these helpers
  rather than calling WebElement methods directly, so waits stay consistent.
- `ui.pages.*Page` — page objects. Hold `By` locators as private fields, expose
  action and getter methods. No assertions.
- `ui.steps.BaseSteps` — shared `openUrl(String)` helper.
- `ui.steps.*Steps` — compose page objects into business-level actions. This is the
  layer tests interact with. Step methods own SLF4J logging for key actions.
- `ui.models.*` — domain objects and their builders (e.g. `WebTableRecord`).
- `testing.ui.BaseUITest` — TestNG base class. `@BeforeMethod` creates a Chrome
  `WebDriver`, maximizes the window, and instantiates the relevant `*Steps` objects
  as fields. `@AfterMethod` does not quit the driver — `driver.quit()` is commented
  out on purpose so the browser stays open for inspection after a run.
  **Do not uncomment it.**
- `testing.ui.*Test` — test classes. Call `*Steps` methods only, hold all
  assertions. No helper methods: anything reusable belongs in the steps layer.

### Layer boundaries

- Objects built through a builder are passed whole. Never unpack them into
  positional parameters at a layer boundary.
- Type conversion between the domain type and the string form the DOM uses lives
  in the page layer only. Steps, tests and models never see the string form.

**UI only:** page objects return domain objects, not individual cell values,
when the caller needs more than one field.

**API only:** clients return the raw Response. They do not deserialise and do
not check status codes — tests assert on the status, and the steps layer
converts the body to a model.

### Package layout

`src/main/java`

- `factory`   — WebDriverFactory, Browser enum
- `ui.pages`  — page objects, extend BasePage
- `ui.steps`  — step layer, extends BaseSteps
- `ui.models` — domain objects and builders. Never constants.
  These live under `src/main/java` rather than `src/test/java` because `ui.pages`
  and `ui.steps` accept and return them directly, and main sources cannot compile
  against test-scope classes.

`src/test/java`

- `testing.ui`       — test classes, extend BaseUITest
- `testing.testdata` — constants only, one class per module, named
  `<Module>TestData`

Test data classes never live in the same package as the test classes.

### Adding a new page under test

1. `<Foo>Page` in `ui.pages`, extending `BasePage`.
2. `<Foo>Steps` in `ui.steps`, extending `BaseSteps`, wrapping the page and wired up
   in `BaseUITest`.
3. `<Foo>Record` or similar in `ui.models`, if the page has structured data.
4. `<Foo>Test` in `testing.ui`, extending `BaseUITest`.
5. Constants in `testing.testdata`, if the test needs fixed expected values.

## Test specifications

Test cases live in Confluence, space `Uiandapite`, under **Test Project**:

- **demoQA** — UI test specifications
- **API** — API test specifications

Read the relevant page before writing or changing a test. Do not invent scenarios
that are not in the specification — if a spec is missing, ambiguous or incomplete,
say so and ask rather than filling the gap.

Confluence is read-only. Never create, update or delete pages there. Documentation
is maintained manually.

## Conventions

### Locators

- Every locator uses `By.xpath()`. Nothing else — not `By.id`, not `By.className`,
  not `By.cssSelector`, not `By.name`, not `By.tagName`. Chosen for uniformity, see
  `docs/decisions/0001-xpath-for-all-locators.md`.
- Always scope locators to a container. demoQA reuses the same id in the form and in
  the output block, so an unscoped locator silently resolves to the wrong element.
- Positional indexes are allowed where no stable attribute identifies the element —
  table columns, nearest ancestor. Prefer an attribute when one exists: a positional
  locator does not break when the page changes, it silently starts matching
  something else.

### Test identifiers

- Every test carries `@Test(description = "XX-000: short description")`. Keep the
  description under roughly 60 characters so it stays readable on one line.
- `XX` identifies the **functional area**, not the technology. An area keeps its
  prefix whether it is exercised through the UI or the API.
- Current prefixes:
  - `TB` — Text Box
  - `CB` — Check Box
  - `WT` — Web Tables
  - `BS` — Books Store
- `000` is a three-digit number within that area.
- Test IDs are never reused, even after a test is deleted.
- Temporary or exploratory code is never committed. Delete it once it has served its
  purpose.

### Test data

- Test data is fixed and deterministic. No random values, no Faker. Where multiple
  records are needed, vary them with a counter.
- Constants holding values read from the page under test are named
  `EXPECTED_OUTPUT_<SCOPE>`. The name must make clear these are the application's
  internal values, not the labels shown in the UI.
- Timestamp-based usernames are an accepted exception to the "no random values"
  rule for API tests: the demoQA user registry is shared and global across all users of the site, 
  so a fixed username would eventually collide. 
  The value itself is never asserted on — only used to avoid collisions — so it does not compromise 
  determinism of the test's outcome.

### Assertions

- UI tests use `SoftAssert` for result checks, so one run reports every failed
  expectation instead of stopping at the first.
- Preconditions use a hard `Assert` regardless of layer. If the setup did not
  happen, the test must stop — continuing against a state that does not exist
  produces failures that point at the wrong thing.
- API tests use hard `Assert` throughout.
- Every assertion carries a failure message as the third argument. The message
  states which behaviour is broken, not the values — `assertEquals` already prints
  expected and actual.
- Test case pages follow a Preconditions / Steps / Expected result structure.
  Checks in the Preconditions block are assertions that must stop the test —
  use a hard Assert for those.

### Logging

Log4j2 config is at `src/test/resources/log4j2.xml` — console-only appender with a
colorized pattern; a commented-out file appender is available if file logging is
ever needed. Loggers are obtained per class via SLF4J
(`LoggerFactory.getLogger(...)`).

## Working agreements

### Git

- Never write to git: no branches, no commits, no stashing, no push, no pull
  requests. Branching and PRs are handled manually.
- Reading is fine and encouraged: `git status`, `git diff`, `git log`.

### Generated output

- Anything longer than a few lines goes to a file, not the chat — unless the prompt
  explicitly asks for the answer in the chat.
- One-off analysis, comparisons and audits go to `reports/` as `report_<topic>.md`,
  with the date it was produced on the first line. These are snapshots, not
  documentation — they go stale and are deleted once acted on.
- Documents meant to be kept live in `docs/`.

### This file

- Do not edit CLAUDE.md. If a rule is missing, wrong, or contradicts the code, say
  so and propose the wording — the change is made manually.

## Dependencies

- Never choose a dependency version from memory. Say which dependency is
  needed and why, and let me pin the version.
- Do not add a dependency without saying so explicitly in your summary.