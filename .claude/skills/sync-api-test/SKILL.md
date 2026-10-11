---
description: Brings an existing API test in line with its updated Confluence spec. Finds the delta, stops for a decision, then updates, runs and reviews the test.
argument-hint: <test-case-id, e.g. BS-014>
disable-model-invocation: true
---
Bring the API test for test case $ARGUMENTS in line with its spec.

1. Read the current state.
    - Find the spec of $ARGUMENTS in Confluence (space Uiandapite, under
      Test Project › API) and read it in full. If there is no such page,
      stop and say so.
    - Search src/test/java for "$ARGUMENTS:" and read the test. If there is
      no test with this ID, stop and say so: /new-api-test is for new tests.
    - Run the current test once before changing anything, and note whether
      it passes. A passing test shows how the application behaves today.
2. Find the delta between the spec and the test. Sort every difference:
    - behavioural: anything that changes what is sent or checked — steps,
      inputs, preconditions, status codes, error codes, messages;
    - editorial: wording, typos, formatting — nothing the test checks.
      If there are no behavioural differences, report "In sync: nothing to
      change", list the editorial ones, and stop.
3. Stop and show the behavioural delta. For each item give three things:
   what the spec says, what the test checks, and what the application does
   today. If an item looks like a mistake in the spec rather than a real
   change (for example, the application and the test agree, or it
   contradicts similar test cases), say so. Wait for the user to decide,
   item by item, whether to update the test. Change nothing before that.
4. Update the test as decided, following CLAUDE.md and the rules files.
   Reuse existing ApiSteps methods and test data; add new ones only where
   needed.
5. List the suite XMLs that run this test. Do not change any suite XML.
6. Run only this test. Fix failures caused by the test code. If it fails
   because the application behaves differently from the spec, stop and
   report it. Never change an expected result just to make the test pass.
7. Use the convention-reviewer subagent to review the current diff. Tell
   it only what to review; do not tell it which findings to expect, accept
   or ignore. Fix the rule violations it reports; list spec mismatches and
   other problems without fixing them.
8. Short summary: the delta found, what changed, test result, reviewer
   result, the suites that run the test, open