# JMeter DSL load-test example (BS-001) — dependency check and run results

Snapshot of 2026-09-30 17:45. Branch `addUiTests_20_load`, nothing written to git.

## 1. What was added

| File | Purpose |
|---|---|
| `pom.xml` | `us.abstracta.jmeter:jmeter-java-dsl:2.2.1`, test scope, with an exclusion of `org.codehaus.groovy:*` (see 2.1) |
| `src/test/java/performance/BookstoreLoadTest.java` | the one TestNG class / one test |
| `src/test/java/testing/testdata/BookstoreLoadTestData.java` | load profile and sampler names |
| `src/test/resources/suite/performance.xml` | its own suite |

Run: `mvn test -DsuiteXmlFile=src/test/resources/suite/performance.xml`

Output per run, in `target/performance/<yyyy-MM-dd_HH-mm-ss>/`:
`html-report/index.html` (JMeter dashboard, plus `report.jtl`) and `BS-001-load.jmx` next to it.

No existing suite picks the class up: only `ui_smoke.xml` / `ui_regression.xml` select by
package, and that package is `testing.ui`; every other suite (and the root `testng.xml`)
lists classes by name. No workflow under `.github/workflows/` references `performance.xml`.

## 2. Dependency tree findings

The DSL adds 131 artifacts to the test classpath (JMeter 5.6.3 engine, Kotlin stdlib,
Batik, Saxon, Xalan, lets-plot, …). Compared against the tree of the committed `pom.xml`.

### 2.1 Broke the project — fixed with an exclusion

**Groovy 3 vs Groovy 5.** JMeter brings `org.codehaus.groovy:groovy*:3.0.20`; RestAssured
6.0.0 brings `org.apache.groovy:groovy*:5.0.3`. The groupIds differ, so Maven does not treat
them as one artifact and puts both on the classpath. Result with the bare dependency:

- API suite: 17 of 17 failed; `bdd.xml`: 6 of 8 failed (every scenario that touches the API)
- cause: `GroovyRuntimeException: Conflicting module versions. Module [groovy-xml is loaded
  in version 5.0.3 and you are trying to load version 3.0.20` → `RestAssured` class fails to
  initialise

Fix applied: `<exclusion>` of `org.codehaus.groovy:*` on the DSL dependency. Cost: JMeter
plans in this project cannot use Groovy JSR223 elements or `${__groovy()}` — there is no
Groovy 3 runtime and no Groovy 5 JSR223 engine. The plan written here uses neither.

### 2.2 Versions that changed for existing code — no failure seen

| Artifact | Before | After | Used by |
|---|---|---|---|
| `commons-io:commons-io` | 2.20.0 | **2.15.1 (downgrade)** | `commons-compress` 1.28.0 under WebDriverManager, which asks for 2.20.0 |
| `org.mozilla:rhino` | 1.7.7.2 | 1.7.14 | `json-schema-core` under RestAssured's `json-schema-validator` |
| `org.apache.httpcomponents:httpcore` | 4.4.13 | 4.4.16 | RestAssured's `httpclient` 4.5.13 |

The `commons-io` downgrade is the one to watch: it sits on the UI side, on the path
WebDriverManager uses to unpack a downloaded driver. The UI scenarios in `bdd.xml` passed,
but with a driver that was already cached, so the unpack path itself was not exercised.
Pinning `commons-io` in `dependencyManagement` would remove the risk — not done, that is a
version choice for you.

### 2.3 Noted, harmless in the runs

- `log4j-slf4j-impl:2.22.1` (SLF4J 1.x binding) and `log4j-1.2-api:2.22.1` arrive next to the
  project's `log4j-slf4j2-impl:2.26.0` / `log4j-core:2.26.0`. No SLF4J warning in any run;
  JMeter logs through the project's `log4j2.xml`.
- Jackson: JMeter asks for 2.16.1, the project's 2.22.2 wins. No issue seen.
- Kotlin stdlib is mixed (`kotlin-stdlib` 1.9.22, `-jdk7/-jdk8/-common` 1.8.20) — internal to
  the JMeter side.
- Selenium and Cucumber: nothing changed, no shared artifacts moved apart from `commons-io`.
- Already there before this change, unrelated to it: Guava 33.2.1 wins over the 33.6.0
  Selenium asks for; byte-buddy 1.17.7 wins over 1.18.10.

## 3. Load run

Profile: 2 threads, ramp-up 10 s, 3 iterations per thread, think time 1–2 s uniform.
Run of 17:42, base URL `https://demoqa.com`. 60 samples, 0 errors, test passed.

