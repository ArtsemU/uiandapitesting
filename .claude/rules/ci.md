---
paths:
  - ".github/workflows/**"
---
# Description

Rules for the GitHub Actions pipelines that run the tests automatically on
every push, pull request and at night. The agent gets this file
automatically when it opens a workflow file.

# UI runs on CI

Any UI suite on CI (no display) must pass `-Dheadless=true`.

# CI/CD

GitHub Actions workflows under `.github/workflows/`:

- `ci-workflow.yml` — runs on every push and pull request. Push and PR both run
  a fast API + UI smoke check (job `simple_check`); PRs and merges into `master`
  additionally run the broader regression suites and the BDD suite (job `bdd`).
- `nightly.yml` — scheduled (cron) and manually dispatchable; runs the full
  regression suite (job `full_regression`), independent of any push/PR/merge
  event.
- `cd-simulate.yml` — manual, simulated deploy pipeline through qa1 → stage →
  prod GitHub Environments (real approval/wait-timer gating, placeholder deploy
  step). No tests run here; untouched by the reporting work below.

Every job in `ci-workflow.yml` and `nightly.yml` ends with two `if: always()`
steps, after its last test step: generate the Allure report
(`mvn allure:report`) and upload it via `actions/upload-artifact` (artifact
names `allure-report-simple_check`, `allure-report-bdd`,
`allure-report-full_regression`; `retention-days: 2`). This runs regardless of
pass/fail and does not change surefire's own failure behaviour — the exit code
that gates branch protection is untouched.
