# Convention review of the current diff — 2026-10-11 14:37

Reviewer: convention-reviewer agent (read-only). Checked against CLAUDE.md,
`.claude/rules/testing.md`, `.claude/rules/api.md` and the Confluence page
"Bookstore test cases" (page id 7274499, BS-001 to BS-014).

Result: 0 rule violations, 1 spec mismatch, 2 other problems.

## Rule violations

None found.

- BS-014 follows testing.md: priority 14, group `regression`, the ID is used
  only once, hard Asserts with behaviour messages, and the user registry is a
  ThreadLocal cleaned in `@AfterMethod(alwaysRun = true)`.
- The new constants are in `testing.testdata.BookstoreTestData`, as CLAUDE.md
  requires.
- The diff also changes `.claude/agents/convention-reviewer.md` and
  `.claude/skills/new-api-test/SKILL.md`. That is not a test-code violation;
  presumably the user made these edits.

## Spec mismatches

- `src/test/java/api/tests/BookApiTests.java:452`, BS-014: the spec says
  "Status code 300" but the test asserts 400. Code `1205`, the message and the
  empty books list all match the spec. 300 is probably a typo on the Confluence
  page, because the matching BS-008 spec says 400. Nothing was changed.
- BS-008 (the refactored test, around line 272) still matches its spec. Only
  the literals moved into constants.

## Other problems

- `src/test/java/api/tests/BookApiTests.java:437`: the spec title is "Add an
  empty book", but the test name is "add book with empty ISBN is rejected".
  "Rejected" says more than the spec does. Low severity.
- Rules that look missing or unclear:
  - CLAUDE.md does not say whether test names must match the spec title.
  - api.md requires "status, then schema, then deserialisation", but there is
    no error-response schema. The new test, like the existing ones, goes
    straight from the status check to deserialising `ErrorResponse`. It is
    unclear whether a schema is expected for error responses.
