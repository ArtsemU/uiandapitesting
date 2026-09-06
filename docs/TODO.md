# TODO

## Decide

- [ ] **Positional indexes.** CLAUDE.md forbids them, but `WebTablesPage` uses
  `./td[n]` for columns and `CheckBoxPage` uses `ancestor::div[...][1]` —
  neither has a non-positional alternative. Carve out an exception, drop
  the rule, or leave it knowingly violated.

- [ ] **Hard vs soft assertions.** The agent uses `SoftAssert` for everything,
  including preconditions — those must stop the test. Fix in CLAUDE.md, in
  the test case format, or both.

- [ ] **Test case page format.** A flat list of steps cannot express which
  checks are gates and which are results. Decide on a
  Preconditions / Steps / Expected result structure before more cases exist.

## Fix

- [ ] Delete `docs/driver-lifecycle.md` — fabricated example, describes a
  Cucumber setup that does not exist here. Already cited once as real.

- [ ] Remove assistant-behaviour rules from the Confluence overview pages
  (generated docs go to a file, agent does not write to git). CLAUDE.md only.

- [ ] Move type parsing out of `TextBoxSteps` into `TextBoxPage` —
  `value.split(":")[1]` leaks DOM handling into the steps layer.

- [ ] Move `EXPECTED_OUTPUT_<SCOPE>` to the demoQA child page. It is UI-specific.

## Later

- [ ] **Reporting.** Nothing beyond console output and surefire XML. Decide
  between Allure, ExtentReports or the surefire HTML report.

- [ ] **Screenshots on failure.** Listener work — `ITestListener.onTestFailure`,
  not try/catch in tests. Depends on the reporting decision.

- [ ] Enable `driver.quit()` behind `-Dkeep.browser=true` instead of leaving it
  commented out.

- [ ] Browser is hardcoded to `CHROME` in `BaseUITest` though `EDGE` and
  `SAFARI` exist. Wire it to a property or record why not.

- [ ] `api.Config` reads a single `api.properties` with no environment
  selection. Add a `-Denv` mechanism once a second environment exists;
  deliberately out of scope while there is only one.
