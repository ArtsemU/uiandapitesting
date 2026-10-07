# Rule findings — ci.md and reporting.md (2026-10-08 01:08)

Scope: the rules moved into `.claude/rules/ci.md` and `.claude/rules/reporting.md`,
checked against `.github/workflows/*.yml`, `run-tests-and-report.cmd`, `pom.xml`,
`src/test/java/reporting/*`, `src/test/resources/META-INF/services/*`, the suite XMLs
those workflows run and `docs/TODO.md`. Nothing was fixed.

No violations of "Any UI suite on CI (no display) must pass `-Dheadless=true`":
every UI/BDD step in `ci-workflow.yml` and `nightly.yml` passes it, and the steps
without it run API-only or sandbox suites (`cicd_simple_commit.xml` →
`api.tests.BookApiTests`, `cicd_syntetic_tests.xml` → `sandbox.tests.CowTest`,
`api-parallel-testng.xml` → `api.tests.BookApiTests`).

## Findings

| # | File : line | Rule (file) | What is wrong |
|---|---|---|---|
| 1 | `pom.xml:25` | "The report lands in `allure-report/report-<timestamp>/`" (reporting.md) | The default `allure.report.directory` is `${project.basedir}/allure-report`, so a plain `mvn allure:report` (the command the rule names, and the one all three CI jobs run) writes straight into `allure-report/`, not a `report-<timestamp>/` subfolder. Only `run-tests-and-report.cmd:13-15` and `AllureReportListener.java:64-65` pass a timestamped directory. |
| 2 | `src/test/java/reporting/AllureReportListener.java:52-55` | "...also fires automatically from `AllureReportListener` on any `mvn test`/IDE-native run" (reporting.md) | The listener returns early when the `CI` environment variable is `"true"`, so it does not fire on any GitHub Actions run. The rule says "any" with no exception. |
| 3 | `src/test/java/reporting/AllureReportListener.java:33-35, 95-119` | reporting.md (Allure report bullet) — not covered | After each report, the listener deletes all but the newest 10 `report-*` folders (`-Dallure.report.keep`). The rules don't mention this. Anyone reading only the rule would expect old reports to stay. |
| 4 | `docs/TODO.md:33-34` | "BDD UI scenarios getting no screenshots is a known, accepted gap (see TODO.md)" (reporting.md) | The only screenshot entry in TODO.md is the open item "Screenshots on failure", which predates the listener and says nothing about BDD or `UiHooks`. The gap the rule points to is not recorded there. The reference also says `TODO.md`, but the file is at `docs/TODO.md`; there is none at the root. |
| 5 | `docs/TODO.md:30-31` | reporting.md (Allure report bullet) | The TODO item "Reporting. Nothing beyond console output and surefire XML. Decide between Allure, ExtentReports or the surefire HTML report." is still open, which contradicts the Allure setup the rules describe. |
| 6 | `.github/workflows/ci-workflow.yml:55-57` | "PRs and merges into `master` additionally run the broader regression suites and the BDD suite" (ci.md) | The "Run PR-only suite" step (`cicd_syntetic_tests.xml`, the sandbox `CowTest`) runs on pull requests only, not on merges into `master`. It is also not a regression suite. ci.md does not describe this step. |
| 7 | `.github/workflows/ci-workflow.yml:47, 90` | ci.md (description of `simple_check` / `bdd`) — style | `cache: 'maven'   # <-- added` is a leftover edit marker comment, in both jobs. No rule names it directly; listed because the Cleanup rule in CLAUDE.md covers debug fragments. |

Findings: 7.
