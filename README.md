# uiandapitesting

A practice project for working through AI-assisted testing tools and
workflows. Full documentation, including architecture and conventions, is
in Confluence: https://artemu666.atlassian.net/wiki/spaces/Uiandapite/overview

## Stack

- Java 17
- Maven
- TestNG 7.12.0
- Selenium Java 4.45.0 (+ WebDriverManager 6.3.4 for driver binaries)
- RestAssured 6.0.0 (dependency only — no API tests exist yet)
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

```
mvn test
```

Runs the suite defined in `testng.xml`.

To run a single class:

```
mvn test -Dtest=<ClassName>
```

## Documentation

https://artemu666.atlassian.net/wiki/spaces/Uiandapite/overview
