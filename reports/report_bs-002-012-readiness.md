2026-09-12

# Readiness check: BS-002 through BS-012

Sources re-read fresh for this check: Confluence "API Testing Overview", "Test plan",
"Bookstore test cases" (BS-001–BS-012), "Examples of API calls"; CLAUDE.md at the repo
root; and the reference implementation `BookApiTests.addBookToUserE2ETest` (BS-001)
with `AccountClient`, `BookstoreClient`, `ApiSteps`, and the `api.models` classes.

## Bottom line

**Yes — 3 blockers.** BS-002, BS-003, BS-004, BS-007, BS-008, BS-009, BS-010 and
BS-012 are ready to automate as written, using only what `AccountClient`,
`BookstoreClient` and `ApiSteps` already expose. BS-005, BS-006 and BS-011 each hit a
blocker described below.

---

## Blockers (must be resolved before writing code)

### 1. BS-005 — the client cannot produce a request with no `Authorization` header

BS-005 requires `GET /User/<userID>` sent **with no `Authorization` header at all**.
`AccountClient.getUser(String userId, String token)` unconditionally does:

```java
.header("Authorization", "Bearer " + token)
```

There is no overload or call path that omits the header. Passing `token = null`
does not satisfy the spec either — it sends `Authorization: Bearer null`, a header
that is present with a garbage value, not an absent header. As written, this step
cannot be produced by the current client code (`AccountClient.getUser`). A decision
is needed on how the client should support a headerless call before this test can
be written.

### 2. BS-006 — the precondition needed to hit the stated error is not specified

BS-006 expects code `1207` / `"User not found!"` for `GET /User/<userID>` with an
"invalid userID." That is the same code/message BS-007 gets when a *valid* token
is presented for the wrong user — i.e. it appears to require a technically-valid
bearer token that simply doesn't match the requested userID, not a missing or
garbled one. `1200`/`"User not authorized!"` is the code used elsewhere (BS-005,
BS-012) for a missing or invalid token.

Every other case that depends on token state says so explicitly with a
`Preconditions:` line — BS-005 ("a user has been created and its userID is
known"), BS-007 ("a token has been generated for user A"), BS-012 (same as
BS-005). BS-006 is the only case in the set with no `Preconditions:` section at
all. As written it doesn't say whether a user/token must be created first, or
where the token used in the call should come from. Filling this in without
confirmation would mean inventing a precondition the spec doesn't state — and
guessing wrong (e.g. using no token, or an arbitrary invalid one) produces
BS-012's error, not BS-006's. This needs a stated precondition before the step
can be written correctly.

### 3. BS-011 — no Steps-layer wrapper for "remove all books"

BS-011's step is `DELETE /BookStore/v1/Books?UserId=<userID>`.
`BookstoreClient.removeAllBooks(String userId, String token)` already exists and
matches this call, but `ApiSteps` has no method that calls it — `ApiSteps`
currently wraps `getAllBooks`, `addBookToUser` and `removeBook` only. Per the
project's Tests → Steps → Clients layering (tests call the Steps layer only), this
operation has no path into a test class today. This is the only gap of its kind —
every other client method BS-002–BS-012 need (`createUser`, `generateToken`,
`getUser`, `deleteUser`, `getAllBooks`, `addBook`, `removeBook`) is already wrapped.

---

## Non-blocking notes (can be decided in passing, or safely deferred)

- **BS-008's "ISBN not in the catalogue."** The Test plan's test-data rule says
  "No ISBN is hardcoded, and assertions compare against the book that was fetched,
  not against fixed values" — but that rule is written for the catalogue-driven
  positive flow (BS-001, BS-009, BS-010). There is no way to obtain a
  guaranteed-absent ISBN except to supply a literal that isn't in the catalogue, so
  reading the "no hardcoded ISBN" rule as scoped to the positive-path tests (and
  treating BS-008's fake ISBN as ordinary fixed test data under CLAUDE.md's
  general "fixed and deterministic" rule) resolves this without conflict. Worth a
  one-line confirmation, not a blocker.

- **CLAUDE.md's package layout section predates the API layer.** CLAUDE.md's
  "Package layout" section still lists only `ui.pages` / `ui.steps` / `ui.models`
  under `src/main/java` and `testing.ui` / `testing.testdata` under `src/test/java`
  — it does not mention the `api`, `api.models` or `api.tests` packages that
  BS-001's reference implementation already uses (all under `src/test/java`, which
  is itself a deliberate difference from `ui.models` since API code has no
  main-source caller to compile against). Per CLAUDE.md's own rule ("if a rule is
  missing... say so and propose the wording"): this is a stale-documentation gap,
  not a functional blocker — BS-002–BS-012 should simply follow BS-001's existing
  package placement for consistency. Suggested addition to CLAUDE.md's Package
  Layout section: a bullet noting `api` / `api.models` / `api.tests` live under
  `src/test/java` (unlike `ui.models`), mirroring what BS-001 already does.

- **Cleanup is already settled.** The Test plan's Rules section states cleanup
  (user deletion) happens generically in `@AfterMethod` and is deliberately not
  written into each case (only BS-001 deletes a user as part of its own scenario).
  BS-002–BS-012 never call the delete endpoint themselves, which matches this rule
  — flagging only to confirm it isn't an oversight, not as an open question.

- **Formatting inconsistency, no functional effect.** BS-005 and BS-012 render as
  "Preconditions**:**" (a stray bold-markup artifact) versus "**Preconditions:**"
  elsewhere. Cosmetic only.

- **Test data conventions already reconcile cleanly.** CLAUDE.md's stated exception
  ("Timestamp-based usernames are an accepted exception to the 'no random values'
  rule for API tests... the value itself is never asserted on") matches the Test
  plan's username-generation rule word for word. No conflict to raise.

- **`UserCredentials`, `ErrorResponse`, `AddBook`, `RemoveBook` already support
  every BS-002–BS-012 request shape** (empty-string username/password via the
  builder for BS-003/BS-004; arbitrary/invalid ISBN strings for BS-008/BS-009/
  BS-010; string `code` field on `ErrorResponse` for asserting `"1204"`, `"1205"`,
  `"1206"`, `"1210"`, `"1207"`, `"1200"`, `"1300"`). No model changes needed for
  any of the eight non-blocked cases.
