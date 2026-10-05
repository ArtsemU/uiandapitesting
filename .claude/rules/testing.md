---
paths:
  - "src/test/java/**"
  - "src/test/resources/bdd/**"
---

# Test conventions

## Test configuration methods

- Test configuration methods (`@BeforeMethod` / `@AfterMethod`) carry
  `alwaysRun = true`: group-filtered suites skip them otherwise, and for cleanup
  that failure is silent.

## Test identifiers

- Every test carries `@Test(priority = N, testName = "XX-000: short description",
  groups = {"smoke"|"regression"})`, description under ~60 characters.
- `priority` is the numeric part of the ID (`BS-010` → `10`).
  `priority == 1` → `smoke`, everything else → `regression`, per functional area.
- `XX` is the functional area, not the technology: `TB` Text Box, `CB` Check
  Box, `WT` Web Tables, `BS` Books Store. `000` is a three-digit number.
- Test IDs are never reused, even after a test is deleted.
- Exception: tests in the `performance` package reuse the ID of the functional
  test case they put under load, and use `groups = {"performance"}`, which keeps
  them out of `smoke` and `regression` runs.

## Test data

- Fixed and deterministic. No random values, no Faker; vary multiple records with
  a counter.
- UI-only test data naming: see `.claude/rules/ui.md` (loads when working on files under `src/main/java/ui/`, `src/main/java/factory/` or `src/test/java/testing/`).
- Exception: API usernames are built from the thread name plus a timestamp. The
  demoQA user registry is shared and global, so a fixed name would collide; the
  thread name is what keeps parallel threads apart. The value is never asserted
  on.

## Assertions

- UI tests use `SoftAssert` for result checks. API tests use hard `Assert`
  throughout. Preconditions use hard `Assert` in every layer — if setup did not
  happen, the test must stop.
- Every assertion carries a failure message stating which behaviour is broken,
  not the values.
- API schema checks use draft-04 JSON Schema only — the RestAssured validator
  silently ignores keywords from newer drafts. Order per response: status, then
  schema, then deserialisation.

## Parallel safety

- Suites run with `parallel="methods"`: TestNG shares one test-class instance
  across threads. Mutable instance fields in test classes must be thread-confined
  (`ThreadLocal`, cleared in `@AfterMethod(alwaysRun = true)`). A thread-safe
  collection is not enough — it prevents corruption, not cross-test interference.
