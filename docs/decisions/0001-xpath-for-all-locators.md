# 1. XPath for all locators

- **Date:** 2026-08-06
- **Status:** Accepted

## Context

Generated and hand-written code drifted into a mix of CSS selectors and XPath
with no rule behind the choice. Reviewing it, the mix was not the result of
anyone weighing trade-offs — it was whichever syntax came to mind first.

The obvious rule to write down would be "CSS by default, XPath where CSS cannot
express the query". It is technically the better rule: CSS is shorter for the
common cases and slightly faster.

It was rejected for one reason. A rule with exceptions is only as good as the
least disciplined person applying it. On a project of any size that produces
exactly the drift above: one person picks CSS, another walks XPath axes where an
id would have done, and a year later both syntaxes are scattered through the
codebase with no way to tell intent from accident.

The same applies with more force to AI agents generating code here. An agent has
no judgement about when an exception is warranted. A single unconditional rule is
followed consistently; a conditional one is followed at random.

## Decision

XPath for all locators. No exceptions for "simple" cases.

Locators are always scoped to a container rather than left global.

## Consequences

- Simple lookups get longer than they need to be: `//button[@id='submit']`
  instead of `#submit`. This is the accepted cost, not an oversight.
- Do not propose replacing XPath with CSS as a simplification. It has been
  considered and rejected here.
- The performance difference between XPath and CSS is real but negligible at
  this project's scale. It is not a reason to reopen this.
- Scoping is not optional. demoQA reuses the same id in the input form and in
  the output block — an unscoped locator silently resolves to the wrong element
  and reads as an empty string rather than failing.