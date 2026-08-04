# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project overview

A Java/Selenium UI test automation project (RestAssured is also a dependency for future API tests, per the project name `uiandapitesting`, though no API tests exist yet). Currently contains a single Page Object Model test suite against the public demo site `https://demoqa.com/text-box`.

- Java 17, Maven, TestNG, Selenium 4, WebDriverManager, SLF4J + Log4j2.

## Build & test commands

- Compile: `mvn compile`
- Run tests: `mvn test`
  - **Known gap:** `pom.xml` configures `maven-surefire-plugin` to run via `<suiteXmlFile>testng.xml</suiteXmlFile>` at the project root, but no `testng.xml` exists in the repo and it is not gitignored. `mvn test` currently fails with `Suite file ... testng.xml is not a valid file`. Tests are actually run through IntelliJ's per-class/per-method TestNG run configurations (see `.idea/workspace.xml`). To run tests from the CLI you must first create a `testng.xml` suite file (or otherwise adjust/remove the surefire `suiteXmlFiles` config).
- Run a single test class from an IDE: use the TestNG run configuration for the class (e.g. `TextBoxTest`), or once a `testng.xml` exists, `mvn test -Dtest=TextBoxTest`.

## Architecture

Three-layer Selenium structure: **Steps → Pages → BasePage**, with tests driving Steps only (never Pages directly).

- `factory.WebDriverFactory` — creates the `WebDriver` instance. `Browser` enum (`CHROME`, `EDGE`, `SAFARI`) selects the driver; WebDriverManager handles driver binaries automatically (Safari needs none, it's built into macOS).
- `ui.pages.BasePage` — base class for all page objects. Wraps common Selenium interactions (`click`, `sendKeys`, `getText`, `isDisplayed`, `waitForPageLoad`) with an explicit `WebDriverWait` (10s timeout). All element interaction in page objects should go through these helpers rather than calling WebElement methods directly, so waits stay consistent.
- `ui.pages.*Page` — page objects hold `By` locators as private fields and expose action/getter methods (e.g. `TextBoxPage`). No assertions live here.
- `ui.steps.BaseSteps` — base class for step objects; holds the shared `openUrl(String)` helper.
- `ui.steps.*Steps` — step objects (e.g. `TextBoxSteps`) compose one or more page objects into business-level actions used by tests (e.g. `fillFullName`, `submitForm`, `getFullName`). This is the layer tests interact with. Step methods also own logging (via SLF4J loggers) for key actions.
- `testing.ui.BaseUITest` — TestNG base test class. `@BeforeMethod` creates a Chrome `WebDriver`, maximizes the window, and instantiates the relevant `*Steps` object(s) as protected/package fields for subclasses to use. `@AfterMethod` is meant to quit the driver, but `driver.quit()` is currently commented out — the browser window is intentionally left open after each test run.
- Test classes under `src/test/java/testing/ui` extend `BaseUITest` and call only the `*Steps` object's methods, then assert with TestNG `Assert`.

When adding a new page under test, follow the existing pattern: add a `<Foo>Page` in `ui.pages` extending `BasePage`, a `<Foo>Steps` in `ui.steps` extending `BaseSteps` that wraps it and is wired up in `BaseUITest`, then a `<Foo>Test` in `testing.ui` extending `BaseUITest`.

## Logging

Log4j2 config is at `src/test/resources/log4j2.xml` — console-only appender with a colorized pattern (a commented-out file appender is available if file logging is ever needed). Loggers are obtained per-class via SLF4J (`LoggerFactory.getLogger(...)`), following the pattern already used in `WebDriverFactory`, `TextBoxPage`, `TextBoxSteps`, and the test classes.
