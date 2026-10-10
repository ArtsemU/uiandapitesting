# Convention review: `CheckBoxTest.java`, `TextBoxTest.java`

Date: 2026-10-10 14:52. Reviewer: `convention-reviewer` subagent (read-only), checking both files in full against `CLAUDE.md`, `testing.md` and `ui.md`.

## Rule violations

None.

## Checked and compliant

- Test IDs and annotations: priority matches the ID number (1 → smoke, 2 and 3 → regression), the area prefixes `TB` and `CB` are correct, and every description is under 60 characters.
- `SoftAssert` is used for the UI result checks. Every assertion message describes the behaviour, not the values.
- Neither class has mutable instance fields or helper methods. Both call Steps only, never Pages.
- Test data comes from `CheckBoxTestData` and `TextBoxTestData` (constants only). The `EXPECTED_OUTPUT_<SCOPE>` naming is followed.
- `BaseUITest` has `alwaysRun = true` on its configuration methods, and both classes extend it.

## Other problems / ambiguities

1. **Inline node labels in `CheckBoxTest`.** "Home", "Desktop", "Documents", "Office", "Downloads" and "Notes" are repeated as literals across tests. No rule says UI *input* labels must live in `testing.testdata`, so this is not a violation, but moving them would be consistent.
2. **`@TB-002` is reused in a BDD feature** (`src/test/resources/bdd/features/text_box/text_box.feature:5`). It has the same ID as `TextBoxTest` TB-002. `testing.md` says "Test IDs are never reused" and makes an exception only for `performance`. A BDD scenario for the same test case is probably intended, but the rules don't say so. Confirmed by the main session.
3. `TextBoxTest.java:7`: `BaseUITest{` is missing a space before the brace. This is a formatting nit, and no rule covers it.
4. `TextBoxTest.java:10`: the method name `setNameOnlyTest` uses a different naming style from the other tests. No rule covers it.
5. The reviewer raised a `SoftAssert` + `assertAll()` "ambiguity" but quoted the failure-message rule for it. This item is weak: the code is compliant either way.
6. The reviewer was unsure about `@Step` coverage in `TextBoxSteps`. **The main session checked it:** 10 public methods (not counting the constructor) and 10 `@Step` annotations. No issue.

## Verdict

Both files follow the rules. The points worth deciding are items 1 and 2.
