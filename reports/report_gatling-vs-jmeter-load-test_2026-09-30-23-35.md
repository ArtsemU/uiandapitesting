# Gatling load-test example (BS-001) next to the JMeter DSL one

Snapshot of 2026-09-30 23:35. Branch `addUiTests_20_load`, nothing written to git.

## 1. What was added / changed

| File | Change |
|---|---|
| `pom.xml` | `io.gatling.highcharts:gatling-charts-highcharts:3.16.0` (test scope, excludes `ch.qos.logback:*`, see 2.1); plugin `io.gatling:gatling-maven-plugin:4.21.12` with no `<executions>` and `simulationClass` = `performance.BookstoreGatlingSimulation` |
| `src/test/java/performance/BookstoreGatlingSimulation.java` | new: the simulation |
| `src/test/java/performance/BookstoreLoadTest.java` | unchanged in the end (temporarily broken for step 7, then restored) |

Run: `mvn test-compile gatling:test` — the goal does not compile sources itself, so a bare
`mvn gatling:test` would run whatever is already in `target/test-classes`.
Report: `target/gatling/bookstoregatlingsimulation-<timestamp>/index.html`.

Lifecycle check: in the three `mvn test` runs of section 5, Maven executed only resources,
compiler and surefire — no `gatling-maven-plugin` line in any log.

## 2. Dependency tree findings

Gatling adds 83 artifacts (29 of them Netty, 22 Gatling modules, Scala 2.13 library,
Brotli4j, Pebble, …). Compared against the tree before this change.

### 2.1 Broke the project — fixed with an exclusion

**Logback as a second SLF4J provider.** Gatling brings `logback-classic` 1.6.4. It lands on
the classpath before the project's `log4j-slf4j2-impl`, and SLF4J 2 takes the first provider
it finds. Seen in the broken JMeter run (step 7):

```
SLF4J(W): Class path contains multiple SLF4J providers.
SLF4J(I): Actual provider is of type [ch.qos.logback.classic.spi.LogbackServiceProvider]
```

Logback, unconfigured, logs everything at DEBUG: 4,609 DEBUG lines in one JMeter run, and
`log4j2.xml` was ignored for every suite.

Fix applied: exclude `ch.qos.logback:*` from the Gatling dependency. After the fix, no run
shows an SLF4J warning or a DEBUG line, and Gatling runs on Log4j2 through SLF4J without
problems.

### 2.2 Versions where the JMeter DSL wins over what Gatling asks for — no failure seen

Nearest-wins resolution picks the JMeter side because it is declared first:

| Artifact | Gatling wants | Resolved | Note |
|---|---|---|---|
| `com.github.ben-manes.caffeine:caffeine` | 3.3.0 | **2.9.3** | major version behind |
| `org.jodd:jodd-lagarto` | 6.0.6 | **5.0.13** | major; Gatling uses it for HTML resource inference, which is off here |
| `net.sf.saxon:Saxon-HE` | 12.10 | **11.6** | major; Gatling uses it only for XPath checks, none used here |
| `org.apache.commons:commons-pool2` | 2.13.1 | 2.12.0 | minor |
| `com.fasterxml.jackson.core:jackson-databind` | 2.22.3 | 2.22.2 (project pin) | patch |
| `org.slf4j:slf4j-api` | 2.0.20 | 2.0.18 (project pin) | patch |
| `org.scala-lang:scala-library` | several 2.13.x | 2.13.18 | fine |

Caffeine is the one to watch: Gatling runs on a Caffeine 2 it was not built against. The
simulation passed twice, but a Gatling feature this simulation does not use could hit a
missing method. Declaring Caffeine in `dependencyManagement` would settle it — not done,
that is a version choice for you.

### 2.3 What moved for existing code

