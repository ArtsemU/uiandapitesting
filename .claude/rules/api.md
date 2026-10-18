---
paths:
  - "src/test/java/api/**"
  - "src/test/resources/schemas/**"
  - "src/test/java/bdd/**"
---

# Description

Rules for the API part of the project: the code that sends
requests to the Bookstore API, and the API tests. The agent gets this
file automatically when it opens an API file.

# API conventions

## Layer boundaries

- **API only:** clients return the raw Response. They do not deserialise and do
  not check status codes. Tests and glue assert on the status, then deserialise
  with `response.as(Model.class)`.

## Test data

- Exception: API usernames are built from the thread name plus a timestamp. The
  demoQA user registry is shared and global, so a fixed name would collide; the
  thread name is what keeps parallel threads apart. The value is never compared
  with a fixed literal; comparing it with the name the test itself sent is
  allowed.

## Assertions

- API schema checks use draft-04 JSON Schema only — the RestAssured validator
  silently ignores keywords from newer drafts. Order per response: status, then
  schema, then deserialisation.
- The schema step applies to responses that have a schema in schemas/, and
  only in the test that owns the contract check (BS-001); other tests go
  from the status check straight to deserialisation. Error responses go
  straight from the status check to deserialising ErrorResponse.
- Tests for empty, null or missing fields check the request body in the
  log after the first run: it must contain exactly the value the spec
  describes. An empty string, null and a missing field are three different
  cases.

## Reporting

- API request/response logging goes through the `AllureRestAssured` filter,
  wired once into the shared `RequestSpecBuilder` in `api.ApiSpec`. Individual
  tests never add their own request/response logging. The `Authorization`
  header is redacted in the attachment; request/response bodies are not
  (`createUser`/`generateToken` bodies carry the password/token in plain text)
  — accepted, since both already appear elsewhere: the password in the config files
  `config/api*.properties` (`ApiLoggingFilter` masks it in the console log), the
  token in the console log (the `generateToken` response body is not masked).
