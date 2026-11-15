# Flaky UI lookups and CI step skipping — 2026-11-15

## Source

The CI failure analysis named in the prompt was not in `reports/`. The user
gave its findings in the chat instead:
- `WebTablesPage.fillRecordForm` looked up `modalContainer` with
  `driver.findElement` right after `openAddForm` clicked Add.
- `CheckBoxPage.expandNode` looked up a child's switcher with `findElement`
  right after its parent had been expanded.

Both fail with `NoSuchElementException` in headless CI.

## New BasePage helpers

- `waitForVisible(By)`: an explicit wait (`visibilityOfElementLocated`) on the
  shared 10-second `WebDriverWait`. It returns the element.
- `waitForVisible(WebElement container, By)`: the same wait, scoped to a
  container, because the page locators are relative (`.//`). It polls
  `container.findElement` until the element is displayed. `WebDriverWait`
  ignores `NotFoundException` between polls.
- No implicit waits were added.

## Places fixed

From the analysis:

| Page | Method | Lookup | After which action |
|---|---|---|---|
| WebTablesPage | fillRecordForm | modalContainer | Add / Edit click opens the modal |
| CheckBoxPage | expandNode | child switcher | the parent node is expanded |

From the audit (same pattern):

| Page | Method | Lookup | After which action |
|---|---|---|---|
| WebTablesPage | clickClose | modalContainer | can directly follow the Add / Edit click |
| WebTablesPage | clickSubmit | modalContainer | can directly follow the Add / Edit click (no caller does this today) |
| WebTablesPage | getRecordByEmail | row by email | the table re-renders after Submit |
| CheckBoxPage | selectItem | child checkbox | the parent node is expanded |
| CheckBoxPage | getSelectedItems | resultContainer | the result block is rendered after a checkbox is selected (it used to call `findElement` and then `isDisplayed`, so it could throw before waiting) |
| TextBoxPage | getFullName, getEmail, getCurrentAddress, getPermanentAddress | output fields | the output block is rendered after Submit |

Audited but not changed:
- `WebTablesPage.getRecordsOnCurrentPage` and `getRecordCountOnCurrentPage`
  run after add, delete, filter and page changes, and `readPageInfo` runs after
  Next / Previous. No single element becomes visible when these refreshes
  finish, so a visibility wait cannot detect them. They would need a
  content-based wait, such as a row count or a text change. That is a separate
  decision.
- Container lookups right after `driver.get` (`treeContainer`,
  `tableContainer`, TextBox inputs) do not follow an action that opens,
  expands or re-renders anything.

## Workflow steps changed

Each of these steps gets `!cancelled()`. Existing event filters are kept and
combined with it:

- nightly.yml, job `full_regression`: API parallel, API parallel with stubs,
  BDD parallel. UI regression is the first test step and is unchanged.
- ci-workflow.yml, job `simple_check`: UI smoke (`!cancelled()`), PR-only
  (`!cancelled() && github.event_name == 'pull_request'`), UI regression
  (`!cancelled() && (<existing PR / master-push filter>)`).
  `cicd_simple_commit` is the first test step and is unchanged.
- ci-workflow.yml, job `bdd`: it has only one test step, so nothing changed.

The job still fails if a suite fails: a failed step without
`continue-on-error` marks the job failed. The `continue-on-error` setting is
only on the Allure steps.

These changes are not verified on GitHub. Nothing has been pushed.

## 20-run check

Command, run locally on the uncommitted working tree with headless Chrome
against the live demoqa.com:
`mvn -B test -DsuiteXmlFile=src/test/resources/suite/ui_regression.xml -Dheadless=true`

| Runs | Result |
|---|---|
| 1–19 | 7 tests, 0 failures each |
| 20 | 7 tests, 1 failure |

In run 20, `WebTablesTest.editRecordUpdatesTheRow` (WT-002) failed with
`ElementClickInterceptedException` in `WebTablesPage.clickEditForRow`
(`BasePage.click`). Another element (`<td>`) received the click. This is not a
lookup failure: the Edit icon was found but covered. The likely cause is that
the Add modal was still closing, or the table was still re-rendering, after
`addRecord` submitted it.

Suggestion (not done): add a `BasePage` helper that waits for an element to
become invisible. Use it after Submit until `modalContainer` is gone. Also
consider a rule line in ui.md for "an action right after something closes".
