2026-09-03

# BS-001 "Add book to user" — blockers to automation

Source: Confluence space `Uiandapite`, pages "Bookstore test cases" (BS-001),
"Test plan", and "Examples of API calls". Analysis only — no design or code
proposed.

## Direct contradiction with the Test plan

- Step 5 of BS-001 says "Get isbn from the first element of the collection"
  (picks by position). The Test plan's last line says "Books are picked by
  ISBN, never by position." These two documents disagree on the exact point
  that matters for this test's test data — unresolved whether the ISBN used
  in steps 5–7 should be a fixed constant or a runtime/positional lookup.

## Explicitly unresolved in the source docs themselves

- The Test plan's Test data section has an open parenthetical: "the user
  registry is global, so a fixed username will eventually collide. Options —
  one user in config, unique per run, or created and deleted per test." No
  strategy has been picked. This also conflicts with CLAUDE.md's "no random
  values" rule for test data — a uniqueness strategy that isn't a random
  value isn't specified anywhere.
- No teardown/cleanup strategy is defined for a run that fails partway (e.g.
  an assertion fails between steps 6 and 9). Step 10 (delete user) only runs
  as part of the happy path; if an earlier step fails, a fixed username could
  be permanently consumed in the shared registry for future runs.

## Missing test data

- No password value or password-policy note anywhere in BS-001. The password
  complexity rule (upper/lower/digit/special char, 8+ length) only surfaces
  indirectly, in an unrelated negative example on the "Examples of API calls"
  page (item 11).
- No fixed ISBN constant is given for steps 5–7 (tied to the position-vs-ISBN
  contradiction above).
- No username constant given (consistent with the open uniqueness question).

## Implicit context not stated in the test case itself

- BS-001's numbered steps never say which calls require the Bearer token —
  that's only stated once, in prose, at the top of the "Bookstore test cases"
  page ("Bearer token required: read user, delete user, and all collection
  operations"). The test case isn't self-contained on this point.
- Step 2 (generateToken) doesn't explicitly say it uses the same
  username/password created in step 1 — a reasonable assumption, but not
  stated.

## Documentation defects noticed along the way

- The Test plan's own "Base URL" (`https://demoqa.com/books`) and "Swagger"
  (`https://demoqa.com/profile`) links look like UI page URLs, not an API
  root or Swagger doc. The real API base
  (`https://demoqa.com/Account/v1/...`, `https://demoqa.com/BookStore/v1/...`)
  is only recoverable from the "Examples of API calls" page.
- The create-user response uses `userID` (capital ID) while the get-user
  response uses `userId` (lowercase d) — visible only in the raw JSON on the
  Examples page, not called out anywhere in BS-001.

## Bottom line

The two biggest blockers are (1) the position-vs-ISBN contradiction between
BS-001 and the Test plan, and (2) the Test plan's own open question on
username uniqueness strategy for the shared/global user registry — both
marked unresolved by the author, not just ambiguous on read. These, plus the
missing password/ISBN test data, should be settled before writing any code.

## Note on prior report

`report_api-001-add-book-to-user.md` (2026-09-01) covers this same test case
under its former ID (API-001) and references a `??need check isbn` note and a
missing "known issues" section that no longer appear on the live pages — the
Confluence pages have since been edited. That report is stale; recommend
deleting it once this one is reviewed.
