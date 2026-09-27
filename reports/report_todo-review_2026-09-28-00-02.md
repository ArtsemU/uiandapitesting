# TODO.md review — open items

Snapshot of `docs/TODO.md` (working copy, with the three "Decide" items
already ticked) checked against the code and the live Confluence space
`Uiandapite`. No code or docs were changed.

## Summary

| # | Item | Status |
|---|------|--------|
| F1 | Delete `docs/driver-lifecycle.md` | **Done in repo**, but dangling references remain |
| F2 | Remove assistant-behaviour rules from Confluence overview | **Still open** |
| F3 | Move type parsing out of `TextBoxSteps` | **Done in code**; Confluence still describes the old state |
| F4 | Move `EXPECTED_OUTPUT_<SCOPE>` to demoQA child page | **Still open** |
| L1 | Reporting | **Still open** |
| L2 | Screenshots on failure | **Still open** (blocked by L1) |
| L3 | `driver.quit()` behind `-Dkeep.browser` | **Obsolete**: quit is active, no flag |
| L4 | Browser hardcoded to `CHROME` | **Done** |
| L5 | `api.Config` environment selection | **Still open (deferred by design)** |

## Fix

### F1 — Delete `docs/driver-lifecycle.md` — done, with leftovers

- The file no longer exists. `docs/` holds only `TODO.md`, `confluence/` and
  `decisions/`.
- It is still referenced in two places:
  - Confluence **Project Overview** (page 1114115), under "Planned work".
  - The local mirror `docs/confluence/project-overview.md:73`.
- Both still say Cucumber "has been discussed (see `docs/driver-lifecycle.md`)".
  That is the "cited as real" problem the TODO warned about. It has moved from
  the file to the pages that link to it.

### F2 — Assistant-behaviour rules on Confluence — still open

Confluence **Project Overview** → "Conventions" still contains:

- "Documents longer than a few lines ... are written to a file under `docs/`,
  not printed in chat."
- "Git history is manual: branches, commits, pushes and pull requests are
  created by the user, not by the assistant."
- Also arguably: "Temporary or exploratory code is never committed; it is
  deleted once it has served its purpose."

The local mirror `docs/confluence/project-overview.md:60-66` has the same text.
Because Confluence is read-only for the agent, this item is manual only.

### F3 — Type parsing in `TextBoxSteps` — done in code

- `TextBoxSteps` no longer parses anything. Its getters pass the values through
  and log them.
- `TextBoxPage` now owns the parsing through a private `parseOutputValue(...)`
  (`src/main/java/ui/pages/TextBoxPage.java:62`). It splits on the first `:` and
  trims the result. It also throws a descriptive `IllegalStateException` when
  the separator is missing, which makes it stricter than the old
  `split(":")[1]`.
- **Left over:** Confluence **Overview of demoQA UI Testing** (page 2392065),
  under "Known gaps", still lists this as an open gap. That entry is now wrong.

### F4 — `EXPECTED_OUTPUT_<SCOPE>` to demoQA child page — still open

- The rule is not on the Confluence demoQA page. It appears only in CLAUDE.md,
  under the general "Test data" section, where it reads as project-wide.
- In code it is used only by UI: `CheckBoxTestData` and `CheckBoxTest`. So
  "UI-specific" still holds.

## Later

### L1 — Reporting — still open

`pom.xml` has only `maven-surefire-plugin`. There is no Allure, ExtentReports
or reporting plugin, and no listener in `testng.xml`. Nothing has been decided.

### L2 — Screenshots on failure — still open

There is no `ITestListener` in the codebase. It still depends on L1.

### L3 — `driver.quit()` behind `-Dkeep.browser=true` — obsolete as written

- `BaseUITest.tearDown()` now calls `driver.quit()` unconditionally, which is
  null-safe and uses `alwaysRun = true`. It is no longer commented out.
- There is no `keep.browser` property anywhere. The "keep browser open for
  inspection" option has been dropped rather than put behind a flag.
- Decide whether to close the item or reword it to "add opt-in keep-open
  flag", if that is still wanted.
- **Left over:** Confluence **Overview of demoQA UI Testing** → "Known gaps"
  still says `driver.quit()` is commented out and that windows pile up.

### L4 — Browser hardcoded to `CHROME` — done

- `WebDriverFactory.createDriver()` reads `-Dbrowser`, defaulting to `CHROME`,
  and `-Dheadless`, defaulting to `false`. `BaseUITest` calls the method with no
  arguments.
- CLAUDE.md already documents this.
- **Left over:** Confluence **Overview of demoQA UI Testing** → "Known gaps"
  still lists it as hardcoded.

### L5 — `api.Config` environment selection — still open, deferred on purpose

`Config` still loads a single `config/api.properties`, which contains only
`base.url` and `user.password`. There is no `-Denv` handling. The item's own
condition, "a second environment exists", has not been met, so leaving it open
is correct.

## Stale Confluence content found while checking

This is outside the TODO list but related to it. All edits are manual.

- **Project Overview**
  - "RestAssured ... no API tests exist yet" and "API tests will be added" are
    stale, because `BookApiTests` and the `api` package exist.
  - The layout section does not mention `api`, `api.models` or `api.tests`.
  - Conventions say `description = "XX-000: ..."`, but CLAUDE.md and the code
    use `testName` plus `priority` and `groups`.
- **API Testing Overview** (page 2392073): its summary still says "This area
  does not exist yet".
- **Overview of demoQA UI Testing** → "Known gaps"
  - Three of its four entries are now resolved: `driver.quit()`, the hardcoded
    browser, and `TextBoxSteps` parsing.
  - The fourth, positional indexes, is now an accepted exception in CLAUDE.md,
    so it is no longer a "gap" either.
- The local mirrors in `docs/confluence/` carry the same stale text.

## Suggested TODO.md state

- Tick F1, F3 and L4.
- Rewrite or close L3.
- Add one new "Fix" item: refresh the Confluence overview pages (the Known
  gaps list, API status, the `driver-lifecycle.md` reference and the
  `testName` convention).
- Keep F2, F4, L1, L2 and L5 open.
