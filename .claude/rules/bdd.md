---
paths:
  - "src/test/java/bdd/**"
  - "src/test/resources/bdd/**"
---
# Description

Rules for the BDD tests written with Cucumber: the feature files and the
Java code behind them. The agent gets this file automatically when it opens
a feature file or BDD code.

# BDD conventions

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
  creates its own `SoftAssert` and calls `assertAll()` at its end. Exception:
  precondition checks in `Given` steps use hard `Assert`, as in every other
  layer; result checks stay in `Then` steps only.
- State between steps only through the PicoContainer-injected scenario context.
  No static mutable state in glue or hooks; `static final` loggers and
  constants are allowed.
- Step definition classes are organised by domain concept, not by feature file.
  Search existing glue before adding a step — no near-duplicate phrasings.
- API cleanup runs in an `@After("@api")` hook; a failed cleanup is logged and
  never fails the scenario.
