# Prompt check: "Make the two catalogue stubs switchable and move them out of the tests"

Checked against the code as committed on `addUiTests_19_mocks` (working tree clean).
Nothing was changed. The prompt is runnable; the points below are where it is
ambiguous or contradicts itself, ordered by how much they affect the result.

## Needs a decision before running

### 1. "Exactly like the original" vs. the schema assert (item 3)

Both tests currently contain a precondition that the original tests did not have:

    MatcherAssert.assertThat("Precondition failed: stubbed catalogue does not match the Books schema", ...)

The test body has one code path for both flag states, so this assert is either
always there or never there. With the flag off it would run against the real
catalogue, which is harmless but is not "exactly like the original", and its
message says "stubbed".

Suggested wording: "The schema precondition stays in both tests and runs in both
modes; reword its message so it does not say 'stubbed'."

### 2. "Get an ApiSteps and use it" cannot also give verify + guaranteed shutdown (item 3)

With the flag on, the test must still trigger the "stub was used" check and the
server must stop even on failure. A bare `ApiSteps` cannot carry either. The
test needs a handle from the support class, for example an `AutoCloseable` used
in try-with-resources that exposes `apiSteps()` and `verifyStubUsed()` (a no-op
when the flag is off).

The alternative, doing verify and shutdown in `@AfterMethod`, is worse: it needs
a `ThreadLocal` field in the test class, and a failed verify would be reported as
a failed configuration method, not a failed BS-009/BS-010.

Suggested wording: "The tests get a closeable handle from the support class; the
only stub-related lines allowed in a test body are obtaining the handle, reading
its ApiSteps, and one call that verifies the stub was used."

### 3. "Nothing static" is stricter than intended (item 4)

`api.Config` is already static (properties loaded once, read through static
methods), and the flags will be read through it. A support class with static
factory methods is also the natural shape. Both are safe because nothing in them
is mutable.

Suggested wording: "No mutable static state and nothing shared between tests:
each test gets its own ApiSteps, spy and WireMock server."

### 4. Flag parsing can silently switch a stub off (items 1, 2, 5)

If the flags are parsed with `Boolean.parseBoolean`, a typo such as
`-Dstub.catalogue.wiremock=ture` or `=1` means "false". The nightly step would
then pass while running the real catalogue, and nothing would show it.

Suggested wording: "Flag values must be exactly `true` or `false`; anything else
fails fast with the key name in the message."

## Smaller points

- **Blank override (item 1).** `Config.getRequired` rejects blank values. The
  prompt does not say whether `-Dbase.url=` (empty) is an error or falls back to
  `api.properties`. Pick one; an error is consistent with the current behaviour.
- **nightly.yml (item 5).** Steps run sequentially with no `if: always()`, so the
  new step is skipped when an earlier step fails (already true today: a UI
  regression failure skips the API step). Say so if the stubbed run should be
  independent. Also say where the step goes; after the existing API step is the
  obvious place.
- **Where the learning-example comments go (item 3).** The two comment blocks
  above BS-009 and BS-010 describe the stubs. Once the wiring moves, they belong
  on the support class, with a one-line pointer left on each test. Covered by
  "decide the rest yourself", but worth stating if you care.
- **Confluence.** The BS-009 and BS-010 specs now need to say the stub is
  optional and off by default. The prompt does not mention it, unlike the two
  previous prompts.

## Checked and fine

- `-D` flags on the `mvn` command line do reach the forked test JVM (same
  mechanism as the existing `-Dheadless=true`), so item 6's "both on" run also
  proves item 1.
- No BDD scenario is tagged `@BS-009` or `@BS-010`, so BDD is only affected
  through the shared `ApiSteps` class; running `bdd.xml` once covers it.
- `ApiSteps` must keep exactly one public constructor because PicoContainer
  injects it into BDD glue. The current `withCatalogueBaseUrl` factory already
  respects this; the refactor must not add a public constructor.
- No new dependency is needed.
- A new package for the support class is not in CLAUDE.md's package layout; the
  prompt already handles this by asking for the package name.
- With both flags on, the real `GET /BookStore/v1/Books` is still exercised by
  BS-001.
