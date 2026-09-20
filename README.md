# uiandapitesting

A practice project for working through AI-assisted testing tools and
workflows. Full documentation, including architecture and conventions, is
in Confluence: https://artemu666.atlassian.net/wiki/spaces/Uiandapite/overview

## Stack

- Java 17
- Maven
- TestNG 7.12.0
- Selenium Java 4.45.0 (+ WebDriverManager 6.3.4 for driver binaries)
- RestAssured 6.0.0
- SLF4J 2.0.18 + Log4j2 2.26.0

## Prerequisites

- JDK 17 installed, with `JAVA_HOME` pointing at it. Maven resolves the JDK
  through `JAVA_HOME`, not through whatever `java` is on `PATH` — on a
  machine with multiple JDKs installed, the build fails if `JAVA_HOME`
  points at the wrong one.

## Running

```
mvn compile
```

mvn test


Runs the suite defined in `testng.xml`.

To run a single class:

mvn test -Dtest=<ClassName>


## Parallel execution

Suite files for scoped or parallel runs (sandbox, API-only, UI-only) live
in `src/test/resources/suite/`. They are not wired into `mvn test` (which
still uses the root `testng.xml`) — run them directly via an IDE run
configuration pointing at the file.

`WebDriver`, `TextBoxSteps`, `CheckBoxSteps` and `WebTablesSteps` in
`BaseUITest` are held in `ThreadLocal`, so `parallel="methods"` in a suite
file is safe for the UI tests — each thread gets its own browser session.

## Running against Selenium Grid

UI tests can run against a local Selenium Grid instead of a locally
launched browser. Grid config lives in
`src/test/resources/selenium-grid/docker-compose.yml`.

Start Grid (requires Docker Desktop running):

docker-compose -f src/test/resources/selenium-grid/docker-compose.yml up -d


Check nodes are registered: http://localhost:4444/ui/#/sessions

Stop Grid:

docker-compose -f src/test/resources/selenium-grid/docker-compose.yml down

`WebDriverFactory.createRemoteDriver()` connects to Grid at
`http://localhost:4444/wd/hub`; swap it in for `createDriver(...)` in
`BaseUITest.setUp()` to run against Grid.

## Documentation

https://artemu666.atlassian.net/wiki/spaces/Uiandapite/overview