# ConfigException check

Suite: `src/test/resources/suite/api-parallel-testng.xml` (`BookApiTests`, 18 tests), real demoqa.com.
Bad config files were temporary, placed only in `target/test-classes/config/`, and removed afterwards.

| Scenario | How it was emulated | Result | Exception | Expected by CLAUDE.md | OK |
|---|---|---|---|---|---|
| Valid config | none | 18 of 18 passed | none | none | yes |
| `user.password` missing | `-Denv` file without the key | 18 of 18 failed | `api.ConfigException` (18 times, in the log) | ConfigException | yes |
| `user.password` blank | `-Denv` file with `user.password=` | 18 of 18 failed | `api.ConfigException` (18 times, in the log) | ConfigException | yes |
| `base.url` missing | `-Denv` file without the key | stopped at startup, 0 tests | ConfigException (from the code, see note) | ConfigException | yes |
| Wrong password value | `-Duser.password=wrong` | 11 of 18 failed | `AssertionError`: expected 201, found 400 | no config error; server rejects it | yes |
| Unknown environment | `-Denv=doesnotexist` | stopped at startup, 0 tests | IllegalStateException (from the code, see note) | ConfigException | NO |
| Invalid boolean | `-Dstub.catalogue.wiremock=maybe` | 1 of 18 failed | `java.lang.IllegalStateException` (in the log) | ConfigException | NO |

Note: in the two startup cases the exception comes from `ApiConfigCheck`, in the TestNG listener. Surefire prints
only the message there, not the class. The class above comes from the only `throw` that produces that message.

## Findings

1. `Config.java:70` (unknown environment) still throws IllegalStateException; CLAUDE.md requires ConfigException.
2. `Config.java:111` (invalid boolean) still throws IllegalStateException; CLAUDE.md requires ConfigException.
3. `Config.java:76` (config file cannot be read) also throws IllegalStateException. CLAUDE.md does not list this case,
   but the `ConfigException(String, Throwable)` constructor is currently unused and fits it.
4. The `Config` Javadoc (`Config.java:16`) still says IllegalStateException.
5. A blank `-Duser.password=` cannot emulate a missing password, because a blank override counts as not set.
6. Wrong password: 7 tests still pass, so they do not depend on that password being valid. The 400s are the server
   rejecting the password, so ConfigException is correctly not involved.

## Update after the fix

Findings 1 to 4 are fixed: all configuration errors in `Config` now throw `ConfigException`, and the unreadable-file
case keeps the original exception as its cause.

| Scenario | Result | Exception |
|---|---|---|
| Valid config | 18 of 18 passed | none |
| Invalid boolean | 1 of 18 failed | `api.ConfigException` (in the log) |
| Unknown environment | stopped at startup, 0 tests | `api.ConfigException` (confirmed by calling `Config` directly) |
| Missing `base.url` | not rerun through Maven | `api.ConfigException` (confirmed by calling `Config` directly) |

The unreadable-file case was not exercised: it needs a real read failure, so it is checked by code reading only.
