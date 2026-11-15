# uiandapitesting

[![CI](https://github.com/ArtsemU/uiandapitesting/actions/workflows/ci-workflow.yml/badge.svg)](https://github.com/ArtsemU/uiandapitesting/actions/workflows/ci-workflow.yml)
[![Nightly](https://github.com/ArtsemU/uiandapitesting/actions/workflows/nightly.yml/badge.svg)](https://github.com/ArtsemU/uiandapitesting/actions/workflows/nightly.yml)

A test automation framework for the public demo site [demoQA](https://demoqa.com):
UI tests (Selenium) and API tests for its Bookstore API (RestAssured), on
Java 17, Maven and TestNG.

It is built almost entirely by an AI coding agent (Claude Code) working under
explicit, versioned rules, see [How the AI agent works here](#how-the-ai-agent-works-here).

## What it covers

- **UI:** Page Object model, layered Tests → Steps → Pages; Text Box, Check
  Box and Web Tables pages.
- **API:** Bookstore API clients, models and steps; JSON Schema validation of
  responses.
- **BDD:** Cucumber 7 scenarios for API and UI, sharing the steps layer with
  the TestNG tests.
- **Stubs and mocks:** the book catalogue stubbed with Mockito (Java level) and
  WireMock (HTTP level), switchable per run.
- **Load tests:** one flow in JMeter DSL and Gatling, for comparison.
- **Parallel runs:** thread-safe test classes (`ThreadLocal` state) under
  `parallel="methods"`.
- **Reporting:** Allure, with request/response attachments and screenshots on
  UI failure.
- **CI/CD:** GitHub Actions on push, pull request, merge and nightly; headless
  UI runs; failing suites block the merge.

## Running

Requires JDK 17, with `JAVA_HOME` pointing at it.

| Command | What it does |
|---|---|
| `mvn test` | Runs the main suite (`testng.xml`) |
| `mvn test -DsuiteXmlFile=src/test/resources/suite/<name>.xml` | Runs any other suite |
| `mvn test -Dheadless=true -Dbrowser=EDGE` | UI options; default is headed Chrome |
| `mvn allure:report` | Builds the Allure report |

## How the AI agent works here

The agent does not work from ad-hoc prompts. Its behaviour is defined in the
repo and enforced where possible:

- **[CLAUDE.md](CLAUDE.md)**: architecture, conventions and working
  agreements, such as when to decide alone and when to stop and ask.
- **Path-scoped rules** in [`.claude/rules/`](.claude/rules): separate rules
  for tests, UI, API, BDD, CI and reporting, loaded only when the agent works
  in that area.
- **Permissions** in [`.claude/settings.json`](.claude/settings.json): the
  agent cannot write to git or edit its own rules; changing `pom.xml` needs
  approval.
- **Review subagent** ([`convention-reviewer`](.claude/agents/convention-reviewer.md)):
  an independent read-only reviewer that checks each change against the rules
  and the test case specs.
- **Skills** in [`.claude/skills/`](.claude/skills): repeatable procedures,
  e.g. write a new API test from its spec, or bring a test in line with an
  updated spec.
- **Hook:** a compile gate that sends the agent back to fix its work if the
  code does not compile.

Decisions are recorded as ADRs in [`docs/decisions/`](docs/decisions).

## More

- Test case specifications and project docs: [Confluence](https://artemu666.atlassian.net/wiki/spaces/Uiandapite/overview)
