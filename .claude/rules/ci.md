---
paths:
  - ".github/workflows/**"
---
# Description

Rules for the GitHub Actions pipelines that run the tests automatically on
every push, pull request and at night. The agent gets this file
automatically when it opens a workflow file.

# CI rules

- Any UI suite on CI (no display) must pass `-Dheadless=true`.
- Every job that runs tests ends with two `if: always()` steps after
  its last test step: build the Allure report (`mvn allure:report`) and
  upload it with `actions/upload-artifact` as `allure-report-<job name>`.
  Keep both steps in every such job. Both carry `continue-on-error: true`,
  so a report failure never fails the job.
