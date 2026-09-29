# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code
in this repository.

## Project overview

A Java/Selenium UI and RestAssured API test automation project (hence the project
name `uiandapitesting`).

Page Object Model suites cover three demoQA pages: Text Box, Check Box and Web
Tables. API tests cover the demoQA Bookstore API (`BookApiTests`).

Java 17, Maven, TestNG, Selenium 4, WebDriverManager, SLF4J + Log4j2.

## Build and test commands

- Compile: `mvn compile`
- Run tests: `mvn test` — runs the suite defined in `testng.xml` at the project
  root, wired through `maven-surefire-plugin`.
- Run a single class from the IDE: use the TestNG run configuration for the class.
- Run a single class from the CLI: `mvn test -Dtest=TextBoxTest`.
- Suite XML files for scoped or parallel runs (sandbox, API-only, UI-only)
  live in `src/test/resources/suite/`. Run them directly via an IDE run
  configuration pointing at the file — they are not wired into `mvn test`,
  which still uses the root `testng.xml`.

## Architecture

Three layers, driven strictly top-down: **Tests → Steps → Pages → BasePage**.
Tests call Steps only, never Pages directly.

### Layers

- `factory.WebDriverFactory` — creates the `WebDriver` instance. `Browser` enum
  (`CHROME`, `EDGE`, `SAFARI`) selects the driver; WebDriverManager resolves driver
  binaries automatically. Headless mode and browser selection are controlled by
  the `headless` and `browser` JVM system properties (see CI/CD section below) —
  read directly by this code via `System.getProperty(...)`, unrelated to
  `pom.xml`'s `suiteXmlFile` property. Default behavior (no properties passed) is
  a visible Chrome window, unchanged from before these switches existed.
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
- `testing.ui.BaseUITest` — TestNG base class. `@BeforeMethod(alwaysRun = true)`
  creates a `WebDriver` via `WebDriverFactory`, maximizes the window (skipped when
  headless, except for Safari), and instantiates the relevant `*Steps` objects,
  each held in a `ThreadLocal` so parallel test methods don't share a browser
  instance. `@AfterMethod(alwaysRun = true)` quits the driver and clears each
  `ThreadLocal`. `alwaysRun = true` is required on both: group-filtered suites
  (e.g. `ui_smoke.xml`) would otherwise silently skip these configuration methods,
  since TestNG's group filter applies to `@BeforeMethod`/`@AfterMethod` too, not
  just to `@Test` methods.
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
not check status codes. Tests and glue assert on the status, then deserialise
with `response.as(Model.class)`.

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
- `testing.testdata` — constants only, one class per module (UI and API alike),
  named `<Module>TestData`

`src/test/java` also holds the API layer:

- `api`         — API clients, one per resource (e.g. `AccountClient`, `BookstoreClient`)
- `api.models`  — request/response models and builders
- `api.steps`   — `ApiSteps`, the shared API step layer used by both `api.tests` and `bdd`
- `api.tests`   — API test classes, named `<Area>ApiTests`
- `bdd` — Cucumber glue: runner, step definitions (`*StepDefs`), hooks and the
    scenario context. Feature files live in `src/test/resources/bdd/features/<area>/`,
    not under `src/test/java` — Maven does not copy non-Java files from there onto
    the test classpath.

Unlike `ui.models`, `api.models` lives under `src/test/java` rather than
`src/main/java` — API code has no main-source caller that needs to compile
against it.

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

- Every test carries `@Test(priority = N, testName = "XX-000: short description",
  groups = {"smoke"|"regression"})`. Keep the description part under roughly 60
  characters so it stays readable on one line. `priority` is the numeric part of
  the test ID (e.g. `BS-010` → `priority = 10`) — it orders execution within a
  functional area, not overall importance.
- `groups`: `priority == 1` → `groups = {"smoke"}`; every other priority →
  `groups = {"regression"}`. This applies per functional area, so each area has
  its own `smoke` test (`TB-001`, `CB-001`, `WT-001`, `BS-001`, ...), not one
  `smoke` test overall.
- `XX` identifies the **functional area**, not the technology. An area keeps its
  prefix whether it is exercised through the UI or the API.
- Current prefixes:
  - `TB` — Text Box
  - `CB` — Check Box
  - `WT` — Web Tables
  - `BS` — Books Store
- `000` is a three-digit number within that area.
- Test IDs are never reused, even after a test is deleted.

### Test data

- Test data is fixed and deterministic. No random values, no Faker. Where multiple
  records are needed, vary them with a counter.
- **UI only:** constants holding values read from the page under test are
  named `EXPECTED_OUTPUT_<SCOPE>`. The name must make clear these are the
  application's internal values, not the labels shown in the UI.
- Usernames built from the thread name plus a timestamp are an accepted exception
  to the "no random values" rule for API tests: the demoQA user registry is shared 
  and global across all users of the site, so a fixed username would eventually collide. 
  The value itself is never asserted on — only used to avoid collisions — so it does not
  compromise determinism of the test's outcome.

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

- Log4j2 config is at `src/test/resources/log4j2.xml` — console-only appender with a
  colorized pattern; a commented-out file appender is available if file logging is
  ever needed. Loggers are obtained per class via SLF4J
  (`LoggerFactory.getLogger(...)`).

### Parallel safety

- Suites run with parallel="methods": TestNG shares one test-class instance across
  threads. Mutable instance fields in test classes must be thread-confined
  (ThreadLocal, cleared in @AfterMethod(alwaysRun = true)). A thread-safe collection
  is not enough — it prevents corruption, not cross-test interference.

### BDD (Cucumber)

