# Project Overview

## Purpose

This is a practice project for working through AI-assisted testing tools and
workflows — writing test suites with an AI coding assistant, iterating on
structure and conventions, and exercising the tooling on a small, low-stakes
codebase. It is not a commercial product and not a portfolio showcase.

## Stack

Versions are taken from `pom.xml`:

- Java 17 (`maven.compiler.source`/`target`), Maven
- TestNG 7.12.0
- Selenium Java 4.45.0
- WebDriverManager 6.3.4 (resolves browser driver binaries automatically)
- RestAssured 6.0.0 (test-scope; no API tests exist yet — see Planned work)
- SLF4J API 2.0.18 + Log4j2 (log4j-core, log4j-slf4j2-impl) 2.26.0
- maven-surefire-plugin 3.2.5

## Running the tests

- `mvn compile` — compiles the project.
- `mvn test` — runs the suite defined in `testng.xml` at the project root,
  wired through `maven-surefire-plugin`. `testng.xml` lists which test classes
  belong to the suite; a class not listed there is not run by `mvn test` even
  if it compiles and passes on its own.
- A single class can be run from the IDE via its TestNG run configuration, or
  from the CLI with `mvn test -Dtest=<ClassName>`.

## Repository layout

`src/main/java`:

- `factory` — builds the `WebDriver` instance used by tests.
- `ui.pages` — Selenium page objects.
- `ui.steps` — reusable action sequences built on page objects.
- `ui.models` — domain objects and their builders, passed between layers.

`src/test/java`:

- `testing.ui` — test classes.
- `testing.testdata` — constants consumed by tests.

Logging is configured once, project-wide, via `src/test/resources/log4j2.xml`
(console-only, colorized), with loggers obtained per class through SLF4J.

## Conventions

These apply across the whole project, not just to UI tests:

- Every `@Test` carries `description = "XX-000: short description"` — a
  two-letter area prefix plus a three-digit number, under ~60 characters.
  Test IDs are never reused, even after a test is deleted.
- Test data is fixed and deterministic: no random values, no Faker. Where
  multiple records are needed, vary them with a counter.
- Every assertion carries a failure message stating which behaviour is
  broken, not the values — `assertEquals` already prints expected and actual.
- Temporary or exploratory code is never committed; it is deleted once it has
  served its purpose.
- Documents longer than a few lines (overviews, specifications, reports) are
  written to a file under `docs/`, not printed in chat.
- Git history is manual: branches, commits, pushes and pull requests are
  created by the user, not by the assistant. Reading (`status`/`diff`/`log`)
  is fine.

## Planned work

- API tests will be added using RestAssured, which is already a dependency
  but currently unused.
- BDD scenario support via Cucumber has been discussed (see
  `docs/driver-lifecycle.md`) but not started: there is no Cucumber
  dependency in `pom.xml` and no hooks class in the repository yet.
