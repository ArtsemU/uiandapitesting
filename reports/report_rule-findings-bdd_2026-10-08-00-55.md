# Rule findings — BDD code vs `.claude/rules/bdd.md`

Date: 2026-10-08 00:55
Scope: files matched by the `bdd.md` globs — `src/test/java/bdd/**` (12 files)
and `src/test/resources/bdd/**` (4 feature files). Checked against the rules
moved into `bdd.md`. Nothing was fixed.

## Findings (4: 2 open, 2 resolved)

| # | File:line | Rule | What is wrong |
|---|---|---|---|
| 1 | `src/test/java/bdd/CatalogueStepDefs.java:22`, `:24` | "Assertions only in `Then` steps" | `@Given("the bookstore catalogue has books")` asserts status 200 and a non-empty catalogue. Note: `testing.md` says "Preconditions use hard `Assert` in every layer", so the two rules conflict for precondition checks in `Given` steps. One of them needs an explicit exception. **Status: resolved — rule clarified** (bdd.md, 2026-10-08). |
| 2 | `src/test/java/bdd/CollectionStepDefs.java:22–28` | "Actions never go into `Then` steps" | `@Then("the user can see their empty collection")` finishes the login: it deserialises the token and stores it with `context.setToken(...)` (line 28). The following `When` steps (add/remove book) depend on that state, so the `Then` does setup work, not just verification. **Status: open.** |
| 3 | `src/test/java/bdd/UserStepDefs.java:50` (and `src/test/java/api/tests/BookApiTests.java:432`) | "Expected values shared by both live in `testing.testdata`" | Error code `"1300"` for BS-013 is a literal in both the TestNG test and the glue. `BookstoreTestData` has no constant for it, unlike `PASSWORD_RULES_MESSAGE` and `USER_NOT_FOUND_MESSAGE`, which are already shared. **Status: open.** |
| 4 | `src/test/java/bdd/ApiHooks.java:14`, `src/test/java/bdd/UiHooks.java:9` | "No static fields in glue or hooks" | `private static final Logger log`. This breaks the rule as written. The rule probably targets mutable state, and a constant logger is harmless, so the rule wording may need an exception instead of a code change. **Status: resolved — rule clarified** (bdd.md, 2026-10-08). |

## Checked and compliant

- Cucumber 7.x via `cucumber-testng` + PicoContainer, all versions from
  `cucumber-bom` (`pom.xml:34–35`, `7.34.9`; artifacts at 208/213/218 have no
  version).
- Tags: every scenario has its ID, `@smoke`/`@regression` (BS-001 and WT-001
  smoke, BS-013 and TB-002 regression, so priority 1 is smoke), and `@api`/`@ui`.
- One `When` per scenario; BS-001 is the end-to-end journey and alternates.
- Cucumber Expressions only (`{string}`); no regular expressions.
- Step definitions call `*Steps` / `ApiSteps` only; no locators, `WebElement`
  or RestAssured calls in glue. (Driver creation lives in `UiSession` via
  `WebDriverFactory`, not in step defs.)
- UI `Then` steps create their own `SoftAssert` and call `assertAll()`
  (`TextBoxStepDefs`, `WebTableStepDefs`); API `Then` steps use hard `Assert`.
- State between steps only through `ScenarioContext` / `UiSession`
  (PicoContainer-injected); no mutable static fields.
- Step definition classes organised by domain concept (Catalogue, Collection,
  User, Navigation, TextBox, WebTable).
- API cleanup in `@After("@api")` (`ApiHooks:29`); failures are logged, never
  rethrown.

## Observations (not findings)

1. Assertions in private helpers. `CollectionStepDefs.assertCollectionIsEmpty`
   (line 66–69) and `readUser` (71–75) contain assertions. They are only called
   from `Then` steps, so the rule holds in effect, but not literally.
2. `UserStepDefs:69`: the `Then` step calls `context.forgetCreatedUser(...)`.
   This is cleanup bookkeeping, commented and needed, so the hook does not
   delete twice. It changes context state in a `Then`, which is borderline
   under the same rule as finding #2.
3. BS-013 invalid passwords are listed both in the feature `Examples` table and
   in the `BookApiTests` data provider. Gherkin cannot reference Java
   constants, so this duplication is unavoidable with the current design.
4. WT-001 step text "through the registration form" names a UI element. It is
   arguably still behaviour (the spec's own wording), not mechanics.