- Cucumber 7.x through `cucumber-testng`, with PicoContainer for dependency
  injection. All Cucumber artifacts take their version from `cucumber-bom` — one
  version to pin, never per-artifact versions.
- Scenarios duplicate existing test cases; they never replace TestNG tests or
  change their behaviour. Expected values shared by both are extracted to
  `testing.testdata` and used from both sides — no duplicated literals.
  Every scenario implements an existing Confluence test case — never invent one.
- Tags replace `priority` / `testName` / `groups`: each scenario carries its test
  case ID (`@BS-001`), `@smoke` or `@regression` by the same rule as TestNG groups
  (`XX-001` → `@smoke`, everything else → `@regression`), and `@api` or `@ui`.
- Gherkin is written in the third person ("the user") and declaratively: describe
  behaviour, not UI mechanics or HTTP calls. `When the user adds a book to their
  collection`, not `When the user clicks "Add"`. One `When` per scenario, except 
  end-to-end journey test cases (e.g. BS-001), which alternate 
  `When` / `Then` — one pair per action. Actions never go into `Then` steps.
- Step text is bound with Cucumber Expressions. Regular expressions only where
  they substantially simplify the binding.
- Step definitions call `*Steps` / `ApiSteps` only — no locators, no WebElement,
  no RestAssured calls. Missing behaviour is added
  to the steps layer, not to glue.
- Assertions only in `Then` steps, following the same rules as TestNG tests:
  API — hard `Assert`; UI — `SoftAssert`, where each `Then` step creates its own
  `SoftAssert` and calls `assertAll()` at the end of that step; preconditions in
  `Given` — hard `Assert`.
- State between steps is shared only through the PicoContainer-injected scenario
  context. No static fields in glue or hooks.
- Step definition classes are organised by domain concept (`UserStepDefs`,
  `CollectionStepDefs`), not by feature file. Before adding a step definition,
  search existing glue for one that matches or can be parameterised —
  near-duplicate phrasings are not allowed.
- API cleanup runs in an `@After("@api")` hook over the users recorded in the
  scenario context. A failed cleanup is logged and never fails the scenario.
- BDD runs through its own suite XML in `src/test/resources/suite/`, not through
  the root `testng.xml`.
- A scenario may cover a subset of its test case's checks; do not extend a
  scenario to match its spec unless asked.

## CI/CD

GitHub Actions workflows live under `.github/workflows/`:

- `ci-workflow.yml` — runs on every `push` and `pull_request`. Always runs
  `cicd_simple_commit.xml` (API) and `ui_smoke.xml` (UI, headless). PR events and
  pushes to `master` (i.e. a merge) additionally run `cicd_syntetic_tests.xml`
  (PR only — demo/sandbox suite) and `ui_regression.xml` (UI, headless, both PR
  and merge).
- `cd-simulate.yml` — manual (`workflow_dispatch`) simulated deploy pipeline:
  three sequential jobs (`deploy-qa1` → `deploy-stage` → `deploy-prod`), each
  scoped to a GitHub Environment (`qa1`, `stage`, `prod`) with its own protection
  rules (required reviewer, wait timer). The deploy step itself is a placeholder
  `echo` — there is no real deploy target, only the environment-gating mechanism
  is real.
- `nightly.yml` — `schedule` (cron) trigger, independent of any code event, plus
  `workflow_dispatch` for manual runs. Runs the full regression scope on a fixed
  schedule rather than tied to a push/PR/merge.

Headless mode and browser selection for UI suites are controlled via JVM system
properties, not `pom.xml`: `-Dheadless=true` (default `false`, visible browser)
and `-Dbrowser=CHROME|EDGE|SAFARI` (default `CHROME`). Any suite run against
`ubuntu-latest` (no display) must pass `-Dheadless=true`.

## Working agreements

### Git

- Never write to git: no branches, no commits, no stashing, no push, no pull
  requests. Branching and PRs are handled manually.
- Reading is fine and encouraged: `git status`, `git diff`, `git log`.

### Cleanup

- Clean up after yourself within a task: scratch files, draft
  implementations, or debug fragments created while iterating toward a
  solution are removed before the task is considered done.

### Decisions

- Decide small, reversible choices yourself — naming, where a constant lives,
  a literal-to-constant refactor, which of two equivalent structures to use.
  State each such choice in one line in your summary so it can be reviewed.
- Stop and ask only when: a spec and the code disagree; a dependency would be
  added or changed; a rule in this file would have to be broken; or the change
  is hard to undo.
- After changing code outside the task's own scope, run the affected existing
  suite and report the result.

### Sandbox package

- `src/test/java/sandbox` is a dedicated space for experiments and concept
  demonstrations, not for testing the application. Code here is not held
  to this file's conventions. Corresponding suite XML files live under
  `suite/`. Stays in the repo by design, not something to "clean up".

### Generated output

- Anything longer than a few lines goes to a file, not the chat — unless the prompt
  explicitly asks for the answer in the chat.
- One-off analysis, comparisons and audits go to `reports/` as
  `report_<topic>_<YYYY-MM-DD-HH-MM>.md`. These are snapshots, not
  documentation, but are kept rather than deleted — the timestamp in the
  filename lets multiple reviews of the same topic coexist and be compared.
- Documents meant to be kept live in `docs/`.

### This file

- Do not edit CLAUDE.md. If a rule is missing, wrong, or contradicts the code, say
  so and propose the wording — the change is made manually.

## Dependencies

- Never choose a dependency version from memory. Say which dependency is
  needed and why, and let me pin the version.
- Do not add a dependency without saying so explicitly in your summary.