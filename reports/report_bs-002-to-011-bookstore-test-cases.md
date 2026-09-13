2026-09-11

# BS-002 to BS-011 "Bookstore test cases" — blockers to automation

Source: Confluence space `Uiandapite`, pages "Bookstore test cases" (BS-002
through BS-011), "Test plan", and "Examples of API calls". Reference
implementation read: `BookApiTests.addBookToUserE2ETest` (BS-001) and its
supporting classes (`AccountClient`, `BookstoreClient`, `ApiSteps`, and the
`api.models` package). Analysis only — no design or code proposed.

## Carried over from BS-001, and now touching more cases

`report_bs-001-add-book-to-user.md` flagged two unresolved questions in the
Test plan. Both recur here and are no longer isolated to one test:

- **Username uniqueness strategy.** The Test plan still has an open
  parenthetical on how to avoid colliding with the shared/global user
  registry. Every one of BS-002 through BS-011 starts from "a user has been
  created," so this same open question now underlies ten test cases, not
  one.
- **ISBN selection: position vs. fixed value.** BS-009 ("one book from the
  catalogue has been added") and BS-010 ("an ISBN from the catalogue") both
  need a real ISBN and neither names one. The Test plan says ISBNs are
  "picked by ISBN, never by position," which rules out reusing BS-001's
  "first element of the collection" approach here — but no fixed ISBN
  constant is given anywhere to use instead.

## New: no cleanup step in any of BS-002–BS-011

BS-001 ends by deleting the user it created (step 10) and confirming the
delete (step 11). None of BS-002 through BS-011 has an equivalent step —
every one of them creates at least one user (BS-007 creates two) and simply
stops after its assertions. Concretely:

- BS-002, BS-005, BS-006, BS-008, BS-009, BS-010, BS-011: one user created,
  never deleted.
- BS-007: two users created, neither deleted.

Given the Test plan's own unresolved point about the registry being shared
and global, this is a real gap, not a nitpick: whether these ten tests
should each end with a delete-user call is not stated either way. CLAUDE.md
is explicit that scenarios not in the spec should not be invented — so
adding teardown steps that aren't written into the test cases isn't a free
choice to make silently, even though skipping it means the registry grows by
at least eleven users every run indefinitely.

## Missing fixed test data

None of these values are given anywhere on the three pages read, so each
would require inventing one:

- **BS-003 / BS-004 — "empty field."** The step says "call create user
  request with empty userName [/password] field," but doesn't show the
  request body. This is at least three different requests: the key present
  with value `""`, the key present with value `null`, or the key omitted
  from the JSON entirely. `UserCredentials` has no `@JsonInclude` annotation,
  so a `null` field currently serialises as `"userName":null` rather than
  being dropped — whether that's equivalent to what the demoQA API treats as
  "empty" isn't established by any example on these pages.
- **BS-006 — "invalid token."** No example invalid-token string is given.
  Garbage string, malformed JWT, and well-formed-but-unsigned JWT are all
  plausible reads of "invalid," and CLAUDE.md's fixed/deterministic test
  data rule means picking one is inventing a constant, not reading one.
- **BS-008 — ISBN not in the catalogue.** No such ISBN is given. The
  catalogue itself (8 books) is fully listed on "Examples of API calls," so
  an out-of-catalogue value would have to be made up rather than sourced
  from a spec.
- **BS-009 / BS-010 — a real catalogue ISBN.** Same gap as the carried-over
  point above: no fixed ISBN constant exists on any of the three pages.

## Gaps found in the reference code, not the spec

These aren't spec ambiguities — they're places where the literal step in a
test case can't be produced by what `AccountClient` / `BookstoreClient` /
`ApiSteps` currently expose, discovered by reading them as the reference
implementation:

- **BS-005 — "no `Authorization` header."** `AccountClient.getUser(userId,
  token)` unconditionally adds `.header("Authorization", "Bearer " +
  token)`. There's no call path today that omits the header rather than
  sending a Bearer value (empty, blank, or `null`) — those are different
  requests from what BS-005 describes ("with no `Authorization` header" at
  all).
- **BS-011 — remove all books.** `BookstoreClient.removeAllBooks(userId,
  token)` already exists at the client layer, but `ApiSteps` has no wrapper
  method for it yet (unlike `addBook`/`removeBook`, which `ApiSteps` already
  wraps). Mechanical, not a design question — noted since it means BS-011
  isn't reachable through the steps layer as it stands.

## Minor / documentation notes

- CLAUDE.md's "Current prefixes" list (`TB`, `CB`, `WT`) doesn't mention
  `BS`, even though `BS-001` already exists in code and all of BS-002–011
  use the same prefix. Flagging per CLAUDE.md's own rule that the file is
  edited manually, not by Claude.
- BS-001 through BS-004 are formatted as bold run-in headings; BS-005
  onward switch to `##` headings. Cosmetic only — no effect on content.
- BS-003/BS-004 write the error code as `1200` with no quotes, while every
  other test case and the "Examples of API calls" page always show `code`
  as a quoted string (`"1204"`, `"1300"`, `"1207"`, `"1205"`) matching
  `ErrorResponse.code` (`String`). Almost certainly still a string — flagged
  only because it's the one place the formatting is inconsistent.

## Bottom line

The single biggest blocker is new to this batch: whether BS-002–BS-011 are
meant to leave created users behind (undefined, and in tension with the Test
plan's own unresolved registry question) or whether teardown was simply left
off the written steps. Close behind are the two carried-over open questions
from BS-001 — ISBN selection strategy and username uniqueness — which now
apply across most of these ten cases instead of just one. The missing fixed
values (empty-field shape, invalid token, out-of-catalogue ISBN, in-catalogue
ISBN) are secondary but still block writing deterministic test data as
CLAUDE.md requires. These should be settled before writing any code.
