# Internet Banking — Statement: story review before test design

Source: `docs/stories/en_internet_banking_statement_task.md` (the only source of truth for this review).
No test cases are written here and no answers are assumed. Each finding lists the question to put to the analyst.

Severity:
- **Critical** — no expected result can be written without an answer.
- **Major** — testable only on an assumption that the story does not support.
- **Minor** — does not block test design, but leaves gaps in coverage or wording.

---

## Critical

### C1. Sorting is by date, but the table has no date column
- **Refers to:** "Results Table" — the five listed columns; "sorted **by date in descending order**".
- **Why it blocks:** none of the columns (Sequence number, Recipient, Payer, Title, Amount with currency) shows a date. The sort order cannot be checked in the UI, so no expected result is possible for the sorting requirement.
- **Question:** Is a Date (or Date and time) column missing from the list? If not, how should the tester verify the order — and is the date shown anywhere else?

### C2. The periods are not defined
- **Refers to:** "Select period" values: Current day, Week, Month, Year.
- **Why it blocks:** the expected set of transactions depends entirely on the period boundaries, which are not given. "Week" can be the calendar week (from Monday or Sunday?) or the last 7 days; "Month" can be the calendar month or the last 30/31 days; "Year" can be the calendar year or the last 365 days. It is not said whether today is included or whether the bounds are inclusive.
- **Question:** For each value, what are the exact start and end of the period (calendar or rolling, first day of the week, inclusive bounds, is today included)?

### C3. Time zone and the "Current day" boundary
- **Refers to:** "Current day".
- **Why it blocks:** a transaction at 23:30 or 00:30 can fall in or out of the period depending on whose clock is used. Without this, boundary tests have no expected result.
- **Question:** Which time zone defines the day: the bank's server, the user's browser, or the account's country? Which timestamp of a transaction counts (created, booked, value date)?

### C4. Which transactions are included
- **Refers to:** the story as a whole — it never says what the statement is made of.
- **Why it blocks:** the expected content of the table cannot be stated. Unknown: which account(s) — the user may have several and there is no account selector; incoming and outgoing or only one direction; status of transactions (pending, completed, declined, cancelled, reversed); card transactions, fees, internal transfers between the user's own accounts.
- **Question:** Which accounts and which transaction types and statuses appear in the statement? If the user has several accounts, how is the account chosen?

### C5. What the section shows when it opens
- **Refers to:** "Default value: **Week**" and "The search runs **only after clicking the Search button**".
- **Why it blocks:** two readings are possible: (a) the section opens with an empty table and the user must click Search; (b) the section opens with results for the default period "Week" already loaded. The expected first screen differs completely.
- **Question:** On opening "Statement", is the table empty until Search is clicked, or is a search for "Week" run automatically?

### C6. No result is defined for an empty search
- **Refers to:** "Search — runs the search for the selected period."
- **Why it blocks:** for a period with no transactions there is no expected result — an empty table, an empty table with headers, a "No transactions found" message (with what text?).
- **Question:** What does the user see when the search returns no transactions? Is there a message, and what is its exact text?

### C7. "Sequence number" is undefined
- **Refers to:** column 1, "Sequence number".
- **Why it blocks:** it may be a row counter (1, 2, 3 … in display order), a transaction ID from the bank, or a document number. The expected value in each row cannot be written.
- **Question:** What is the Sequence number — a row counter in display order, or an identifier of the transaction? If a counter, does it start from 1 on every search (and on every page, if there is paging)?

### C8. "Amount with currency" — format and sign
- **Refers to:** column 5, "Amount with currency".
- **Why it blocks:** the expected display of an amount is unknown: sign or colour for debit vs credit; decimal and thousands separators; number of decimals; currency as ISO code (EUR), symbol (€) or name; position of the currency; how a mix of currencies in one result is shown.
- **Question:** What is the exact display format of an amount, and how are incoming and outgoing amounts told apart? Can one statement contain several currencies?

### C9. What "Clear" resets
- **Refers to:** "Clear — clears the results table."
- **Why it blocks:** it is not said whether Clear also resets the period dropdown to "Week", or whether it only removes the rows. After Clear, the expected state of the filter is unknown.
- **Question:** After Clear, does the dropdown keep the selected value or return to "Week"? Does the table show the empty-state message from C6 or a different state?

---

## Major

### M1. Recipient and Payer content
- **Refers to:** columns 2 and 3.
- **Why it weakens testing:** it is not said what is shown — name, account number (IBAN, masked?), both. For the user's own transactions one of the two fields is the user; it is not said whether it is shown as their name or as "Me"/the account.
- **Question:** What exactly is displayed in Recipient and Payer, and how is the current user shown in their own transactions?

### M2. "Title" meaning and long values
- **Refers to:** column 4, "Title".
- **Why it weakens testing:** "Title" may be the payment purpose entered by the payer, a transaction type, or a merchant name. Nothing is said about long or missing values (truncation, tooltip, wrapping, empty cell).
- **Question:** What is the source of Title? How are long, empty or special-character values displayed?

### M3. Large result sets — paging or limit
- **Refers to:** "Year" period and "Results Table".
- **Why it weakens testing:** a year can contain hundreds of transactions. It is not said whether there is pagination, infinite scroll, a maximum number of rows, or a warning.
- **Question:** Is there paging or a row limit? If so, how many rows per page and how does sorting and the Sequence number behave across pages?

### M4. Sorting rules beyond the default
- **Refers to:** "By default, the table results are sorted by date in descending order".
- **Why it weakens testing:** "by default" implies another sort is possible, but no other sort is described. The order of transactions with the same timestamp is not defined, so the expected order of such rows is not fixed.
- **Question:** Can the user re-sort by clicking column headers? If so, which columns and how? What is the tie-breaker for transactions with the same date and time?

