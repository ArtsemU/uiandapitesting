# Convention review: current diff

Date: 2026-10-10 14:26. Reviewer: `convention-reviewer` subagent (read-only). It reviewed the diff blind and was not told which changes were planted.

## Reviewer test outcome

| Planted change | Expected | Reviewer result |
|---|---|---|
| `TextBoxPage`: `By.cssSelector("#submit")` field | Violation (`ui.md` › Locators: XPath only) | Caught. Also flagged that the field is unused, duplicates `submitButton` and is not scoped to a container |
| `BookApiTests` BS-008: last hard `Assert` → `SoftAssert` + `assertAll()` | Violation (`testing.md` › Assertions: API uses hard `Assert`) | Caught |
| `CheckBoxTest` CB-002: `groups` removed | Violation (`testing.md` › Test identifiers) | Caught |
| `sandbox/Cow.java`: `By.cssSelector("#anything")` field | Not reported (sandbox is exempt) | Correctly skipped as exempt |

The reviewer caught all three planted violations, skipped the sandbox change as CLAUDE.md requires, and reported no false positives on planted code.

## Rule violations (as reported)

1. `src/test/java/api/tests/BookApiTests.java:280-282` (BS-008): an API test uses `SoftAssert`. Rule: `testing.md` › Assertions, "API tests use hard `Assert` throughout." Fix: revert to a hard `Assert` and drop the import.
2. `src/test/java/testing/ui/CheckBoxTest.java:25` (CB-002): the `groups = {"regression"}` attribute is missing. Rule: `testing.md` › Test identifiers. Group-filtered suites would skip this test.
3. `src/main/java/ui/pages/TextBoxPage.java:32-33`: the new field uses `By.cssSelector`. Rule: `ui.md` › Locators, "Every locator uses `By.xpath()`". The field is also unused, duplicates `submitButton`, and is not scoped to a container.

## Other problems (as reported)

- `.idea/inspectionProfiles/Project_Default.xml` is untracked and not covered by `.gitignore`. It is not part of the planted changes and was already in the working tree. No rule covers it.
- `sandbox/Cow.java` was skipped as exempt.

## Ambiguities raised

- `testing.md`: "API tests use hard `Assert` throughout" already settles the BS-008 case. The reviewer suggests the more explicit wording "API tests never use `SoftAssert`."
- `ui.md` Locators: the rule is clear. It does not mention unused or experimental fields, but the XPath-only rule catches them anyway.

## Verdict (reviewer)

Do not accept as is: three rule breaks. Revert them, and review the `.idea` file separately.
