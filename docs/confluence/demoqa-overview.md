# demoQA — UI Testing

Child page of the project overview. Purpose, stack, build commands and
project-wide conventions are covered there and not repeated here.

## What demoQA is

demoQA (demoqa.com) is a public site offering a set of standalone UI
widgets for practicing test automation — forms, a checkbox tree, data
tables, and more — each on its own page, with no login and no backend
dependency to set up. It is the target site for every UI test in this
repository. No rationale for choosing it over another practice site is
recorded anywhere in the repo.

## Modules covered

**Text Box** (`TextBoxTest`, `/text-box`):
- `TB-001 setNameOnlyTest` — fills only the full-name field, submits, and
  asserts the output block's name text equals the entered value.
- `TB-002 submittedDataIsDisplayedInOutput` — fills all four fields (full
  name, email, current address, permanent address), submits, and
  soft-asserts all four output fields match what was entered.

**Check Box** (`CheckBoxTest`, `/checkbox`):
- `CB-001` — expands Home then Desktop, selects the Desktop checkbox, and
  asserts the selected-items list is exactly Desktop, Notes and Commands.
- `CB-002` — expands into Documents/Office, selects Office and Downloads,
  and asserts the selected-items list matches both subtrees' full leaf sets.
- `CB-003` — expands Home then Desktop, selects only Notes, and asserts the
  selected-items list contains just Notes.

**Web Tables** (`WebTablesTest`, `/webtables`):
- `WT-001` — adds one record, reads the row back by email, and asserts it
  matches the submitted record and that the row count grew by exactly one.
- `WT-002` — adds a record, edits its first name and department, reads the
  row back by email, and asserts the edit landed and the row count is
  unchanged.
- `WT-003` — adds a record, filters by its email, and asserts exactly one
  row remains and matches the record.
- `WT-004` — adds 11 records to force pagination, then asserts the table
  spans exactly two pages, page 1 is full, page 2 holds the remainder and
  differs from page 1, and navigating back to page 1 restores its rows.

## Architecture layers

- **`BasePage`** — shared Selenium plumbing only: `click` (scrolls into
  view, waits for clickability), `sendKeys` (waits for visibility, clears
  then types), `getText`, `isDisplayed`, `waitForPageLoad`, all built on a
  10-second `WebDriverWait`. Never contains assertions or page-specific logic.
- **`*Page`** — locators as private fields (or private methods for
  parameterized ones), action and getter methods, and any parsing between
  the DOM's string form and a domain type (e.g. `WebTablesPage` parsing
  cell text to `int` and building `WebTableRecord`). Never contains
  assertions and never calls another page object.
- **`*Steps`** — composes one or more page objects into business actions
  (e.g. `addRecord`, `editRecord`) and owns SLF4J logging for them. Never
  touches `By`, `WebElement`, or the driver directly, and never asserts.
- **`*Test`** — calls only `*Steps` methods and holds all assertions
  (`Assert`/`SoftAssert`). Never instantiates or calls a page object.

## UI testing conventions

- Locators are always `By.xpath()`, scoped to a container (e.g.
  `treeContainer`/`resultContainer`, `tableContainer`/`modalContainer`),
  per the project's xpath-only rule.
- Step methods log the action they perform, not the raw Selenium calls
  underneath.

## Known gaps

- `driver.quit()` is commented out in `BaseUITest.tearDown()`, on purpose,
  so a browser stays open after each test for inspection. Consequence:
  every run leaves a Chrome window/process behind, so repeated local runs
  accumulate open browsers.
- Browser selection is hardcoded to `CHROME` in `BaseUITest`, even though
  `WebDriverFactory.Browser` also defines `EDGE` and `SAFARI`.
- `WebTablesPage`'s column lookups (`./td[n]` via its `Column` enum) and
  `CheckBoxPage`'s ancestor lookups (`ancestor::div[...][1]`) use
  positional indexes, which the "no positional indexes" locator rule
  otherwise prohibits — columns and nearest-ancestor selection have no
  non-positional alternative here.
- `TextBoxSteps` parses the raw `"label:value"` text returned by
  `TextBoxPage`'s getters (`value.split(":")[1]`) instead of the page
  layer returning an already-parsed value, so DOM string handling leaks
  into the Steps layer for this page.
