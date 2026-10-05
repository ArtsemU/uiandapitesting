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
- `mvn allure:report` builds the local Allure HTML report from
  `target/allure-results/`. It is a standalone goal, not bound to any Maven
  lifecycle phase, specifically so a failing test does not stop the build before
  the report step runs. `run-tests-and-report.cmd` runs a test command followed
  by `mvn allure:report` on its own line (not `&&`), so the report regenerates
  whether the run passed or failed, and also fires automatically from
  `AllureReportListener` on any `mvn test`/IDE-native run. The report lands in
  `allure-report/report-<timestamp>/` — a single-file `index.html` (Allure 3
  `singleFile`), outside `target/` so `mvn clean` never wipes it. Override the
  destination per run with `-Dallure.report.directory=<path>`.

## Architecture

Three layers, driven strictly top-down: **Tests → Steps → Pages → BasePage**.
Tests call Steps only, never Pages directly.

- UI Pages, Steps and `BaseUITest.currentDriver()`: see `.claude/rules/ui.md` (loads when working on files under `src/main/java/ui/`, `src/main/java/factory/` or `src/test/java/testing/`).
- Tests call Steps only and hold all assertions. No helper methods: anything
  reusable belongs in the steps layer.
- Test configuration methods: see `.claude/rules/testing.md` (loads when working on files under `src/test/java/`).

### Layer boundaries

- Objects built through a builder are passed whole. Never unpack them into
  positional parameters at a layer boundary.
- UI-only layer boundaries: see `.claude/rules/ui.md` (loads when working on files under `src/main/java/ui/`, `src/main/java/factory/` or `src/test/java/testing/`).
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
- `reporting` — TestNG listeners for reporting (`AllureReportListener`,
  `ScreenshotOnFailureListener`), registered through
  `META-INF/services/org.testng.ITestNGListener`, not suite XMLs.

`src/test/resources`:
- `bdd/features/<area>/` — feature files. Not under `src/test/java`: Maven does
  not copy non-Java files from there onto the test classpath.
- `schemas/` — JSON Schemas for API responses (`<resource>-schema.json`).

## CI/CD

GitHub Actions workflows under `.github/workflows/`:

- `ci-workflow.yml` — runs on every push and pull request. Push and PR both run
  a fast API + UI smoke check (job `simple_check`); PRs and merges into `master`
  additionally run the broader regression suites and the BDD suite (job `bdd`).
- `nightly.yml` — scheduled (cron) and manually dispatchable; runs the full
  regression suite (job `full_regression`), independent of any push/PR/merge
  event.
- `cd-simulate.yml` — manual, simulated deploy pipeline through qa1 → stage →
  prod GitHub Environments (real approval/wait-timer gating, placeholder deploy
  step). No tests run here; untouched by the reporting work below.

Every job in `ci-workflow.yml` and `nightly.yml` ends with two `if: always()`
steps, after its last test step: generate the Allure report
(`mvn allure:report`) and upload it via `actions/upload-artifact` (artifact
names `allure-report-simple_check`, `allure-report-bdd`,
`allure-report-full_regression`; `retention-days: 2`). This runs regardless of
pass/fail and does not change surefire's own failure behaviour — the exit code
that gates branch protection is untouched.

## Test specifications

Test cases live in Confluence, space `Uiandapite`, under **Test Project**:
**demoQA** (UI) and **API**. Read the relevant page before writing or changing a
test. Do not invent scenarios — if a spec is missing, ambiguous or incomplete,
say so and ask.

Confluence is read-only. Never create, update or delete pages there.

## Conventions

Locators: see `.claude/rules/ui.md` (loads when working on files under `src/main/java/ui/`, `src/main/java/factory/` or `src/test/java/testing/`).

Test identifiers, test data, assertions and parallel safety: see `.claude/rules/testing.md` (loads when working on files under `src/test/java/`).

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

### Reporting

- UI Steps `@Step` annotations: see `.claude/rules/ui.md` (loads when working on files under `src/main/java/ui/`, `src/main/java/factory/` or `src/test/java/testing/`).
- API request/response logging goes through the `AllureRestAssured` filter,
  wired once into the shared `RequestSpecBuilder` in `api.ApiSpec`. Individual
  tests never add their own request/response logging. The `Authorization`
  header is redacted in the attachment; request/response bodies are not
  (`createUser`/`generateToken` bodies carry the password/token in plain text)
  — accepted, since both already appear elsewhere (console log, `api.properties`).
- `ScreenshotOnFailureListener` attaches a PNG to Allure on `onTestFailure`,
  for classes extending `BaseUITest` only. API and BDD failures are skipped
  (BDD: logged nothing, since Cucumber's own `@After` hook already closed the
  driver before TestNG sees the failure; plain API classes: skipped silently).
  A failure inside the screenshot capture itself is caught and logged, never
  rethrown — it must never mask the real test failure. BDD UI scenarios
  getting no screenshots is a known, accepted gap (see TODO.md); fixing it
  means capturing in `UiHooks`' `@After`, not this listener.

## Working agreements

### Git

- Never write to git: no branches, commits, stashes, pushes or pull requests.
- Reading is fine: `git status`, `git diff`, `git log`.

### Decisions

- Decide small, reversible choices yourself — naming, where a constant lives,
  a literal-to-constant refactor, which of two equivalent structures to use.
  State each in one line in your summary.
- Stop and ask only when: a spec and the code disagree; a dependency would be
  added or changed; a rule in this file would have to be broken; the change is
  hard to undo; or the prompt does not match the current state of the repo
  (the work is already done, or what it refers to is missing).
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
- Agent instructions live only in the root CLAUDE.md and .claude/rules/.
  Do not create nested CLAUDE.md files; area-specific rules go into a
  path-scoped file under .claude/rules/.

## Dependencies

- Never choose a dependency version from memory. Say which dependency is needed
  and why, and let me pin the version.
- Never add a dependency without saying so explicitly in your summary.
- Allure's four artifacts (`allure-testng`, `allure-java-commons`,
  `allure-rest-assured`, all on `${allure.version}`) and the `allure-maven`
  plugin are already pinned and settled — do not propose different versions
  without being asked.