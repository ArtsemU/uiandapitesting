# Convention review: diff for the reviewer-findings fix batch

Date: 2026-10-10 16:15. Reviewer: `convention-reviewer` subagent (read-only). It re-read CLAUDE.md and all six rules files and checked 9 modified files. There are no untracked files.

## Rule violations

None.

Closest checks, all passing:
- `ci-workflow.yml` and `nightly.yml`: `continue-on-error: true` on the Allure build and upload steps keeps report failures from failing the job. Both `if: always()` steps are kept, and the artifact names follow `ci.md`. (The reviewer said "four jobs"; there are three: `simple_check`, `bdd` and the nightly job.)
- `UserStepDefs` / `CollectionStepDefs`: token storage and cleanup-tracking removal now live in `When` steps. The `Then` steps only assert, which matches the updated `bdd.md`.
- `CheckBoxTestData` `NODE_*` constants: these are UI labels, so the `EXPECTED_OUTPUT_` prefix does not apply. No node-label literals remain.
- `PASSWORD_RULES_CODE`: `"1300"` now appears only in the constant.

## Other problems

1. The assertion lines in `UserStepDefs.java:51` and `BookApiTests.java:432` are now over 150 characters. No rule covers line length.
2. `.gitignore`: the new `.idea/` entry makes the specific `.idea/*.xml` lines redundant. Main-session note: the reviewer's worry that the entry would "hide" tracked files is wrong. Files git already tracks stay tracked (`.idea/.gitignore`, `encodings.xml`, `misc.xml`, `vcs.xml`). Only new, untracked `.idea` files are ignored.
3. In the login and delete `When` steps, storing the token and the cleanup bookkeeping are skipped on failure, and the failure surfaces in the next `Then`. This works because every scenario has a `Then` right after the `When`. This is option B, chosen deliberately.
4. Removing the `# <-- added` comments was harmless cleanup (it was in the prompt).

## Ambiguous rules, with suggested wording

1. `ci.md` "must not change whether the job passes or fails": the rule does not name the mechanism. Suggested: "Report and upload steps carry `continue-on-error: true`, so a report failure never fails the job."
2. `bdd.md` "Actions never go into `Then` steps": the rule does not say whether read-only verification calls are allowed. Suggested: "Then steps may issue read-only calls to verify the result, but never calls that change server state."
3. `bdd.md`: the rule does not say outright that `When` steps may store response data (token, ids) in the scenario context. That is implied, but could be stated.
4. `ui.md`: there is no naming rule for UI *input* constants (such as `NODE_*`). Suggested: "UI input constants are named `<ELEMENT>_<NAME>`."
5. `api.md` lists `src/test/java/bdd/**` in its paths, and `bdd.md` covers it too. The two overlap on reading Responses. They do not contradict each other now but could drift apart.

## Verdict

The diff follows the rules. Open decisions: whether to put the `continue-on-error` pattern into `ci.md` explicitly, and whether to drop the redundant `.idea/*` lines from `.gitignore`.
