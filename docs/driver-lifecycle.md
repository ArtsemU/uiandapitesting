# WebDriver Lifecycle

## Entry points

There are two places a `WebDriver` gets created for a test run:

1. **TestNG tests**, via `BaseUITest` — implemented today. This is currently the only entry point that exists in the repo.
2. **BDD scenarios**, via Cucumber hooks — planned, secondary. As of now there is no Cucumber dependency and no hooks class in this repo; this section documents the rule the hooks must follow *when* they're added, not something already built.

The reason there have to be two entry points at all: Cucumber hooks are plain `@Before`/`@After` methods invoked by Cucumber's own runtime, not TestNG's. A hooks class cannot extend `BaseUITest` to inherit its `@BeforeMethod`/`@AfterMethod` setup — the two frameworks don't share a lifecycle. So BDD scenarios necessarily need their own setup/teardown code path, independent of `BaseUITest`.

## Why the two entry points must stay in sync

Both entry points solve the same problem — hand a test (or scenario) a working `WebDriver` — and must produce equivalent drivers. The rule:

**All driver construction logic lives in `WebDriverFactory`, and only there. `BaseUITest` and any future BDD hooks are callers, never implementers, of that logic.**

Concretely: browser options/capabilities, `WebDriverManager` setup calls, and the `new XDriver()` construction happen once, inside the factory. Neither `BaseUITest` nor future hooks should instantiate a driver directly or duplicate any part of that setup. If this rule is broken — e.g. a hooks class calls `new ChromeDriver()` itself, or re-implements `WebDriverManager.chromedriver().setup()` — the two paths will silently drift: a change made for one test style (say, switching to headless mode, or pinning a driver version) won't apply to the other, and the gap won't surface until it causes a bug that only reproduces in TestNG or only in BDD, not both.

## Known gaps

- **`driver.quit()` is commented out in `BaseUITest.tearDown()` intentionally, not as an oversight or leftover.** The intent is to leave the browser open after a test finishes so its state can be inspected. Do not "clean this up" by deleting the commented line or re-enabling `quit()` without confirming — it's a deliberate debugging aid, not dead code.
- **BDD hooks don't exist yet.** No Cucumber dependency in `pom.xml`, no hooks class anywhere in the repo. Everything above about hooks is a constraint for whoever adds them, not a description of current code.
- **Browser selection is hardcoded to `Browser.CHROME`** in `BaseUITest`, even though the factory's `Browser` enum also supports EDGE and SAFARI. Nothing in the repo currently picks a non-Chrome browser. Whoever adds BDD hooks will need to decide how (or whether) the hooks path selects a browser — that decision doesn't exist yet.
