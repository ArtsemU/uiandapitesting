# Prompt check — "Agent instructions / rules files" rule

Date: 2026-10-08 00:45
Mode: check only. Nothing was edited.

## What the prompt is

A replacement for the existing bullet in CLAUDE.md, section "README and this
file" (lines 207–209). Its first three lines match the current text word for
word; three sentences are new:

1. Each rules file starts with its `paths:` frontmatter, then a "# Description"
   section.
2. The Description has two or three simple sentences on what the rules are for
   and when the agent gets them.
3. Pointers to a rules file name the file, never its folders or globs.

## Current repo vs the proposed rule

| Requirement | State | Result |
|---|---|---|
| No nested CLAUDE.md | Only `./CLAUDE.md` exists | OK |
| Every rules file starts with `paths:` frontmatter | `api.md`, `testing.md`, `ui.md` — all line 1 `---`, `paths:` | OK |
| "# Description" comes straight after the frontmatter | All three, line 7 | OK |
| 2–3 sentences, on purpose + when loaded | api.md 2, ui.md 2, testing.md 3; all end with "The agent gets this file automatically when…" | OK |
| Pointers name the file, no folders/globs | 11 pointers (CLAUDE.md 8, testing.md 3), all `see .claude/rules/<x>.md (loads automatically for …)` | OK |

The repo already complies; adding the rule needs no other change.

## Issues in the wording

1. **"never its folders" contradicts the pointers it describes (literal reading).**
   Each pointer contains `.claude/rules/`, which is a folder. "Its folders"
   is meant as the folders the file *covers*, but can be read as the file's
   own location. Suggested: "Pointers to a rules file name the file, never the
   folders or globs it covers."
2. **"Pointer" is not defined.** It is clear from this session, not to a future
   reader. Suggested: "A pointer is a line elsewhere in CLAUDE.md or
   .claude/rules/ that sends the reader to a rules file."
3. **No permission to edit CLAUDE.md.** The prompt contains only the rule text.
   CLAUDE.md says "Do not edit CLAUDE.md". To apply it, the run prompt needs
   the same one-task override used in the previous tasks.
4. **"Simple" is subjective** and can't be checked. Minor; could be dropped,
   since "two or three sentences" already limits the length.
5. **Minor:** the new sentences make the file have two H1 headings
   (`# Description`, then the original title). This works but differs from
   the usual one-title-per-file layout. Optional: make it `## Description`.
   That would require changing the three files and the rule.

## Suggested final wording

```
- Agent instructions live only in the root CLAUDE.md and .claude/rules/.
  Do not create nested CLAUDE.md files; area-specific rules go into a
  path-scoped file under .claude/rules/. Each rules file starts with its
  paths: frontmatter, then a "# Description" section: two or three
  sentences on what the rules are for and when the agent gets them.
  Pointers to a rules file (lines elsewhere that send the reader to it)
  name the file, never the folders or globs it covers.
```
