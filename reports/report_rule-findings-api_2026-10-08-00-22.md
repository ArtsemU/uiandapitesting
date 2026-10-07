# Rule findings — API code vs `.claude/rules/api.md`

Date: 2026-10-08 00:22
Scope: files matched by the `api.md` globs (`src/test/java/api/**`, 17 files;
`src/test/resources/schemas/**`, 2 files), plus the BDD glue that the moved
"Tests and glue" rule names (`src/test/java/bdd/*`). Checked against the four
rules moved into `api.md`. Nothing was fixed.

## Findings

**0 findings.**

| Rule (api.md) | Checked | Result |
|---|---|---|
| Layer boundaries: clients return raw Response, no deserialise / status check | `AccountClient`, `BookstoreClient`, `ApiSteps` | Compliant. Clients only build and send; `ApiSteps` only forwards Responses. |
| Layer boundaries: tests and glue assert status, then `response.as(...)` | `BookApiTests`, `bdd/*StepDefs`, `bdd/ApiHooks` | Compliant. Every `.as(...)` is preceded by a status assertion (hooks: a status `if` check, which is cleanup, not assertion). |
| Test data: API usernames = thread name + timestamp, never asserted | `UserCredentials.uniqueUserName` (line 50–52), `BookApiTests` BS-007 (`_A`/`_B` suffixes on a unique base) | Compliant. No assertion on username values. |
| Assertions: draft-04 schema; order status → schema → deserialisation | `books-schema.json`, `user-schema.json`; `BookApiTests` lines 74–77, 89–93, 300–303, 345–348 | Compliant. Both schemas declare draft-04; all four schema checks follow the order. |
| Reporting: `AllureRestAssured` wired once in `api.ApiSpec`, Authorization redacted, no per-test logging | `ApiSpec` line 123; grep for `.log()` in `api/` and `bdd/` | Compliant. |

## Observations (not findings)

1. **Glob does not cover BDD glue.** The moved layer-boundary bullet says
   "Tests and glue assert on the status…", but `api.md` loads only for
   `src/test/java/api/**` and `schemas/**`. When editing `src/test/java/bdd/*`
   (API step defs, `ApiHooks`) this rule is no longer in context. Possible fix:
   add `"src/test/java/bdd/**"` to `paths`, or accept the gap.
2. **Username exception also not loaded for its producer's callers outside
   `api/`.** `UserCredentials.unique()` lives under `api/`, so it is covered;
   BDD glue that builds users (`UserStepDefs`) is not — same root cause as #1.
3. **Schema checks are not applied to every response that has a schema.**
   `user-schema.json` is checked at `BookApiTests:92` only; other
   `GET /Account/v1/User` responses (e.g. lines 68–69, 104–105, 277–278) are
   deserialised without it. The rule defines order, not coverage, so this is
   not a violation.
4. **Reporting bullet's justification is partly inaccurate.** It says the
   password and token "already appear elsewhere (console log,
   `api.properties`)". `ApiLoggingFilter` masks `password` fields in the
   console log (line 223–225), so the password appears only in
   `api.properties`; the token does appear unmasked in the console log
   (response body of `generateToken`). Wording is outside this move's scope
   (moves only, no rewording).
5. Out of scope for `api.md` but noticed: many `BookApiTests` assertions use
   messages like `"Expected status : 200"`, which state values rather than
   the broken behaviour (`testing.md`, Assertions, second bullet).
