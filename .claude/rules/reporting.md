---
paths:
  - "src/test/java/reporting/**"
  - "src/test/resources/META-INF/services/**"
  - "run-tests-and-report.cmd"
  - "pom.xml"
---
# Description

Rules for the test reports: how the Allure report is built and how a
screenshot is added when a UI test fails. The agent gets this file
automatically when it opens reporting code, the report script or pom.xml.

# Allure report

- `mvn allure:report` builds the local Allure HTML report from
  `target/allure-results/`. It is a standalone goal, not bound to any Maven
  lifecycle phase, specifically so a failing test does not stop the build before
  the report step runs. `run-tests-and-report.cmd` runs a test command followed
  by `mvn allure:report` on its own line (not `&&`), so the report regenerates
  whether the run passed or failed, and also fires automatically from
  `AllureReportListener` on any `mvn test`/IDE-native run. The report lands in
  `allure-report/report-<timestamp>/` — a single-file `index.html` (Allure 3
  `singleFile`), outside `target/` so `mvn clean` never wipes it. Override the
  destination per run with `-Dallure.report.directory=<path>`.

# Screenshot on failure

- `ScreenshotOnFailureListener` attaches a PNG to Allure on `onTestFailure`,
  for classes extending `BaseUITest` only. API and BDD failures are skipped
  (BDD: logged nothing, since Cucumber's own `@After` hook already closed the
  driver before TestNG sees the failure; plain API classes: skipped silently).
  A failure inside the screenshot capture itself is caught and logged, never
  rethrown — it must never mask the real test failure. BDD UI scenarios
  getting no screenshots is a known, accepted gap (see TODO.md); fixing it
  means capturing in `UiHooks`' `@After`, not this listener.
