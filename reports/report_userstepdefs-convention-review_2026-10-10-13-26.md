# Convention review — `src/test/java/bdd/UserStepDefs.java`

Date: 2026-10-10 13:26. Reviewer: `convention-reviewer` subagent (read-only), checked against `CLAUDE.md` and `.claude/rules/`.
The main session spot-checked both rule violations against the code (line 69 `forgetCreatedUser` sits in a `@Then`; `"1300"` is a literal in both `UserStepDefs.java:50` and `BookApiTests.java:432`). The other findings are the subagent's and were not checked again.

## Rule violations

1. **State change and API call in a `Then`**: `UserStepDefs.java:65-76` (`theUsersAccountCanNoLongerBeFound`)
   - Rule: `bdd.md`: "Actions never go into `Then` steps"; "result checks stay in `Then` steps only".
   - Problem: the `Then` calls `context.forgetCreatedUser(...)` (line 69), which is cleanup bookkeeping. It also makes a new call, `apiSteps.getUserData(...)` (line 71).
   - Fix: move the 204 check and `forgetCreatedUser` into the `When` step "the user deletes their account". After that, the `Then` only reads and asserts.

2. **Shared expected value kept as a literal**: `UserStepDefs.java:50` (`"1300"`)
   - Rule: `bdd.md`: "Expected values shared by both live in `testing.testdata`". `CLAUDE.md` › Where things go.
   - Problem: `"1300"` is also asserted inline in `BookApiTests.java:432`, and `BookstoreTestData` has no constant for it. The message strings are already shared correctly.
   - Fix: add `PASSWORD_RULES_CODE` to `BookstoreTestData` and use it in both places.

## Other problems / ambiguities

1. `:71-75`: it is unclear whether a read-only verification call in a `Then` counts as an "action". `bdd.md` should say.
2. `:57-62`: "the user logs in" stores only the raw response. The token is extracted in `CollectionStepDefs.theUserCanSeeTheirEmptyCollection`, which is a `Then` that also changes state. "the user deletes their account" reads `context.getToken()`, so it depends on that other class's `Then` having run first. Without it, the token is null. This is hidden coupling between classes.
3. `:39,48,67,72`: the status codes are literals. No rule requires constants for them, and `BookApiTests` also uses literals. Not a violation.
4. `:79-88`: the private `register(...)` helper contains logic: a status branch, deserialisation and context bookkeeping. CLAUDE.md's "No helper methods" rule is written for test classes, and it is unclear whether it covers glue. Also, if the 201 response body fails to deserialise, the user is never recorded and is leaked (no cleanup).
5. **Rules contradict each other.** `bdd.md` says glue has "no ... RestAssured calls". `api.md` says "Tests and glue assert on the status, then deserialise with `response.as(Model.class)`". The file follows `api.md`. Suggested wording for `bdd.md`: "no direct request building".
6. `:36-53`: no schema check in the `Then` steps, although `api.md` orders checks as status → schema → deserialisation. `bdd.md` allows a subset of checks, so it is unclear whether a schema check is required in glue.
7. `:31`: the username comes from `UserCredentials.unique()` and only the name is kept. This is allowed by the username exception in `api.md`. It is awkward but legal.
8. "the user is registered with an empty collection" and "the user can see their empty collection" are similar phrasings. They check different things, so this is not a violation of the near-duplicate rule.

## Checked and fine

Hard `Assert` throughout, with no `SoftAssert`. Every assertion has a message that describes the behaviour. Assertions appear only in `Then` steps. State is passed only through the injected `ScenarioContext`. The class is organised by domain concept. Cucumber Expressions are used. Builder objects are passed whole. The `@After("@api")` cleanup hook is present in `ApiHooks` and never fails the scenario. Feature tags (`@BS-001`, `@BS-013`, `@api`) are correct.

## Verdict

Mostly compliant: 2 rule violations and 8 other problems. No files were changed.
