# uiandapitesting

A practice project for working through AI-assisted testing tools and
workflows. Full documentation, including architecture and conventions, is
in Confluence: https://artemu666.atlassian.net/wiki/spaces/Uiandapite/overview

Repo-level conventions and architecture for AI coding agents are in
[CLAUDE.md](CLAUDE.md).

## Stack

- Java 17
- Maven
- TestNG 7.12.0
- Selenium Java 4.45.0 (+ WebDriverManager 6.3.4 for driver binaries)
- RestAssured 6.0.0 — API tests against the demoQA Bookstore API (`BookApiTests`)
- SLF4J 2.0.18 + Log4j2 2.26.0
- Cucumber 7.34.9 (`cucumber-testng` + PicoContainer, versions from `cucumber-bom`) — BDD scenarios
- RestAssured json-schema-validator 6.0.0 — JSON Schema checks on API responses (draft-04)

## Prerequisites

- JDK 17 installed, with `JAVA_HOME` pointing at it. Maven resolves the JDK
  through `JAVA_HOME`, not through whatever `java` is on `PATH` — on a
  machine with multiple JDKs installed, the build fails if `JAVA_HOME`
  points at the wrong one.

## Running

```
mvn compile
```

```
mvn test
```

Runs the suite defined in `testng.xml`.

To run a single class:

```
mvn test -Dtest=<ClassName>
```

### Running a scoped suite (smoke / regression / sandbox)

Suite XML files for scoped or parallel runs live in
`src/test/resources/suite/` and are not wired into `mvn test` — run them
explicitly via `-DsuiteXmlFile`:

```
mvn test -DsuiteXmlFile=src/test/resources/suite/ui_smoke.xml
```

### UI runs: headless mode and browser selection

Two JVM system properties control how UI tests launch a browser, read
directly by `WebDriverFactory` — not `pom.xml` settings:

- `-Dheadless=true` — runs without a visible browser window. Default is
  `false` (visible window), unchanged from before this flag existed.
  Required for any run on a machine without a display (CI runners).
- `-Dbrowser=CHROME|EDGE|SAFARI` — selects the browser. Default is `CHROME`.
  Safari does not support headless mode; requesting both logs a warning and
  starts a visible Safari window instead.

Example — headless Edge, smoke suite only:

```
mvn test -DsuiteXmlFile=src/test/resources/suite/ui_smoke.xml -Dheadless=true -Dbrowser=EDGE
```
### BDD (Cucumber) scenarios

Cucumber scenarios live in `src/test/resources/bdd/features/` and run through
their own suites, not through `mvn test`:

```
mvn test -DsuiteXmlFile=src/test/resources/suite/bdd.xml -Dheadless=true
```

`bdd_parallel.xml` runs the same scenarios in parallel (3 threads, set by
`data-provider-thread-count`).

Filter by tag with `-Dcucumber.filter.tags`, e.g. `"@ui"`, `"@smoke and @api"`
or a single test case: `"@WT-001"`. The `-Dheadless` and `-Dbrowser` switches
apply to `@ui` scenarios as well.

## CI/CD

GitHub Actions workflows (`.github/workflows/`):

- **`ci-workflow.yml`** — runs on every push and pull request. Push and PR
  both run a fast API + UI smoke check; PRs and merges into `master`
  additionally run the broader regression suites. Branch protection on
  `master` requires these checks to pass before a PR can be merged. 
  A separate `bdd` job runs the Cucumber suite on PRs and merges to `master`.
  A separate `bdd` job runs `bdd.xml` (headless) on PR events and pushes to
  `master`.
- **`cd-simulate.yml`** — manual, simulated deploy pipeline through
  `qa1` → `stage` → `prod` GitHub Environments (real approval/wait-timer
  gating, placeholder deploy step — there is no real deploy target).
- **`nightly.yml`** — scheduled full regression run, independent of any
  push/PR/merge event, including the Cucumber suite in parallel
  (`bdd_parallel.xml`). Also runs `bdd_parallel.xml` (headless). 

All UI steps in every workflow pass `-Dheadless=true` — GitHub's runners
have no display, so a UI suite run without this flag fails immediately on
browser startup, not just runs slower.

## Documentation

https://artemu666.atlassian.net/wiki/spaces/Uiandapite/overview