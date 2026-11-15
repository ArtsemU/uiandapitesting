# Sandbox inventory — 2026-11-15

Read-only inventory of `src/test/java/sandbox` and the suite XMLs that only run
sandbox code. Nothing was changed. Verdicts are suggestions.

Dependencies: every sandbox class uses only the JDK, TestNG and SLF4J. Both are
used across the whole project, so **no pom dependency is used only by sandbox**.
No sandbox class depends on project classes outside `sandbox`.

## Classes

| # | File | What it is | Who uses it | Depends on | Verdict | Reason |
|---|------|-----------|-------------|-----------|---------|--------|
| 1 | `sandbox/Counter.java` | Plain `int` counter, deliberately not thread-safe | `CounterTest` only | JDK | keep in sandbox | Part of the race-condition experiment; harmless |
| 2 | `sandbox/SynchronizedCounter.java` | Counter with `synchronized` methods | `CounterTest` only | JDK | keep in sandbox | Same experiment (thread-safe variant) |
| 3 | `sandbox/AtomicCounter.java` | Counter backed by `AtomicInteger` | `CounterTest` only | JDK | keep in sandbox | Same experiment (lock-free variant) |
| 4 | `sandbox/tests/CounterTest.java` | Races the three counters with `invocationCount`/`threadPoolSize`, logs final counts (no asserts) | `suite/sandbox-testng.xml` | Classes 1–3, TestNG, SLF4J | keep in sandbox | Observational experiment, used by nobody else |
| 5 | `sandbox/UnsafeMapWriter.java` | Wrapper around a plain `HashMap`, not thread-safe | `MapWriterTest` only | JDK | keep in sandbox | Part of the shared-collection experiment |
| 6 | `sandbox/MapWriter.java` | Wrapper around `ConcurrentHashMap` | `MapWriterTest` only | JDK | keep in sandbox | Same experiment (safe variant) |
| 7 | `sandbox/tests/MapWriterTest.java` | Concurrent writes to both maps, logs final sizes (no asserts); javadoc ties it to `createdUsers` in `BookApiTests` | `suite/sandbox-testng.xml` | Classes 5–6, TestNG, SLF4J | keep in sandbox | Experiment; note its logged "expected" size (3) does not match the number of writes |
| 8 | `sandbox/SharedTokenHolder.java` | Single shared `String` field, not thread-safe | `TokenHolderTest` only | JDK | keep in sandbox | Part of the shared-vs-ThreadLocal experiment |
| 9 | `sandbox/ThreadLocalTokenHolder.java` | `ThreadLocal<String>` holder | `TokenHolderTest` only | JDK | keep in sandbox | Same experiment; the real pattern already lives in `BookApiTests.createdUsers` |
| 10 | `sandbox/tests/TokenHolderTest.java` | Counts read-back mismatches for shared vs ThreadLocal token (no asserts) | `suite/sandbox-testng.xml` | Classes 8–9, TestNG, SLF4J | keep in sandbox | Experiment, used by nobody else |
| 11 | `sandbox/FlagWorker.java` | Busy-wait worker with a plain and a `volatile` stop flag | `FlagVisibilityTest` only | JDK | keep in sandbox | Part of the visibility experiment |
| 12 | `sandbox/tests/FlagVisibilityTest.java` | Starts a spinning thread, flips each flag, logs whether it stopped (no asserts) | `suite/sandbox-testng.xml` | Class 11, TestNG, SLF4J | keep in sandbox | Experiment; a plain-flag thread that never stops is left running until JVM exit |
| 13 | `sandbox/Cow.java` | Toy domain object (name, milk, hunger, age) for practising suite/thread config | `CowTest` only | JDK | keep in sandbox | Goes with `CowTest` (see 14) |
| 14 | `sandbox/tests/CowTest.java` | ~33 plain unit tests (two with data providers) on `Cow`, with `@Listeners(ConsoleBannerListener)` | `suite/sandbox-testng.xml`, `suite/cow-testing.xml`, `suite/cicd_syntetic_tests.xml` → **CI**: `ci-workflow.yml` step "Run PR-only suite" (pull requests only) | Classes 13, 15, TestNG, SLF4J | keep in sandbox | Used by CI, but it tests a toy object, not demoQA, so no CLAUDE.md package fits; deleting it means removing the CI step first |
| 15 | `sandbox/ConsoleBannerListener.java` | Educational TestNG listener printing a console banner per result | **Root `testng.xml`** (`<listeners>`, so every plain `mvn test`), and `@Listeners` on `CowTest` | TestNG | keep in sandbox | Its own javadoc says it is not part of reporting; the issue is that the default suite depends on sandbox — drop that line from `testng.xml`, or promote to `reporting` (registered via META-INF, per CLAUDE.md) if the banner is wanted |
| 16 | `sandbox/FakeClass.java` | Class with one unused field `s1 = "fake"` | nobody | — | delete | Dead code, no references anywhere |

## Suite XMLs used only by sandbox

| # | File | What it is | Who uses it | Verdict | Reason |
|---|------|-----------|-------------|---------|--------|
| 17 | `suite/sandbox-testng.xml` | Runs all five sandbox test classes, `parallel="methods"`, 5 threads | nobody (manual runs; referenced in sandbox javadocs) | keep in sandbox | The only runner for the concurrency experiments |
| 18 | `suite/cicd_syntetic_tests.xml` | Runs `CowTest`, `parallel="methods"`, 5 threads | `ci-workflow.yml` "Run PR-only suite" | keep | Needed by CI as long as that step exists |
| 19 | `suite/cow-testing.xml` | Runs `CowTest`, `parallel="methods"`, 10 threads | nobody | delete | Duplicate of `cicd_syntetic_tests.xml` except the thread count |

## Other mentions

- `CLAUDE.md` (sandbox rules) and earlier reports in `reports/` mention sandbox; no file in `docs/` or `README.md` does.
- Earlier report `report_diff-convention-review_2026-10-10-14-26.md` refers to a planted `By.cssSelector` field in `Cow.java`; it is no longer there.

## Counts

- keep: 17 (15 classes and 2 suite XMLs, incl. `cicd_syntetic_tests.xml` kept for CI)
- promote: 0 (`ConsoleBannerListener` is an option — see row 15)
- delete: 2 (`FakeClass.java`, `cow-testing.xml`)
