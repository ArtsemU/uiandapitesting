# API environment selection — 2026-10-12

## Change
- `api.Config`: `-Denv=<name>` selects `config/api-<name>.properties` (standalone, no fallback);
  without it `config/api.properties`. `-D<key>` still wins. Loaded lazily (no static initializer).
  Logs once: `API environment: <name|default>, base.url: <value>`.
- Unknown env: `IllegalStateException("unknown environment <name> (no config/api-<name>.properties on the classpath)")`.
- `api.ApiSpec.SPEC` (static field) -> `ApiSpec.spec()` (lazy); clients updated.
- `api.support.ApiConfigCheck` (TestNG `IExecutionListener`, registered in
  `META-INF/services/org.testng.ITestNGListener`): with `-Denv` set, loads Config before any test
  class is instantiated. Without it TestNG reported "Cannot instantiate class api.tests.BookApiTests"
  and the real message was only in the surefire dump file.
- `performance.BookstoreGatlingSimulation`: `CREDENTIALS_BODY` static -> instance field `credentialsBody`,
  so Gatling also shows the message rather than an ExceptionInInitializerError (Gatling does not use TestNG listeners).
- New `src/test/resources/config/api-qa2.properties` (copy of `api.properties`).

## Config readers (all go through `api.Config`)
API tests (`BookApiTests`, `UserCredentials`), clients/`ApiSpec`, `CatalogueStub`, BDD (via `ApiSteps`),
`BookstoreLoadTest`, `BookstoreGatlingSimulation`. No other reader of the file exists.

## Runs (suite `api-parallel-testng.xml`)
| Run | Result |
|---|---|
| default | 18/18 passed, log `environment: default` |
| `-Denv=qa2` | 18/18 passed, log `environment: qa2` |
| `-Denv=qa9` | build fails at startup: `unknown environment qa9 (no config/api-qa9.properties on the classpath)`, no tests run |
| `-Denv=qa2 -Dstub.catalogue.mockito=true` | 18/18 passed, Mockito stub active (override beats qa2 file) |
| `-Denv=qa2 -Dstub.catalogue.wiremock=true` | 18/18 passed, WireMock stub active |
| both stub flags, default env | 18/18 passed, both stubs active |

Not run: BDD and performance suites (compile only).
