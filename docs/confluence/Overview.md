# UI and API Testing — Project Overview

## Purpose

This is a practice project for working through AI-assisted testing tools and workflows: writing Selenium test suites with an AI coding assistant, iterating on structure, and exercising the tooling on a small, low-stakes codebase. It is not a commercial product and not a portfolio showcase — treat gaps and rough edges as expected, not as defects to hide.

## Stack

Versions are taken from `pom.xml`, not guessed:

- Java 17 (`maven.compiler.source`/`target`), Maven
- TestNG 7.12.0
- Selenium Java 4.45.0
- WebDriverManager 6.3.4 (resolves browser driver binaries automatically)
- RestAssured 6.0.0 (test-scope dependency, not yet used by any test — see Planned)
- SLF4J API 2.0.18 + Log4j2 (log4j-core, log4j-slf4j2-impl) 2.26.0
- maven-surefire-plugin 3.2.5, wired to run the suite defined in `testng.xml`

## Current scope

UI tests run against demoQA. Three modules are covered:

- **Text Box** (`TextBoxTest`) — verifies that submitting the form displays the entered full name in the output block, and that all four submitted fields (full name, email, current address, permanent address) are reflected correctly in the output.
- **Check Box** (`CheckBoxTest`) — verifies that selecting a node in the file-tree checkbox propagates to the correct set of child items (e.g. selecting "Desktop" selects Desktop, Notes and Commands; selecting a single leaf node selects only that node).
- **Web Tables** (`WebTablesTest`) — verifies adding a record and its full field values appearing in the table, editing a record updating its row, filtering by email narrowing the table to one matching row, and pagination behavior once the row count exceeds one page.

`testng.xml` currently wires up `CheckBoxSuite` with `CheckBoxTest` and `WebTablesTest`. `TextBoxTest` exists in the codebase but is not currently listed in `testng.xml`, so `mvn test` does not run it by default.

## Architecture

Three layers, driven strictly top-down: **Tests → Steps → Pages → BasePage**.

- **`ui.pages.BasePage`** — shared Selenium plumbing: `click`, `sendKeys`, `getText`, `isDisplayed`, `waitForPageLoad`, all built on an explicit 10-second `WebDriverWait`. Must not contain assertions or business logic.
- **`ui.pages.*Page`** — one page object per demoQA page. Holds `By` locators as private fields, exposes action and getter methods, and returns domain objects when a caller needs more than one field. Must not contain assertions, and is the only layer allowed to convert between the DOM's string form and domain types.
- **`ui.steps.BaseSteps`** — shared `openUrl(String)` helper.
- **`ui.steps.*Steps`** — composes one or more page objects into business-level actions and owns logging for key actions (SLF4J). This is the layer tests are allowed to call.
- **`ui.models.*`** — domain objects and their builders (e.g. `WebTableRecord`). Passed whole across layer boundaries — never unpacked into positional parameters. Contains no constants.
- **`testing.ui.BaseUITest`** — TestNG base class. `@BeforeMethod` creates a Chrome `WebDriver` via `factory.WebDriverFactory` and instantiates the `*Steps` fields subclasses use. `@AfterMethod` does not call `driver.quit()` — it's commented out on purpose, so browsers are left open after a run for inspection. This means repeated local runs accumulate open Chrome windows/processes; that's a known, deliberate gap, not an oversight to fix silently.
- **`testing.ui.*Test`** — test classes, calling only `*Steps` methods and asserting with TestNG `Assert`/`SoftAssert`.

## Conventions for a first test

- Locators are always `By.xpath()`, scoped to a container (demoQA reuses ids between form and output). No other locator strategy, no positional indexes (`[1]`, `(...)[2]`).
- Every `@Test` carries a `description` of the form `"XX-000: short description"` (TB, CB, WT are the current module prefixes); test IDs are never reused.
- Every assertion carries a failure message describing which behavior broke, not the values.
- Test data is fixed and deterministic — no random values, no Faker; multiple records vary by counter. Constants for the app's own internal values are named `EXPECTED_OUTPUT_<SCOPE>`.
- Test data classes live under `testing.testdata`, separate from the test classes that use them.
- No temporary or exploratory code gets committed.

## Planned

- **API tests against restful-booker.** RestAssured is already a dependency for this; no API test classes exist yet.
- **BDD with Cucumber.** Not yet introduced into the build or dependency set; current tests are plain TestNG only.
