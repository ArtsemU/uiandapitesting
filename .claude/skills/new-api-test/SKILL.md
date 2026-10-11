---
description: Writes a new API test for a Bookstore test case from its Confluence spec, runs it and has it reviewed.
argument-hint: <test-case-id, e.g. BS-014>
disable-model-invocation: true
---
Write a new API test for test case $ARGUMENTS.

1. Find the spec. Search Confluence (space Uiandapite, under Test Project ›
   API) for the page of test case $ARGUMENTS and read it in full. If there
   is no such page, stop and say so.
2. Check before writing.
    - Search src/test/java for "$ARGUMENTS:". If a test with this ID already
      exists, stop and say so: this skill only adds new tests.
    - If the spec is unclear, incomplete, or contradicts the code or the
      rules, stop, list your questions and wait for answers. Do not invent
      steps or expected values.
3. Write the test in the matching <Area>ApiTests class, following CLAUDE.md
   and the rules files. Reuse existing ApiSteps methods and test data; add
   new ones only where the spec needs them.
4. Check whether the existing suite XMLs already pick the new test up (by
   class or by group). Change them only if they do not.
5. Run only the new test: mvn test -Dtest=<Class>#<method>. Fix failures
   caused by the test code. If it fails because the application behaves
   differently from the spec, stop and report it. Never change an expected
   result just to make the test pass.
6. Use the convention-reviewer subagent to review the current diff. Fix the
   rule violations it reports; list spec mismatches and other problems
   without fixing them.
7. Short summary: the test added, test result, reviewer result, open
   questions.