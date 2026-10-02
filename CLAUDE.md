# CLAUDE.md

Guidance for Claude Code when working in this repository.

## Project overview

Java/Selenium UI and RestAssured API test automation against demoQA: Page Object
suites for Text Box, Check Box and Web Tables; API tests for the Bookstore API
(`BookApiTests`). Java 17, Maven, TestNG, Selenium 4, SLF4J + Log4j2.

## Build and test commands

- `mvn test` runs the suite in the root `testng.xml`.
- Scoped and parallel suites live in `src/test/resources/suite/` and are not
  wired into `mvn test`; run one with `-DsuiteXmlFile=<path>`.
- Every key in `api.properties` can be overridden with a JVM system property of
  the same name (`-Dkey=value`); a blank value counts as not set. Boolean keys
  accept only `true` or `false`.
- UI runs take `-Dheadless=true|false` (default false) and
  `-Dbrowser=CHROME|EDGE|SAFARI` (default CHROME), read directly by
  `WebDriverFactory`, not by `pom.xml`. Any UI suite on CI (no display) must pass
  `-Dheadless=true`.

## Architecture

Three layers, driven strictly top-down: **Tests → Steps → Pages → BasePage**.
Tests call Steps only, never Pages directly.

- Pages hold `By` locators as private fields and expose actions and getters. No
  assertions. Element interaction goes through the `BasePage` helpers, not
  `WebElement` methods directly, so waits stay consistent.
- Steps compose page objects into business-level actions and own SLF4J logging.
  No assertions, no `By`, `WebElement` or driver access.
- Tests call Steps only and hold all assertions. No helper methods: anything
  reusable belongs in the steps layer.
- Test configuration methods (`@BeforeMethod` / `@AfterMethod`) carry
  `alwaysRun = true`: group-filtered suites skip them otherwise, and for cleanup
  that failure is silent.

### Layer boundaries

- Objects built through a builder are passed whole. Never unpack them into
  positional parameters at a layer boundary.
- Type conversion between the domain type and the string form the DOM uses lives
  in the page layer only.
- **UI only:** page objects return domain objects, not individual cell values,
  when the caller needs more than one field.
- **API only:** clients return the raw Response. They do not deserialise and do
  not check status codes. Tests and glue assert on the status, then deserialise
  with `response.as(Model.class)`.

### Where things go

`src/main/java`:
- `factory`, `ui.pages`, `ui.steps`, `ui.models`. `ui.models` (domain objects and
  builders, never constants) is in main sources because pages and steps accept
  and return them, and main sources cannot compile against test classes.

`src/test/java`:
- `testing.ui` — UI test classes, extend `BaseUITest`.
- `testing.testdata` — constants only, one class per module (UI and API alike),
  named `<Module>TestData`. Never in the same package as the tests.
- `api` — clients, one per resource; `api.models` — request/response models;
  `api.steps` — `ApiSteps`, shared by `api.tests` and `bdd`; `api.tests` —
  `<Area>ApiTests`; `api.support` — test support that is neither a client, a
  model nor a step (e.g. `CatalogueStub`).
- `bdd` — Cucumber runner, step definitions (`*StepDefs`), hooks, scenario
  context.
- `performance` — load-test examples (JMeter DSL, Gatling). Gatling runs only
  through `mvn gatling:test`, never in the normal test lifecycle.

`src/test/resources`:
- `bdd/features/<area>/` — feature files. Not under `src/test/java`: Maven does
  not copy non-Java files from there onto the test classpath.
- `schemas/` — JSON Schemas for API responses (`<resource>-schema.json`).

## Test specifications

Test cases live in Confluence, space `Uiandapite`, under **Test Project**:
**demoQA** (UI) and **API**. Read the relevant page before writing or changing a
test. Do not invent scenarios — if a spec is missing, ambiguous or incomplete,
say so and ask.

Confluence is read-only. Never create, update or delete pages there.

## Conventions

### Locators

- Every locator uses `By.xpath()` — nothing else. See
  `docs/decisions/0001-xpath-for-all-locators.md`.
- Always scope locators to a container. demoQA reuses the same id in the form and
  in the output block, so an unscoped locator silently resolves to the wrong
  element.
- Positional indexes only where no stable attribute exists (table columns,
  nearest ancestor). A positional locator does not break when the page changes —
  it silently starts matching something else.

### Test identifiers

- Every test carries `@Test(priority = N, testName = "XX-000: short description",
  groups = {"smoke"|"regression"})`, description under ~60 characters.
- `priority` is the numeric part of the ID (`BS-010` → `10`).
  `priority == 1` → `smoke`, everything else → `regression`, per functional area.
- `XX` is the functional area, not the technology: `TB` Text Box, `CB` Check
  Box, `WT` Web Tables, `BS` Books Store. `000` is a three-digit number.
- Test IDs are never reused, even after a test is deleted.

