# Links between `src/test/java/lessonslearned` and the rest of the repo

Read-only analysis before deleting `src/test/java/lessonslearned`. Nothing was changed.
Excluded from the search: `target/`, `allure-report/`, `.allure/`, `.git/` (build output / VCS).
`sandbox` was included when checking dependency usage.

## Verdict

**b) dependencies found.** No code outside the package refers to it, so deleting the
folder alone compiles and leaves every suite and CI job intact. But three pom.xml entries
exist only for it, and two instruction files (plus one untracked IDE file) name it.

| # | File | Line | Link |
|---|------|------|------|
| 1 | `pom.xml` | 222–229 | dependency `com.microsoft.playwright:playwright` 1.63.0 (scope compile) — used only by `lessonslearned.playwright` and `lessonslearned.playwrigth2` |
| 2 | `pom.xml` | 230–236 | dependency `com.aventstack:extentreports` 5.1.2 (scope compile) — used only by `lessonslearned.playwright.utils.ExtentManager` / `project1.base.BaseTest` |
| 3 | `pom.xml` | 282–289 | plugin `org.codehaus.mojo:exec-maven-plugin` 3.5.0 — its only documented purpose (comment at 282–284) is running the Playwright CLI (`codegen`) |
| 4 | `CLAUDE.md` | 115, 117, 124 | section "Sandbox and lessons-learned packages" names the package and its (non-existent) suite XMLs |
| 5 | `.claude/agents/convention-reviewer.md` | 21 | "Skip anything under ... src/test/java/lessonslearned" |
| 6 | `.idea/workspace.xml` (untracked, gitignored) | 181, 183, 265–271, 292–298 | IntelliJ run configurations for `OrangeRunner` and `TestRunner`, recent-folder entries |

## 1. Inbound — references from outside the package

Case-insensitive search for `lessonslearned`, `lessons-learned`, `lessons learned`, and for
every class/file name in the package (`GoogleSearchSteps`, `LoginSteps`, `TestRunner`,
`Hooks`, `OrangeHooks`, `OrangeRunner`, `OrangeParallelRunner`, `UiContext`,
`OrangeHRMStepDefenition`, `Dashboard`, `LoginPage`, `DellIt`, `BaseTest`, `HomePage`,
`FirstTests`, `LoginTest`, `ExtentManager`, `ScreenshotUtil`, `BasePage`, `BaseSteps`,
`BaseTests`, `LogicSteps`, `playwrigth2`, `orangehrm`, feature files, `interview-questions`).

| Area | Result |
|------|--------|
| Java code in `src/main/java`, `src/test/java` (incl. sandbox) | none. Name collisions only: `ui.steps.BaseSteps`, `ui.pages.BasePage` are the project's own classes, not references to the package |
| `testng.xml`, `src/test/resources/suite/*.xml` | none — no suite lists a `lessonslearned` class or package; no wildcard package entry |
| `pom.xml` | no textual reference (see Outbound for deps) |
| `.github/workflows/` | none — workflows run only the suites listed in the root/suite XMLs |
| `CLAUDE.md` | lines 115, 117, 124 |
| `README.md` | none |
| `.claude/` | `agents/convention-reviewer.md:21`; nothing in `rules/`, `settings*.json`, `hooks/`, `skills/` |
| `docs/`, `reports/` | none |
| `.gitignore` | none |
| `run-tests-and-report.cmd` | none |
| `META-INF/services/org.testng.ITestNGListener` | none (lists `reporting.*` and `api.support.ApiConfigCheck`) |
| Cucumber glue/features of `bdd` | none — `bdd.CucumberRunner` uses `glue = "bdd"`, `features = "classpath:bdd/features"` |
| `.idea/workspace.xml` | run configs + recent paths (untracked, gitignored, local only) |

## 2. Outbound — what exists only because of the package

### pom.xml

| Dependency / plugin | Used in lessonslearned | Used elsewhere |
|---------------------|------------------------|----------------|
| `com.microsoft.playwright:playwright` | `playwright/**`, `playwrigth2/**` | **nowhere else** (no `com.microsoft.playwright` import in `src/main/java` or the rest of `src/test/java`, sandbox included) |
| `com.aventstack:extentreports` | `playwright/utils/ExtentManager`, `playwright/project1/base/BaseTest` | **nowhere else** in code; only prose mentions in `docs/TODO.md:32` and two old reports |
| `org.codehaus.mojo:exec-maven-plugin` | not used by code; pom comment says it runs the Playwright CLI codegen | **nowhere else** — no workflow, script or doc calls `exec:java` |
| `io.cucumber:cucumber-java/-testng/-picocontainer` (+ BOM) | `cucumber/**` | `bdd` package — keep |
| `org.testng:testng`, `selenium-java`, `slf4j-api` | yes | whole project — keep |

Everything else in the pom (RestAssured, Jackson, Mockito, WireMock, JMeter DSL, Gatling,
Log4j2, Allure, AspectJ, WebDriverManager, surefire, allure-maven, gatling-maven-plugin)
is not used by `lessonslearned`.

### Main code the package uses (stays; not "only because of" it)

- `ui.pages.BasePage` — extended by `cucumber/orangehrm/pages/Dashboard`, `LoginPage`; also
  the base of every project page object.
- `factory.WebDriverFactory.createDriver()` — called by `cucumber/GoogleSearchSteps`,
  `cucumber/orangehrm/OrangeHooks`; also called by `bdd.UiSession`.

### Resources, configs, test data

- Feature files live inside the package (`cucumber/features/*.feature`), referenced by
  filesystem path from `TestRunner` / `OrangeRunner` — go with the package.
- `interview-questions.md` lives inside the package — goes with it; nothing links to it.
- No file under `src/test/resources` and no `testing.testdata` class is used by the package.
- `test-output/` (repo root, empty, untracked): `ExtentManager` writes
  `test-output/ExtentManager.html` and `ScreenshotUtil` writes `test-output/screenshots/`.
  Not exclusive — it is also TestNG's default output folder for IDE runs — so not a link
  to remove.

## Notes for the deletion

- Deleting the folder alone is safe for compilation and for `mvn test`/CI.
- Orphans left behind: the three pom entries above (playwright, extentreports,
  exec-maven-plugin). Removing them is a dependency change, so it needs the user's go-ahead.
- `CLAUDE.md` section "Sandbox and lessons-learned packages" and
  `.claude/agents/convention-reviewer.md:21` would refer to a missing folder. Both are
  blocked for the agent; the user edits them. Already now, `CLAUDE.md:124` says
  `lessonslearned` suite XMLs live under `src/test/resources/suite/` — none exist.
  Proposed wording after deletion: rename the section to "Sandbox package" and drop every
  `lessonslearned` mention; in convention-reviewer.md: "Skip anything under
  src/test/java/sandbox."
- `.idea/workspace.xml` run configurations will show as broken in IntelliJ; local only.
