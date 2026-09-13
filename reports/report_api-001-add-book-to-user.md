2026-09-01

# API-001 "Add book to user" — blockers to automation

Source: Confluence space `Uiandapite`, pages "Bookstore test cases" (API-001) and
"Test plan". Analysis only — no design or code proposed.

## Direct contradiction with the Test plan

- Step 5 of API-001 says "Get isbn from the first element of the collection"
  (picks by position). The Test plan's last line says "Books are picked by ISBN,
  never by position." These two documents disagree on the exact point that
  matters for this test's test data — unresolved whether the ISBN used in
  steps 5–7 is a fixed constant or a runtime lookup.

## Explicitly unresolved in the source docs themselves

- Step 6 has a literal `??need check isbn` in the spec — the author flagged that
  the expected-result assertion for "add book" isn't decided. No field(s) or
  expected value specified.
- The Test plan's Test Data section has an open parenthetical: "the user
  registry is global, so a fixed username will eventually collide. Options — one
  user in config, unique per run, or created and deleted per test." Step 1
  requires a "unique username" but the plan hasn't picked a strategy. This also
  conflicts with CLAUDE.md's "no random values, no Faker" rule for test data — a
  uniqueness strategy that isn't a random value isn't specified anywhere.

## Missing test data

- No password value or password-policy note anywhere. demoQA's Bookstore user
  creation enforces a password complexity rule; the spec gives no fixed password
  to use as test data.
- No fixed ISBN constant, book title, or author value to assert against in
  steps 6–7 (tied to the position-vs-ISBN contradiction above) — "newly added
  book values" in step 7 is not spelled out as concrete expected values.
- No login/username constant given (consistent with the open uniqueness
  question).

## Vague or underspecified expected results

- Step 7: "Field books has newly added book values" doesn't say which fields
  (isbn only? isbn + title + author?) or how strictly to compare.
- Step 11: expected message `"User not found!"` after deleting the user and
  re-calling Get user with the same (now-deleted-user's) token — this is a
  specific demoQA quirk-dependent string that should be verified against the
  live API rather than trusted blindly, since the plan calls out that this
  project excludes some quirky/undocumented behavior elsewhere (token expiry)
  precisely because it's hard to pin down.

## Structural mismatch with CLAUDE.md

- CLAUDE.md says test case pages follow a Preconditions / Steps / Expected
  result structure, and preconditions get hard `Assert` while steps get
  `SoftAssert`. API-001 is a flat 11-step numbered list with no
  precondition/step boundary marked. Likely candidate: steps 1–3 (user
  creation + token + empty-books check) are preconditions, steps 4 onward are
  the actual test — but that's a guess, not something the spec states.
- CLAUDE.md's Test identifiers section says API prefixes are "not assigned
  yet," yet this test case is already labeled API-001 in Confluence. There's
  no defined functional-area prefix (analogous to `TB`/`CB`/`WT`) for Bookstore
  to use in the `@Test(description = "XX-000: ...")` annotation.

## Referenced but absent content

- The Bookstore test cases page's own summary text says "Application
  description, scope and **known issues** are in the Test plan," but the Test
  plan page body as read has no "known issues" section at all. Either that
  section hasn't been written yet or something didn't load — either way, any
  known-quirks context that section was supposed to supply (likely relevant to
  steps 6 and 11) isn't available.

## Bottom line

The two biggest blockers are (1) the position-vs-ISBN contradiction between the
test case and the plan, and (2) the plan's own open question on username
uniqueness strategy — both are marked unresolved by the author, not just
ambiguous on read. These plus the missing password/ISBN test data should be
settled before writing any code.
сдф