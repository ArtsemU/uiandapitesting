# Convention review: BDD step definitions and CI workflow

Date: 2026-10-10 00:34
Reviewer: convention-reviewer subagent (read-only, no files changed)

Files reviewed in full:
- `src/test/java/bdd/CollectionStepDefs.java`
- `src/test/java/bdd/UserStepDefs.java`
- `.github/workflows/ci-workflow.yml`

Rules checked: CLAUDE.md, bdd.md, api.md, testing.md (Java files), ci.md (workflow).
ui.md and reporting.md do not cover these files.

## Rule violations

None confirmed.

Checks that passed:
- ci.md, `-Dheadless=true` on CI UI suites: `ui_smoke`, `ui_regression` and `bdd` pass it. `cicd_simple_commit` is API only. `cicd_syntetic_tests` runs sandbox `CowTest`. Neither needs the flag.
- ci.md, two `if: always()` Allure steps at the end of each test job, artifact `allure-report-<job name>`: both `simple_check` and `bdd` have them, and the artifact names match the job names.
- bdd.md, assertions only in `Then` steps: assertions sit in `Then` steps or in helpers that `Then` steps call. `register()` has none.
- testing.md / api.md assertions: every assertion is a hard `Assert` with a behaviour message, and each checks the status before deserialising.
- CLAUDE.md, builder objects passed whole: `Book` is not built with a builder, so `addBookToUser(userId, isbn, token)` is fine.
- bdd.md, state shared only through the PicoContainer scenario context: both classes use constructor injection and hold no static state.

## Other problems and uncertain items

1. **`CollectionStepDefs.java:22-31`. Login handling inside a collection `Then` step.**
   The step `the user can see their empty collection` checks the token-generation response and calls `context.setToken(...)`, then asserts the collection is empty. The login `When` is in `UserStepDefs`.
   Rules: bdd.md "organised by domain concept" and "Actions never go into `Then` steps". Storing a token is arguably not an action.
   Fix if the rule is meant strictly: move the token check and `setToken` into the login `When`, or into a `Then` in `UserStepDefs`.

2. **`CollectionStepDefs.java:66-75`. Private helpers `assertCollectionIsEmpty()` and `readUser()`.**
   Rule: CLAUDE.md, "No helper methods: anything reusable belongs in the steps layer." It is unclear whether this covers Cucumber glue.
   Fix if it does: move `readUser` into `ApiSteps`. The assertion helper cannot move there, because assertions must stay in `Then` steps.

3. **`UserStepDefs.java:78-88`. Private `register()` helper with logic.**
   It checks the status, deserialises the response and records the user for cleanup. It branches on `statusCode() == 201` without asserting first, so a `Then` step can assert the 400 case. That is a deliberate exception to api.md "assert on the status, then deserialise", but the rule does not provide for it.
   Same helper-method ambiguity as item 2.

4. **Both Java files use RestAssured `Response` (`statusCode()`, `as(...)`).**
   bdd.md forbids "RestAssured calls" in glue. api.md requires glue to assert the status and then deserialise with `response.as(Model.class)`. The two rules conflict. The code follows api.md.
   Proposed bdd.md wording: "RestAssured `Response` methods `statusCode()` and `as()` are allowed in glue; no request building."

5. **Inline status codes and error code `"1300"`.**
   Status literals appear at `CollectionStepDefs.java` lines 25, 43, 60, 73 and `UserStepDefs.java` lines 39, 48, 67, 72. `"1300"` is at `UserStepDefs.java:50`. No rule forbids them, and `BookApiTests` does the same. `"1300"` is not shared with `BookApiTests`, so the bdd.md shared-testdata rule may not apply. No change needed.

6. **`ci-workflow.yml:20` and `:63`. Leftover `# <-- added` comments.**
   These look like edit markers (CLAUDE.md Cleanup, "debug fragments"). Fix: delete them.

7. **`ci-workflow.yml:39`, `:43`, `:71`, `:75`. The Allure steps can fail a job.**
   ci.md says these steps "must not change whether the job passes or fails". There is no `continue-on-error: true`, so a failing `mvn allure:report` (for example, no results after a compile failure) would fail a job whose tests passed.
   Fix if the rule is strict: add `continue-on-error: true` to the report step. Otherwise, clarify ci.md.

8. **`ci-workflow.yml:43` and `:75`. `actions/upload-artifact@v7`.**
   The other actions are on `@v4`. The reviewer could not verify that the v7 tag exists. Please confirm.

9. **`ci-workflow.yml:3-5`. No nightly trigger.**
   The ci.md Description says pipelines run "on every push, pull request and at night", but this file has no `schedule:` trigger. Either another workflow handles the nightly run, or the description is out of date.

10. **`ci-workflow.yml:22-34`. Four `mvn test` runs, one Allure report.**
    This is allowed by ci.md. Noted only because all four runs write into the same `allure-results`.

## Rule gaps and ambiguities (proposed for the owner)

- CLAUDE.md "No helper methods" does not say whether it covers BDD glue.
- bdd.md "no RestAssured calls" conflicts with api.md "glue asserts status, then deserialises" (wording proposed in item 4).
- The ci.md Description mentions a nightly run that this workflow does not have.
- ci.md "must not change whether the job passes or fails" does not say what happens when the report step itself fails.