### M5. Dropdown changed after a search — stale results
- **Refers to:** "Changing the dropdown value does not trigger a search on its own."
- **Why it weakens testing:** after a search for "Week" the user selects "Month" without clicking Search. The table still shows the "Week" results under a filter that says "Month". It is not said whether this is intended or whether there is any hint that the results are out of date.
- **Question:** Is it accepted that the table and the dropdown can disagree until Search is clicked? Should the old results be cleared or marked?

### M6. Errors and unavailable service
- **Refers to:** not covered by the story.
- **Why it weakens testing:** no behaviour is defined for a server error, timeout, lost connection, or an expired session during a search.
- **Question:** What does the user see on each of these errors (message text, retry option, redirect to login on session expiry)?

### M7. Loading state and repeated clicks
- **Refers to:** "Search" button.
- **Why it weakens testing:** it is not said whether there is a loading indicator, whether Search is disabled while a request runs, or what happens on double click or on clicking Search again with the same period.
- **Question:** Is there a loading indicator? Is Search (and Clear) disabled during a request? Does a repeated search with the same period refresh the data?

### M8. Button states
- **Refers to:** "Buttons".
- **Why it weakens testing:** it is not said whether Clear is enabled when the table is already empty, or whether Search is always enabled.
- **Question:** When are Search and Clear enabled or disabled?

### M9. Access and visibility of the menu item
- **Refers to:** "a new **Statement** option appears in the left menu".
- **Why it weakens testing:** it is not said whether every logged-in user sees it — users with no accounts, closed or blocked accounts, restricted roles (e.g. view-only, business users).
- **Question:** Which users see "Statement"? What do users with no accounts or no rights see?

### M10. "Window" — page, panel or pop-up
- **Refers to:** "Navigating to the Statement section opens a window consisting of two blocks".
- **Why it weakens testing:** a modal, a new browser window and a content area of the same page have different expected behaviour (close button, back button, URL, focus).
- **Question:** Is "window" a section of the page, a modal dialog, or a new browser tab/window? How is it closed or left?

### M11. State after leaving and returning
- **Refers to:** not covered by the story.
- **Why it weakens testing:** after going to another menu item and back, or after a page refresh, it is unknown whether the selected period and the results are kept.
- **Question:** Are the filter and results kept on navigation and refresh, or reset to the opening state?

### M12. Data freshness
- **Refers to:** not covered by the story.
- **Why it weakens testing:** it is not said whether a transaction made a minute ago appears in "Current day" immediately or after booking/processing.
- **Question:** How soon after a transaction is made does it appear in the statement?

---

## Minor

### m1. Exact labels and texts
- **Refers to:** "Statement", "Select period", "Search", "Clear", the period values and column names.
- **Why it weakens testing:** it is unclear whether these are the final UI texts (case, punctuation) or working names, and whether other languages are supported.
- **Question:** Are the quoted names the exact UI texts? Which languages must be supported?

### m2. Position of the menu item and order of values
- **Refers to:** "left menu"; dropdown value list.
- **Why it weakens testing:** the place of "Statement" in the menu and whether the dropdown order is fixed are not stated.
- **Question:** Where in the left menu does "Statement" appear? Is the dropdown order as listed?

### m3. Custom date range
- **Refers to:** "Select period" — only four fixed values.
- **Why it weakens testing:** a free date range is usually expected in a bank statement. Without a statement that it is out of scope, it is unclear whether its absence is a defect.
- **Question:** Is a custom date range out of scope for this release?

### m4. Export, print, download
- **Refers to:** not covered by the story.
- **Why it weakens testing:** statements are usually downloadable (PDF, CSV) or printable. Absence is not confirmed as intended.
- **Question:** Is export or printing of the statement in scope? If yes, which formats and contents?

### m5. Transaction details
- **Refers to:** "Results Table".
- **Why it weakens testing:** it is not said whether a row can be opened to see full details.
- **Question:** Is a row clickable, and if so, what does it show?

### m6. Login preconditions
- **Refers to:** "the user logs in to the client side with a login and password".
- **Why it weakens testing:** it is not said whether login is in scope of this story or only a precondition, nor whether a second factor is involved.
- **Question:** Is login only a precondition here? Is there any second factor that affects access to "Statement"?

### m7. Release scope — "As part of the update"
- **Refers to:** "As part of the update".
- **Why it weakens testing:** it is unclear whether the feature is behind a flag or rolled out to a subset of users, which affects test environments and the M9 checks.
- **Question:** Is "Statement" released to all users at once or behind a feature flag?

### m8. Non-functional expectations
- **Refers to:** not covered by the story.
- **Why it weakens testing:** no response time (especially for "Year"), supported browsers, mobile/responsive layout or accessibility requirements are stated.
- **Question:** Are there requirements for response time, browsers, screen sizes and accessibility?

### m9. Security of displayed data
- **Refers to:** Recipient / Payer columns.
- **Why it weakens testing:** it is not said whether account numbers or names are masked, or whether opening a statement is logged for audit.
- **Question:** Must any data in the table be masked? Is access to the statement audited?

---

## Summary

| Severity | Count |
|----------|-------|
| Critical | 9     |
| Major    | 12    |
| Minor    | 9     |

The most urgent answers are C1 (no date column for date sorting), C2–C4 (what data and which period boundaries define the expected table) and C5 (opening state). Without them no test case for the Statement table can have a clear expected result.
