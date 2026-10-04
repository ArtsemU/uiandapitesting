# uiandapitesting

A practice project for test automation with AI coding agents. It automates the
public demo site [demoQA](https://demoqa.com): UI tests for several of its pages
(Selenium) and API tests for its Bookstore API (RestAssured), built with Java 17,
Maven and TestNG.

The repository also holds experiments around that core — BDD with Cucumber,
JSON Schema validation, stubs and mocks, load tests, parallel runs, reporting
and CI/CD pipelines.

## Running

Requires JDK 17, with `JAVA_HOME` pointing at it.

```
mvn test
```

Runs the main suite defined in `testng.xml`.

## More

- Documentation: https://artemu666.atlassian.net/wiki/spaces/Uiandapite/overview
- Conventions and architecture for AI coding agents: [CLAUDE.md](CLAUDE.md)