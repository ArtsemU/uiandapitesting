# 0003. BasePage helpers take locators, not found elements

Date: 2026-11-15
Status: Accepted
Related: 1. XPath for all locators
Context

BasePage helpers (click, sendKeys, getText) accepted an already found WebElement and waited only for it to become clickable. ADR 1 requires locators to be scoped to a container, so pages first found the container with a bare driver.findElement, outside any wait. When the container appeared only after an action (a modal after Add, a tree node after expand), that lookup failed at once with NoSuchElementException.

This caused flaky nightly UI runs (WT-003, WT-004, CB-003), always in headless CI, always within about 1.5 s against a 10 s wait. The same pattern was found in 7 more places that simply had not failed yet. The code was written by the agent following the rules as they stood, so the gap was in the design, not in a single page.

Two alternatives were considered:

An implicit wait. One line, and every lookup waits. Rejected: mixed with the explicit waits used everywhere else, it produces unpredictable timeouts.
Wait for the container before each scoped lookup (waitForVisible, the fix applied on 2026-11-15). It works, but only as long as every new page remembers to call it. A rule that depends on remembering is followed at random by an agent, for the same reason ADR 1 gives.
Decision
BasePage helpers take a By and find the element inside the wait.
A found WebElement is not kept between actions.
Container scoping is done in the locator itself (one XPath: container + element), not by a separate findElement.
No implicit waits.
Consequences
Every lookup and action waits by construction, so this class of flakiness can no longer be written, by hand or by the agent.
It also prevents StaleElementReferenceException after React re-renders, since nothing holds an old element reference.
Scoped XPath locators get longer. This is the accepted cost.
Page objects need a refactor to the new helpers (separate task). Until then the waitForVisible / waitForInvisible fix stays in place.
Do not reintroduce helpers that take a WebElement, and do not propose an implicit wait. Both have been considered and rejected here.
Same model as Playwright's Locator, which eases the phase 2 port.