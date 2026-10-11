---
description: Checks a prompt for Claude Code against the current state of the repo and the rules, without running it or changing anything.
argument-hint: <the prompt text>
disable-model-invocation: true
disallowed-tools: Edit, Write, NotebookEdit, Bash, PowerShell
---
Check the prompt below. Do not carry it out and do not change anything.

Prompt to check:
$ARGUMENTS

1. Find everything the prompt refers to — files, classes, methods, test
   IDs, suites, rules — and check that it exists as the prompt describes.
2. Check whether the work is already done, fully or partly.
3. Check the prompt against CLAUDE.md and the rules files that cover the
   files it touches: would following it break a rule, or would a rule make
   the agent stop and ask?
4. If it names a test case ID, read its spec in Confluence and check that
   the prompt matches it.
5. Find what is missing or ambiguous: anything the agent would have to
   guess.

Report:
Verdict: Ready to run / Fix first.
Then one line per problem: what is wrong and how to fix the prompt.
If there are no problems, say "No problems found."
Answer in the chat only; do not write a report file.