### Test data

- Fixed and deterministic. No random values, no Faker; vary multiple records with
  a counter.
- **UI only:** constants holding values read from the page under test are named
  `EXPECTED_OUTPUT_<SCOPE>` — the application's internal values, not UI labels.
- Exception: API usernames are built from the thread name plus a timestamp. The
  demoQA user registry is shared and global, so a fixed name would collide; the
  thread name is what keeps parallel threads apart. The value is never asserted
  on.

### Assertions

- UI tests use `SoftAssert` for result checks. API tests use hard `Assert`
  throughout. Preconditions use hard `Assert` in every layer — if setup did not
  happen, the test must stop.
- Every assertion carries a failure message stating which behaviour is broken,
  not the values.
- API schema checks use draft-04 JSON Schema only — the RestAssured validator
  silently ignores keywords from newer drafts. Order per response: status, then
  schema, then deserialisation.

### Parallel safety

- Suites run with `parallel="methods"`: TestNG shares one test-class instance
  across threads. Mutable instance fields in test classes must be thread-confined
  (`ThreadLocal`, cleared in `@AfterMethod(alwaysRun = true)`). A thread-safe
  collection is not enough — it prevents corruption, not cross-test interference.

### BDD (Cucumber)

- Cucumber 7.x through `cucumber-testng` with PicoContainer. All Cucumber
  artifacts take their version from `cucumber-bom`.
- Scenarios duplicate existing test cases; they never replace TestNG tests or
  change their behaviour. Expected values shared by both live in
  `testing.testdata`. A scenario may cover a subset of its test case's checks;
  do not extend it to match its spec unless asked.
- Tags replace `priority` / `testName` / `groups`: the test case ID (`@BS-001`),
  `@smoke` or `@regression` by the same rule as TestNG groups, and `@api` or
  `@ui`.
- Gherkin: third person ("the user"), declarative — behaviour, not UI mechanics
  or HTTP calls. One `When` per scenario, except end-to-end journeys (e.g. BS-001),
  which alternate `When` / `Then`. Actions never go into `Then` steps.
- Cucumber Expressions; regular expressions only where they substantially
  simplify the binding.
- Step definitions call `*Steps` / `ApiSteps` only — no locators, `WebElement`
  or RestAssured calls. Missing behaviour goes into the steps layer.
- Assertions only in `Then` steps, same rules as TestNG; for UI, each `Then`
  creates its own `SoftAssert` and calls `assertAll()` at its end.
- State between steps only through the PicoContainer-injected scenario context.
  No static fields in glue or hooks.
- Step definition classes are organised by domain concept, not by feature file.
  Search existing glue before adding a step — no near-duplicate phrasings.
- API cleanup runs in an `@After("@api")` hook; a failed cleanup is logged and
  never fails the scenario.
- BDD runs through its own suite XML, not the root `testng.xml`.

## Working agreements

### Git

- Never write to git: no branches, commits, stashes, pushes or pull requests.
- Reading is fine: `git status`, `git diff`, `git log`.

### Decisions

- Decide small, reversible choices yourself — naming, where a constant lives,
  a literal-to-constant refactor, which of two equivalent structures to use.
  State each in one line in your summary.
- Stop and ask only when: a spec and the code disagree; a dependency would be
  added or changed; a rule in this file would have to be broken; or the change
  is hard to undo.
- After changing code outside the task's own scope, run the affected existing
  suite and report the result.

### Cleanup

- Remove scratch files, draft implementations and debug fragments before a task
  is done.

### Sandbox and lessons-learned packages

- `src/test/java/sandbox` (experiments) and `src/test/java/lessonslearned`
  (exercises based on interview feedback) are not held to this file's rules,
  unless a prompt explicitly asks for them. Do not refactor, restyle or "fix"
  code there, and leave both out of repo-wide changes, reviews and audits.
- Exception: if a change elsewhere breaks compilation there, make the minimal fix
  that keeps it compiling and say so — a compile error there breaks every
  `mvn test` run.
- Their suite XMLs live under `src/test/resources/suite/`; `lessonslearned`
  suites are not wired into CI. Both packages stay in the repo by design; the
  Cleanup rule does not apply to them.

### Generated output

- Anything longer than a few lines goes to a file, not the chat, unless the
  prompt asks for the chat.
- One-off analysis, comparisons and audits go to `reports/` as
  `report_<topic>_<YYYY-MM-DD-HH-MM>.md` and are kept. Documents meant to be kept
  live in `docs/`.

### README and this file

- Do not edit README.md unless explicitly asked — it is a stable overview.
- Do not edit CLAUDE.md. If a rule is missing, wrong or contradicts the code, say
  so and propose the wording.

## Dependencies

- Never choose a dependency version from memory. Say which dependency is needed
  and why, and let me pin the version.
- Never add a dependency without saying so explicitly in your summary.