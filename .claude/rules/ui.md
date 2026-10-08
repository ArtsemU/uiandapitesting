---
paths:
  - "src/main/java/ui/**"
  - "src/main/java/factory/**"
  - "src/test/java/testing/**"
---

# Description

Rules for the UI part of the project: the code that works with
web pages, and the UI tests. The agent gets this file automatically when
it opens a UI file.

# UI conventions

## Architecture

- Pages hold `By` locators as private fields and expose actions and getters. No
  assertions. Element interaction goes through the `BasePage` helpers, not
  `WebElement` methods directly, so waits stay consistent. A direct `WebElement`
  call is allowed only with a code comment explaining why no helper fits (e.g.
  the React-controlled input in `WebTablesPage.clearField`). Locating elements
  with `findElement` / `findElements` is not interaction and is fine.
- Steps compose page objects into business-level actions and own SLF4J logging.
  No assertions, no `By` or `WebElement`. Steps hold the `WebDriver` only to
  build page objects; opening a URL goes through `BaseSteps.openUrl`.
- `BaseUITest.currentDriver()` is a read-only static accessor used only by
  reporting listeners (`ScreenshotOnFailureListener`) to reach the current
  thread's driver from outside the test. It is not for test or Steps code —
  tests still go through the Steps layer exclusively; this is a deliberate,
  narrow exception for infrastructure that runs outside the normal call chain.

## Layer boundaries

- Type conversion between the domain type and the string form the DOM uses lives
  in the page layer only.
- **UI only:** page objects return domain objects, not individual cell values,
  when the caller needs more than one field.

## Locators

- Every locator uses `By.xpath()` — nothing else. See
  `docs/decisions/0001-xpath-for-all-locators.md`.
- Always scope locators to a container. demoQA reuses the same id in the form and
  in the output block, so an unscoped locator silently resolves to the wrong
  element.
- Positional indexes only where no stable attribute exists (table columns,
  nearest ancestor). A positional locator does not break when the page changes —
  it silently starts matching something else.

## Test data

- **UI only:** constants holding values read from the page under test are named
  `EXPECTED_OUTPUT_<SCOPE>` — the application's internal values, not UI labels.

## Reporting

- UI `*Steps` methods carry Allure `@Step("...")` alongside their existing
  SLF4J log line — both stay, they serve different readers (console vs Allure
  report). This applies to every public method in the Steps layer, not just
  today's three classes: any new UI Steps method gets `@Step` too. Use
  parameter placeholders (`{param}`, or `{object.field}` for one meaningful
  field of an object parameter) rather than leaving the annotation's value
  empty or dumping a whole object's `toString()`.
