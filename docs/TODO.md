# TODO

## Decide

- [x] **Positional indexes.**
- [x] **Hard vs soft assertions.**
- [x] **Test case page format.**

## Fix

- [x] Delete `docs/driver-lifecycle.md` — deleted, and both dangling
  references removed (Confluence Project Overview rewritten, and the
  entire `docs/confluence/` local mirror deleted as redundant — the agent
  reads Confluence directly via MCP, so the mirror served no purpose and
  was the actual source of staleness).

- [x] Remove assistant-behaviour rules from the Confluence overview pages
  (generated docs go to a file, agent does not write to git). CLAUDE.md
  only. Confirmed removed from Project Overview's Conventions section.

- [x] Move type parsing out of `TextBoxSteps` into `TextBoxPage` — done in
  code, and Confluence's "Known gaps" entry for it removed (page renamed
  from "Overview of demoQA UI Testing" to "UI Testing").

- [x] Move `EXPECTED_OUTPUT_<SCOPE>` to the demoQA child page. CLAUDE.md
  marked it UI-only, and the convention is now documented on the "UI
  Testing" Confluence page too.

## Later

- [x] **Reporting.** Nothing beyond console output and surefire XML. Decide
  between Allure, ExtentReports or the surefire HTML report.

- [x] **Screenshots on failure.** Listener work — `ITestListener.onTestFailure`,
  not try/catch in tests. Depends on the reporting decision.

- [x] ~~Enable `driver.quit()` behind `-Dkeep.browser=true`~~ Resolved
  differently: `driver.quit()` is unconditional in
  `@AfterMethod(alwaysRun = true)`, no flag — settled, not reopening.

- [x] Browser is hardcoded to `CHROME` — done via `-Dbrowser` system
  property.

- [x] `api.Config` reads a single `api.properties` with no environment
  selection — done: `-Denv=<name>` selects `config/api-<name>.properties`
  (standalone, no fallback); an unknown name fails at startup.
- [ ] When re-enabling Grid: refactor createRemoteDriver to accept (Browser, headless)
  like the local path, not a new enum value.