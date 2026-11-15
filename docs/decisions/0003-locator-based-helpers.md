# 0003. BasePage helpers take locators, not found elements

Status: Accepted (2026-11-15)

## Context
BasePage helpers (click, sendKeys, getText) accepted an already found
WebElement and waited only for its clickability. Pages scoped locators to
a container, so they found the container with a bare driver.findElement
first. When the container appeared after an action (a modal after Add, a
tree node after expand), that lookup failed at once with
NoSuchElementException. It caused flaky nightly UI runs (WT-003, WT-004,
CB-003) and the same pattern was found in 7 more places.

## Decision
- BasePage helpers take a By and find the element inside the wait.
- A found WebElement is not kept between actions.
- Container scoping is done in the locator itself (one xpath:
  container + element), not by a separate findElement.
- No implicit waits.

## Consequences
- Lookups and actions always wait, so this class of flakiness can no
  longer be written by hand or by the agent.
- Also prevents StaleElementReferenceException after React re-renders.
- Page objects need a refactor to the new helpers (separate task).
- Same model as Playwright's Locator, which eases the phase 2 port.