Only `org.xmlresolver:xmlresolver` 5.2.1 (under JMeter's Saxon) changed. Nothing under
RestAssured, Selenium, Cucumber, WireMock or Mockito changed version. Gatling's Netty
comes in new: Selenium 4.45 does not bring Netty onto this classpath.

## 3. Thresholds proven to fail (step 7)

Both checks were broken the same way: the last request ("10 Get user (deleted)") was told to
expect 404 instead of 401. The broken step is at the very end, so the flow and its cleanup
(delete user) still ran normally.

| Tool | Result of the broken run |
|---|---|
| Gatling | `BUILD FAILURE` — `Gatling simulation assertions failed`; console: `Global: count of failed events is 0.0 : false (actual : 6.0)`; errors: `status.find.is(404), found 401` ×6; the other 54 requests OK |
| JMeter DSL | `BUILD FAILURE` — `AssertionError: Load run produced failed samples expected [0] but found [6]`; only `10 Get user (deleted)` had errors (6 of 6) |

Both restored to 401 afterwards (checked with grep before the final runs). The reports of the
broken runs were deleted from `target/` so only the final runs remain.

## 4. Final runs — side by side

Same profile for both: 2 users, ramp-up 10 s, 3 iterations each, think time 1–2 s before
every request. Gatling ran at 23:24, JMeter at 23:25; both passed, 0 errors. Times in ms, from
each tool's HTML report.

| Request | Gatling count | Gatling errors | Gatling median | Gatling max | JMeter count | JMeter errors | JMeter median | JMeter max |
|---|---:|---:|---:|---:|---:|---:|---:|---:|
| 01 Create user | 6 | 0 | 870 | 1469 | 6 | 0 | 1121.5 | 1548 |
| 02 Generate token | 6 | 0 | 1069 | 1663 | 6 | 0 | 1190.5 | 1783 |
| 03 Get user (new) | 6 | 0 | 236 | 1450 | 6 | 0 | 534.5 | 1298 |
| 04 Get catalogue | 6 | 0 | 46 | 58 | 6 | 0 | 55 | 80 |
| 05 Add first book | 6 | 0 | 218 | 954 | 6 | 0 | 190 | 255 |
| 06 Get user (after add) | 6 | 0 | 161 | 859 | 6 | 0 | 158.5 | 217 |
| 07 Remove book | 6 | 0 | 437 | 533 | 6 | 0 | 486.5 | 633 |
| 08 Get user (after remove) | 6 | 0 | 148 | 206 | 6 | 0 | 185.5 | 231 |
| 09 Delete user | 6 | 0 | 454 | 729 | 6 | 0 | 509.5 | 859 |
| 10 Get user (deleted) | 6 | 0 | 45 | 51 | 6 | 0 | 67 | 173 |
| **Total** | 60 | 0 | 239 | 1663 | 60 | 0 | 222.5 | 1783 |

Reading it:
- The rows line up one to one: the request names are shared constants.
- The numbers are close. With 6 samples per row, one slow response on the public demoQA
  site moves a max by a second (e.g. `05 Add first book`: 954 vs 255). The runs do not show
  that one tool is faster.
- The medians are computed differently. JMeter interpolates between the two middle values
  (1121.5); Gatling reports an actual sample value (870).
- Throughput: 0.92 req/s for Gatling, about 1.0 for JMeter.

## 5. Regression check (final pom)

| Suite | Result |
|---|---|
| `api-parallel-testng.xml` | 17 run, 0 failed |
| `api-parallel-testng.xml` with `-Dstub.catalogue.wiremock=true -Dstub.catalogue.mockito=true` | 17 run, 0 failed |
| `bdd.xml` with `-Dheadless=true` | 8 run, 0 failed |

No SLF4J warnings and no DEBUG output in any of them. The TestNG UI suites were not run.

## 6. demoQA traffic

Four load runs in total: Gatling broken, JMeter broken, Gatling final, JMeter final. That
makes 240 requests and 24 users; each run's flow deleted the users it created.

## 7. Choices made

- **Workload model.** The JMeter closed profile is reproduced as
  `rampUsers(2).during(10 s)` plus a `repeat(3)` loop around the flow. That gives 2
  long-lived users started 5 s apart, each iterating 3 times, and no new arrivals.
  Gatling's closed-model steps (`constantConcurrentUsers` / `rampConcurrentUsers`) hold a
  concurrency level for a duration and replace finished users. They cannot express
  "3 iterations per user", so they were not used. The difference between the two models is
  explained in a comment in the simulation.
- **Think time.** `pause(1 s, 2 s)` (uniform) before every request, matching the JMeter
  timer at thread-group scope, including the pause before step 01 of each iteration.
  Wrapped in a private `thinkThen(...)` helper.
- **Scenario name.** Reuses `BookstoreLoadTestData.THREAD_GROUP_NAME` ("Bookstore users").
  Request names reuse the `STEP_*` constants, so no literal is duplicated.
- **Username.** `perf_g_u<virtual user id>_i<iteration>_<millis>`, set in the session once per
  iteration. The `g` tells Gatling users apart from JMeter's `perf_t…` users.
- **Correlation.** `jsonPath`: `$.userID`, `$.token`, `$.books[0].isbn`. This is the Gatling
  counterpart of the JMESPath extractors in the JMeter test.
- **Request bodies.** Sent with `.asJson()`, which sets `Content-Type` and also
  `Accept: application/json` (JMeter sends only the content type).
- **Status checks.** An explicit `status().is(n)` on every request. That replaces Gatling's
  implicit "2xx/304" check, so the final 401 needs no special flag. JMeter needed
  `ignoreStatus()` for the same thing.
- **HTTP protocol.** `disableCaching()` (step 5), plus `disableWarmUp()`. Otherwise Gatling
  sends a warm-up request to gatling.io before the run.
- **Suite.** No TestNG suite XML: a Gatling simulation is not a TestNG test, and surefire
  never picks it up.
- **Plugin.** No `<executions>`, which is what keeps it out of the lifecycle. `simulationClass`
  is set explicitly so a second simulation added later does not make the plugin ask which
  one to run.

## 8. Proposed CLAUDE.md wording

Extends the `performance` package entry proposed in
`report_jmeter-dsl-load-test_2026-09-30-17-45.md`:

> - `performance` — load tests, learning examples at a deliberately tiny load against demoQA,
>   never run from CI. Two tools, same BS-001 flow and profile (`BookstoreLoadTestData`):
>   - JMeter DSL — `BookstoreLoadTest`, a TestNG test, run through
>     `src/test/resources/suite/performance.xml`; output in `target/performance/<timestamp>/`.
>   - Gatling — `BookstoreGatlingSimulation`, run with `mvn test-compile gatling:test`;
>     output in `target/gatling/`. The plugin has no executions and never runs in `mvn test`.
>   Every Gatling class has "Gatling" in its name. Request names are shared constants so the
>   two reports compare row by row. Neither uses `ApiSteps`/RestAssured.

Dependencies:

> - `gatling-charts-highcharts` excludes `ch.qos.logback:*`: Logback would become the SLF4J
>   provider for the whole test classpath and override `log4j2.xml`.
