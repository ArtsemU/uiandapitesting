# API Testing

Child page of the project overview. This area does not exist yet — this
page records the starting point, not a design.

## Intended target

restful-booker is the intended target for API tests.

## What is already in place

RestAssured 6.0.0 is declared in `pom.xml` as a test-scope dependency. A
repository-wide search finds no other trace of it: no import of
`io.restassured.*`, no `given()`/RestAssured usage, and no test class that
exercises restful-booker or any other API. The dependency has been added
and nothing has been built on top of it.

## What is not in place

No API test classes, no request/response models, no base class, and no
package for API code exist anywhere in the repository. `testng.xml` lists
only UI test classes.

## Open questions

These need answering before the first API test is written, not guessed at
here:

- Where API models (request/response bodies) will live.
- Whether the Steps layer used for UI tests applies to API tests in some
  form, or whether API tests need their own structure entirely.
- What test ID prefix API tests will use, and whether it follows the same
  `XX-000` format as the UI tests.
