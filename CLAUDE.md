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
  `WebDriverFactory`, not by `pom.xml`.
- UI runs on CI: see `.claude/rules/ci.md` (loads automatically for workflow files).
- Allure report: see `.claude/rules/reporting.md` (loads automatically for reporting code).

## Architecture

Three layers, driven strictly top-down: **Tests → Steps → Pages → BasePage**.
Tests call Steps only, never Pages directly.

- UI Pages, Steps and `BaseUITest.currentDriver()`: see `.claude/rules/ui.md` (loads automatically for UI code).
- Tests call Steps only and hold all assertions. No helper methods: anything
  reusable belongs in the steps layer.
- Test configuration methods: see `.claude/rules/testing.md` (loads automatically for tests).

### Layer boundaries

- Objects built through a builder are passed whole. Never unpack them into
  positional parameters at a layer boundary.
- UI-only layer boundaries: see `.claude/rules/ui.md` (loads automatically for UI code).
- API-only layer boundaries: see `.claude/rules/api.md` (loads automatically for API code).

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

CI/CD workflows: see `.claude/rules/ci.md` (loads automatically for workflow files).

## Test specifications

Test cases live in Confluence, space `Uiandapite`, under **Test Project**:
**demoQA** (UI) and **API**. Read the relevant page before writing or changing a
test. Do not invent scenarios — if a spec is missing, ambiguous or incomplete,
say so and ask.

Confluence is read-only. Never create, update or delete pages there.

## Conventions

Locators: see `.claude/rules/ui.md` (loads automatically for UI code).

Test identifiers, test data, assertions and parallel safety: see `.claude/rules/testing.md` (loads automatically for tests).

### BDD (Cucumber)

- BDD conventions: see `.claude/rules/bdd.md` (loads automatically for BDD code).
- BDD runs through its own suite XML, not the root `testng.xml`.

### Reporting

- UI Steps `@Step` annotations: see `.claude/rules/ui.md` (loads automatically for UI code).
- API request/response logging: see `.claude/rules/api.md` (loads automatically for API code).
- Screenshot on failure: see `.claude/rules/reporting.md` (loads automatically for reporting code).

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
  path-scoped file under .claude/rules/. Each rules file starts with its
  paths: frontmatter, then a "# Description" section: two or three simple
  sentences on what the rules are for and when the agent gets them.
  Pointers to a rules file (lines elsewhere that send the reader to it)
  name the file, never the folders or globs it covers.hi again! 

## Dependencies

- Never choose a dependency version from memory. Say which dependency is needed
  and why, and let me pin the version.
- Never add a dependency without saying so explicitly in your summary.
- Allure's four artifacts (`allure-testng`, `allure-java-commons`,
  `allure-rest-assured`, all on `${allure.version}`) and the `allure-maven`
  plugin are already pinned and settled — do not propose different versions
  without being asked.