| Sampler | Samples | Errors | Median ms | p95 ms | Max ms | Status seen |
|---|---:|---:|---:|---:|---:|---|
| 01 Create user | 6 | 0 | 889 | 1290 | 1290 | 201 |
| 02 Generate token | 6 | 0 | 1311 | 1828 | 1828 | 200 |
| 03 Get user (new) | 6 | 0 | 196 | 948 | 948 | 200 |
| 04 Get catalogue | 6 | 0 | 51 | 63 | 63 | 200 |
| 05 Add first book | 6 | 0 | 197 | 440 | 440 | 201 |
| 06 Get user (after add) | 6 | 0 | 193 | 242 | 242 | 200 |
| 07 Remove book | 6 | 0 | 498 | 649 | 649 | 204 |
| 08 Get user (after remove) | 6 | 0 | 145 | 1333 | 1333 | 200 |
| 09 Delete user | 6 | 0 | 383 | 730 | 730 | 204 |
| 10 Get user (deleted) | 6 | 0 | 45 | 66 | 66 | 401 (expected) |
| **Total** | 60 | 0 | 249 | 1473 | 1828 | |

Throughput: about 0.15 requests/s per sampler, 1.01 requests/s overall.

These figures are from the HTML report (`statistics.json`). With 6 samples a p95 is simply
the maximum.

**The p95 the test logs is not the same number.** The test logs what the DSL's own
`TestPlanStats` returns, and at this sample count its p95 comes out equal to its median
(e.g. `01 Create user`: logged median 932, p95 932, max 1290), and its median differs from
the report's. Samples, errors, max and throughput agree. At 6 samples per sampler treat the
logged percentiles as unreliable and read them from the HTML report.

## 4. Regression check (with the Groovy exclusion in place)

| Suite | Result |
|---|---|
| `api-parallel-testng.xml` | 17 run, 0 failed |
| `api-parallel-testng.xml` with `-Dstub.catalogue.wiremock=true -Dstub.catalogue.mockito=true` | 17 run, 0 failed |
| `bdd.xml` with `-Dheadless=true` | 8 run, 0 failed |

Not run: the TestNG UI suites (`ui_smoke.xml`, `ui_regression.xml`).

## 5. Deviations and things not verified

- The load test hit demoQA three times, not once: the first run stopped on a bug of mine
  (below), the second passed before the Groovy exclusion, the third is the one reported.
  18 users created in total, all deleted by the flow itself.
- First-run bug: the DSL adds an HTTP cache manager by default, and JMeter records no
  sample for a cache hit, so steps 06, 08 and 10 (same URL as step 03) silently produced
  nothing. Fixed with `httpCache().disable()`.
- The failing side of the threshold was not exercised: no run had an error, so "errors > 0
  fails the test" is covered by the assertion's logic only.
- The `.jmx` was checked by content (stock JMeter elements only, 2 threads / 10 s / 3 loops),
  not opened in the JMeter GUI.

## 6. Choices made

- Package `performance`, class `BookstoreLoadTest`, suite `performance.xml`.
- `testName = "BS-001: Add book to user under light load"`, `priority = 1`,
  `groups = {"performance"}`.
- Constants (profile, thread-group name, sampler names) in
  `testing.testdata.BookstoreLoadTestData`; request paths stay in the test class, the way the
  API clients hold theirs. The catalogue path reuses `BookstoreTestData.CATALOGUE_PATH`.
- Sampler names are numbered (`01 Create user` … `10 Get user (deleted)`) so report rows
  sort in flow order and the four "get user" steps are told apart.
- Username `perf_t<thread>_i<iteration>_<millis>`, built with JMeter functions
  (`__threadNum`, the thread group's `__jm__…__idx`, `__time`) rather than a Java lambda: a
  lambda would not survive into the `.jmx`.
- Correlation with JMESPath extractors: `userID`, `token`, `books[0].isbn`.
- Status checks are explicit response-code assertions on every sampler; the final one adds
  `ignoreStatus()` so the expected 401 counts as a pass.
- Think time is one timer at thread-group scope, so it also runs before the first step of
  each iteration — 10 pauses per iteration rather than 9. It is not part of any measured
  response time.
- Each run writes to its own timestamped directory: JMeter refuses to write an HTML report
  into a non-empty one, so a fixed path would fail on every second run without `mvn clean`.
- The `.jmx` is saved from the plan without the HTML reporter, which is a DSL-only listener
  the JMeter GUI does not have.
- Cookies left at the DSL default (on, cleared each iteration).
- The `.jmx` contains the demoQA password in clear text, like `api.properties` does; it is
  under `target/`, which git ignores.

## 7. Proposed CLAUDE.md wording

Package layout, under `src/test/java`:

> - `performance` — load tests written with JMeter DSL (`*LoadTest`). Requests are built
>   with the DSL's own HTTP samplers, not `ApiSteps`/RestAssured. Learning example at a
>   deliberately tiny load against demoQA; runs only through
>   `src/test/resources/suite/performance.xml`, never from CI. Output goes to
>   `target/performance/<timestamp>/`.

Test identifiers, `groups` rule:

> - Load tests carry `groups = {"performance"}` instead of `smoke`/`regression`, and reuse
>   the ID of the test case whose flow they replay.

Dependencies:

> - `jmeter-java-dsl` excludes `org.codehaus.groovy:*`: JMeter's Groovy 3 and RestAssured's
>   Groovy 5 cannot share a classpath. JMeter plans must not use Groovy JSR223 elements or
>   `${__groovy()}`.
