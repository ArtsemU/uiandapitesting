# Internet Banking — Statement: Test Documentation

Source: `docs/stories/en_internet_banking_statement_task.md` (the only source of truth).

## 1. Description

The Statement feature adds a "Statement" item to the left menu of the internet
banking client side, behind a feature flag, and is available to all logged-in
clients. On the Statement page the client picks a calendar period (Today, This
week, This month, This year) and clicks "Search" to see their completed
transactions for that period in a sortable table, with pagination for 21 or more
results. We test the menu item and feature flag, the filter block, period
boundaries in the Europe/Warsaw time zone (including daylight saving time), the
search and loading state, the table content and formats, sorting, pagination,
the "Clear" action and the page's default state. The UI only displays and
formats transaction data from the API, so every expected value comes from the
seeded test data.

## 2. Technical information

| Item | Value |
| --- | --- |
| Branch | master |
| Environment | QA |
| Browser / resolution | Chrome, Full HD (1920×1080) — story Constraints |
| UI language | English | 

## 3. Scope

### In scope

- "Statement" left-menu item and loading of the page in the main content area (AC1).
- Feature flag off: hidden menu item, redirect on direct URL (AC2).
- Default page state on opening, after refresh and after leaving and coming back (AC3, AC15).
- Filter block: element order, period dropdown values, button availability (AC4, AC5).
- Search: spinner and blocked interaction, status filtering, default sort, repeated search, stale filter (AC6, AC8).
- Period boundaries in Europe/Warsaw, both sides of a DST change (AC7).
- Empty result (AC9).
- Table columns, date format, empty values, title cut-off, amount format and colour (AC10, AC11).
- Sorting by Date (AC12).
- Pagination (AC13) — **blocked until the pagination module is delivered**; the interim "first 20 rows" behaviour is tested now.
- Clear (AC14).

### Out of scope (as listed in the story)

- Custom date range.
- Export, print, download.
- Transaction details view.
- Error handling and unavailable-service behaviour — the standard application behaviour applies.
- Data freshness and other non-functional requirements.
- Feature-flag rollout plan.

## 4. Assumptions and open questions

### Assumptions

| ID | Assumption | Test cases that change if it is wrong |
| --- | --- | --- |
| A1 | The QA environment lets us fix the current date and time used as the "moment of search" (test clock) and seed transactions with any creation timestamp, status, direction, type, amount, currency, recipient, payer and title for a test client. | All cases that use seeded data; without a test clock ST-018 – ST-031 can only run on the matching real calendar dates. |
| A2 | The Date column drops seconds (truncation, not rounding): 14:05:59 is shown as `14:05`. | ST-041 |
| A3 | "Empty" Recipient, Payer or Title means `null` or an empty string; both are shown as `—`. | ST-042, ST-043 |
| A4 | The API returns the amount without a sign plus a direction (debit/credit); the minus sign is part of the formatting. Test data is defined that way. | ST-044 – ST-046 (test data only; expected display does not change) |
| A5 | The story gives no colour codes; "green" and "red" are checked as the computed text colour being a green / red hue, consistent across rows. | ST-044 – ST-046 |
| A6 | Pagination buttons do what their names say: First → page 1, Previous → current − 1, Next → current + 1, Last → last page. | ST-053 – ST-057 |
| A7 | The statement API response can be delayed in QA (proxy, stub or network throttling) so the loading state can be observed. | ST-012 |
| A8 | "No search is run" and "the data is re-queried" are observed through the presence or absence of the statement API request in the browser network log. | ST-007, ST-011, ST-032 – ST-034, ST-047, ST-048, ST-064, ST-065 |
| A9 | Title length is counted in characters; test titles use plain ASCII text. | ST-043, ST-039 |

### Open questions (no test cases written for these gaps)

| ID | Question | Effect |
| --- | --- | --- |
| Q1 | In ascending order, are transactions with the same date and time still ordered by ID descending, or by ID ascending? | No tie-break case for ascending order; ST-016 checks descending only. |
| Q2 | Is "same creation date and time" compared to the second (full timestamp) or to the displayed minute? | Tie cases use identical full timestamps; ordering cases use different minutes. |
| Q3 | Which accounts does the statement cover for a client with several accounts? | All cases use a test client with one account; no multi-account case. |
| Q4 | Does "all interaction on the page is blocked" include the left menu and browser navigation? | ST-012 checks only the Statement page controls. |
| Q5 | How are amounts with more than two decimals, or currencies with 0 or 3 minor units, displayed (rounding, truncation)? | No case; test amounts have at most two decimals. |
| Q6 | Is a whitespace-only Recipient, Payer or Title shown as `—` or as blank? | No case. |
| Q7 | AC10 says values "match the transaction data in the database"; the Results Table section says they come from the API. Which is the oracle? | Cases compare with the seeded test data, which is the same in both if the API returns the stored data. |
| Q8 | The Pagination "Dependency" sentence is truncated (starts with "…"). What is the full text? | Interim cases ST-049, ST-051 use only the visible part and the AC13 note. |
| Q9 | "Available to all clients": are there client segments (e.g. retail, business) that must be covered? | ST-004 uses a second client only. |

## 5. Test cases

### Common preconditions (apply to every case unless stated otherwise)

- QA environment, branch master, Chrome, Full HD.
- Feature flag **on**.
- Test client **C1** (one account) is logged in.
- Test clock (A1) set to **Thu 15.10.2026 12:00:00 Europe/Warsaw** (2026-10-15T10:00:00Z, CEST, UTC+02:00).
- Browser DevTools network log open where a case checks requests (A8).

### Test data set D1 (client C1)

Timestamps are stored in UTC; "Shown" is the expected Date column value in Europe/Warsaw.

| ID | Created (UTC) | Shown | Status | Direction | Amount | Currency | Recipient | Payer | Title |
| --- | --- | --- | --- | --- | --- | --- | --- | --- | --- |
| 1001 | 2026-10-15T08:30:00Z | 15.10.2026 10:30 | completed | credit | 123.25 | USD | Jan Kowalski | ACME Sp. z o.o. | Salary October |
| 1002 | 2026-10-14T16:45:00Z | 14.10.2026 18:45 | completed | debit | 45.05 | EUR | Electro Shop | Jan Kowalski | Order 5531 |
| 1003 | 2026-10-13T07:05:00Z | 13.10.2026 09:05 | completed | debit | 1000 | PLN | Anna Nowak | Jan Kowalski | Rent for October 2026 flat 12 |
| 1004 | 2026-10-12T09:00:00Z | 12.10.2026 11:00 | completed | credit | 0.5 | PLN | Jan Kowalski | *(empty)* | *(empty)* |
| 1005 | 2026-10-14T10:00:00Z | — | pending | debit | 10.00 | PLN | Shop A | Jan Kowalski | Pending one |
| 1006 | 2026-10-14T11:00:00Z | — | declined | debit | 20.00 | PLN | Shop B | Jan Kowalski | Declined one |
| 1007 | 2026-10-14T12:00:00Z | — | reversed | debit | 30.00 | PLN | Shop C | Jan Kowalski | Reversed one |
| 1008 | 2026-10-05T10:00:00Z | 05.10.2026 12:00 | completed | debit | 15.00 | PLN | Café Nord | Jan Kowalski | Coffee |

Expected "This week" result for D1 (Date descending):

| Date | Recipient | Payer | Title | Amount |
| --- | --- | --- | --- | --- |
| 15.10.2026 10:30 | Jan Kowalski | ACME Sp. z o.o. | Salary October | `123.25 USD` (green) |
| 14.10.2026 18:45 | Electro Shop | Jan Kowalski | Order 5531 | `-45.05 EUR` (red) |
| 13.10.2026 09:05 | Anna Nowak | Jan Kowalski | Rent for October 202 | `-1000.00 PLN` (red) |
| 12.10.2026 11:00 | Jan Kowalski | — | — | `0.50 PLN` (green) |

Expected "This month" result for D1: the four rows above plus `05.10.2026 12:00 | Café Nord | Jan Kowalski | Coffee | -15.00 PLN` as the last row.

---

### AC1 — Menu item

Technique: equivalence partitioning (feature flag on; different clients).

#### ST-001 — "Statement" item is shown in the left menu
- **AC:** AC1 · **Priority:** High
- **Preconditions:** Common.
- **Steps:** 1. Look at the left menu.
- **Expected result:** The left menu contains the item "Statement".

#### ST-002 — Clicking "Statement" loads the page in the main content area
- **AC:** AC1 · **Priority:** High
- **Preconditions:** Common; another page is open.
- **Steps:** 1. Click "Statement" in the left menu.
- **Expected result:** The Statement page (filter block and results table) is loaded in the main content area, the same way as other menu items; no pop-up or side panel opens; the left menu stays visible.

#### ST-003 — Statement is not the default page after login
- **AC:** Story › Description · **Priority:** Medium
- **Preconditions:** Feature flag on; C1 is logged out.
- **Steps:** 1. Log in as C1.
- **Expected result:** The default page after login is shown, not the Statement page; "Statement" is present in the left menu.

#### ST-004 — "Statement" is available to another client
- **AC:** AC1, Story › Description ("available to all clients"); see Q9 · **Priority:** Medium
- **Preconditions:** Feature flag on; test client C2 (different from C1) is logged in.
- **Steps:** 1. Look at the left menu. 2. Click "Statement".
- **Expected result:** "Statement" is in the left menu and the Statement page loads in the main content area.

### AC2 — Feature flag off

Technique: equivalence partitioning (feature flag off).

#### ST-005 — Menu item is hidden when the flag is off
- **AC:** AC2 · **Priority:** High
- **Preconditions:** Feature flag **off**; C1 is logged in.
- **Steps:** 1. Look at the left menu.
- **Expected result:** The left menu does not contain "Statement".

#### ST-006 — Direct URL redirects to the default page when the flag is off
- **AC:** AC2 · **Priority:** High
- **Preconditions:** Feature flag **off**; C1 is logged in; the Statement page URL is known (taken with the flag on).
- **Steps:** 1. Open the Statement page URL in the address bar.
- **Expected result:** The browser is redirected to the default page after login; the Statement page is not shown.

### AC3 — Default state on opening

Technique: state transition (initial state "Opened").

#### ST-007 — Default state when the page is opened
- **AC:** AC3, Story › Page State, Empty Table · **Priority:** High
- **Preconditions:** Common; D1 seeded (C1 has transactions this week).
- **Steps:** 1. Click "Statement" in the left menu.
- **Expected result:** "Select period" shows "This week"; the table shows "No trx found"; no pagination block; no statement API request is sent (A8).

### AC4 — Filter block layout and buttons

Techniques: none for ST-008 (layout check); state transition for ST-009.

#### ST-008 — Filter block element order
- **AC:** AC4 · **Priority:** Medium
- **Preconditions:** Common; Statement page open.
- **Steps:** 1. Look at the filter block.
- **Expected result:** Elements appear in this order: "Select period" dropdown, "Search" button, "Clear" button. Labels are in English.

#### ST-009 — "Search" and "Clear" are enabled in every state without a search in progress
- **AC:** AC4, Story › Buttons · **Priority:** High
- **Preconditions:** Common; D1 seeded without transaction 1001 (so C1 has no transactions today).
- **Steps:**
  1. Open the Statement page (state: Default). Check both buttons.
  2. Select "This month" without searching (state: Selection changed). Check both buttons.
  3. Click "Search" and wait for completion (state: Results). Check both buttons.
  4. Select "Today" and click "Search" (state: Empty result). Check both buttons.
  5. Click "Clear" (state: Cleared). Check both buttons.
- **Expected result:** In every state both "Search" and "Clear" are enabled.

### AC5 — Period selection

Technique: equivalence partitioning (each of the four dropdown values).

#### ST-010 — Dropdown values and order
- **AC:** AC5, Story › Period Selection · **Priority:** High
- **Preconditions:** Common; Statement page open.
- **Steps:** 1. Open the "Select period" dropdown.
- **Expected result:** The dropdown contains exactly "Today", "This week", "This month", "This year", in this order, and nothing else.

#### ST-011 — Selecting a value does not run a search
- **AC:** AC5 · **Priority:** Medium
- **Preconditions:** Common; D1 seeded; Statement page open in the default state.
- **Steps:** 1. Select "Today". 2. Select "This month". 3. Select "This year". 4. Select "This week". Check the table and network log after each step.
- **Expected result:** The dropdown shows the selected value after each step; the table keeps showing "No trx found"; no statement API request is sent.

### AC6 — Search

Techniques: equivalence partitioning (transaction status; direction/type); state transition (Searching → Results; sort order after a new search).

#### ST-012 — Spinner is shown and the page is blocked during a search
- **AC:** AC6, Story › Loading State, Buttons; see A7, Q4 · **Priority:** High
- **Preconditions:** Common; D1 seeded; Statement page open; "This week" search already run once (table shows results).
- **Steps:**
  1. Set a 5-second delay on the statement API response (A7).
  2. Select "This month" and click "Search".
  3. While the spinner is shown: try to open the dropdown, click "Search", click "Clear", click the Date column header.
  4. Wait for the search to complete.
- **Expected result:** A spinner is shown right after step 2. During step 3 nothing reacts: the dropdown does not open, no second request is sent, the dropdown is not reset, the sort order does not change. After completion the spinner disappears, the table shows the "This month" result for D1, and both buttons are enabled.

#### ST-013 — Only completed transactions are shown
- **AC:** AC6, Story › Results Table · **Priority:** High
- **Preconditions:** Common; D1 seeded (1005 pending, 1006 declined, 1007 reversed are all within this week).
- **Steps:** 1. Keep "This week". 2. Click "Search".
- **Expected result:** The table shows exactly the four rows of the expected "This week" result for D1; transactions 1005, 1006 and 1007 are not shown.

#### ST-014 — Completed transactions of different types and both directions are shown
- **AC:** AC6, Story › Results Table ("all types") · **Priority:** Medium
- **Preconditions:** Common; for C1 seed, within this week, one completed transaction of each transaction type available in the QA test data, with at least one credit and one debit.
- **Steps:** 1. Keep "This week". 2. Click "Search".
- **Expected result:** Every seeded completed transaction is shown once, whatever its type and direction, with values matching the test data.

#### ST-015 — Results are sorted by Date descending after a search
- **AC:** AC6, Story › Sorting · **Priority:** High
- **Preconditions:** Common; D1 seeded.
- **Steps:** 1. Keep "This week". 2. Click "Search".
- **Expected result:** Rows appear in the order 15.10.2026 10:30, 14.10.2026 18:45, 13.10.2026 09:05, 12.10.2026 11:00; the Date header arrow shows descending order.

#### ST-016 — Same creation date and time are ordered by transaction ID descending
- **AC:** Story › Sorting; AC6; see Q1, Q2 · **Priority:** Medium
- **Preconditions:** Common; for C1 seed three completed transactions with the identical timestamp 2026-10-14T08:00:00Z: ID 2001 (title "Tie A"), ID 2003 ("Tie C"), ID 2002 ("Tie B").
- **Steps:** 1. Keep "This week". 2. Click "Search".
- **Expected result:** The three rows (all `14.10.2026 10:00`) appear in the order "Tie C", "Tie B", "Tie A".

#### ST-017 — A new search shows results sorted by Date descending again
- **AC:** AC6, AC12 · **Priority:** Medium
- **Preconditions:** Common; D1 seeded; "This week" search run.
- **Steps:** 1. Click the Date header (order becomes ascending). 2. Click "Search".
- **Expected result:** After step 2 the rows are in Date descending order and the arrow shows descending.

### AC7 — Period boundaries

Techniques: boundary value analysis (23:59:59 of the day before vs 00:00:00 of the period start); equivalence partitioning (CET side, CEST side, period crossing a DST change).

For every case: seed for C1 two completed credit transactions **TB** ("Before", 1 second before the period start) and **TS** ("Start", exactly at the period start), set the test clock, select the period, click "Search".
DST 2026 in Europe/Warsaw: starts Sun 29.03.2026 (02:00 CET → 03:00 CEST), ends Sun 25.10.2026 (03:00 CEST → 02:00 CET).

| Case | Period | Clock (Warsaw) | Offset at start / at search | TB (UTC) | TS (UTC) | Expected |
| --- | --- | --- | --- | --- | --- | --- |
| ST-018 | Today | 15.10.2026 12:00 | CEST / CEST | 2026-10-14T21:59:59Z | 2026-10-14T22:00:00Z | TS shown as `15.10.2026 00:00`; TB not shown |
| ST-019 | Today | 10.11.2026 12:00 | CET / CET | 2026-11-09T22:59:59Z | 2026-11-09T23:00:00Z | TS shown as `10.11.2026 00:00`; TB not shown |
| ST-020 | This week | Thu 15.10.2026 12:00 | CEST / CEST | 2026-10-11T21:59:59Z | 2026-10-11T22:00:00Z | TS shown as `12.10.2026 00:00`; TB not shown |
| ST-021 | This week | Thu 12.11.2026 12:00 | CET / CET | 2026-11-08T22:59:59Z | 2026-11-08T23:00:00Z | TS shown as `09.11.2026 00:00`; TB not shown |
| ST-022 | This month | 15.10.2026 12:00 | CEST / CEST | 2026-09-30T21:59:59Z | 2026-09-30T22:00:00Z | TS shown as `01.10.2026 00:00`; TB not shown |
| ST-023 | This month | 10.11.2026 12:00 | CET / CET | 2026-10-31T22:59:59Z | 2026-10-31T23:00:00Z | TS shown as `01.11.2026 00:00`; TB not shown |
| ST-024 | This year | 15.10.2026 12:00 | CET / CEST | 2025-12-31T22:59:59Z | 2025-12-31T23:00:00Z | TS shown as `01.01.2026 00:00`; TB not shown |
| ST-025 | This year | 10.02.2026 12:00 | CET / CET | 2025-12-31T22:59:59Z | 2025-12-31T23:00:00Z | TS shown as `01.01.2026 00:00`; TB not shown |
| ST-026 | This week (crosses end of DST) | Sun 25.10.2026 12:00 | CEST / CET | 2026-10-18T21:59:59Z | 2026-10-18T22:00:00Z | TS shown as `19.10.2026 00:00`; TB not shown |
| ST-027 | This month (crosses start of DST) | 31.03.2026 12:00 | CET / CEST | 2026-02-28T22:59:59Z | 2026-02-28T23:00:00Z | TS shown as `01.03.2026 00:00`; TB not shown |
| ST-028 | Today (DST start day) | 29.03.2026 12:00 | CET / CEST | 2026-03-28T22:59:59Z | 2026-03-28T23:00:00Z | TS shown as `29.03.2026 00:00`; TB not shown |
| ST-029 | Today (DST end day) | 25.10.2026 12:00 | CEST / CET | 2026-10-24T21:59:59Z | 2026-10-24T22:00:00Z | TS shown as `25.10.2026 00:00`; TB not shown |
| ST-030 | This week (search on Monday) | Mon 12.10.2026 09:00 | CEST / CEST | 2026-10-11T21:59:59Z | 2026-10-11T22:00:00Z | TS shown as `12.10.2026 00:00`; TB (Sunday 23:59:59) not shown |

- **AC:** AC7, Story › Period Selection (boundaries, time zone) · **Priority:** High for ST-018 – ST-027; Medium for ST-028 – ST-030
- **Preconditions:** Common, with the clock and data from the table row.
- **Steps:** 1. Set the clock. 2. Seed TB and TS. 3. Open the Statement page. 4. Select the period. 5. Click "Search".
- **Expected result:** As in the "Expected" column: the result contains TS and does not contain TB.

#### ST-031 — Year start on a Thursday: week reaches into the previous year
- **AC:** AC7, Story › Period Selection · **Priority:** Medium
- **Technique:** boundary value analysis (search on the first day of month and year)
- **Preconditions:** Common, but clock set to Thu 01.01.2026 10:00 Warsaw (CET). Seed TW 2025-12-28T23:00:00Z (Mon 29.12.2025 00:00 local), TB 2025-12-31T22:59:59Z (31.12.2025 23:59:59 local), TS 2025-12-31T23:00:00Z (01.01.2026 00:00 local).
- **Steps:** Search with "Today", then "This month", then "This year", then "This week".
- **Expected result:** "Today", "This month" and "This year" each show only TS (`01.01.2026 00:00`). "This week" shows TS, TB (`31.12.2025 23:59`) and TW (`29.12.2025 00:00`), in that order.

### AC8 — Repeated search and stale filter

Technique: state transition (Results → Selection changed → Results).

#### ST-032 — Changing the period after a search does not change the table
- **AC:** AC8, AC5 · **Priority:** High
- **Preconditions:** Common; D1 seeded; "This week" search run (4 rows shown).
- **Steps:** 1. Select "This month". 2. Do not click "Search".
- **Expected result:** The table still shows the 4 "This week" rows; no statement API request is sent; the dropdown shows "This month".

#### ST-033 — Search with the same period re-queries the data
- **AC:** AC8, Story › Buttons · **Priority:** High
- **Preconditions:** Common; D1 seeded; "This week" search run (4 rows shown).
- **Steps:** 1. Seed a new completed credit transaction 2026-10-15T09:00:00Z, 50.00 PLN, title "New one". 2. Keep "This week" and click "Search".
- **Expected result:** A new statement API request is sent; the table shows 5 rows: the new row `15.10.2026 11:00 | … | New one | 50.00 PLN` first, followed by the 4 previous rows.

#### ST-034 — Search with a stale (changed) period applies the new period
- **AC:** AC8 · **Priority:** High
- **Preconditions:** As ST-032, after its step 1 (dropdown "This month", table shows "This week" results).
- **Steps:** 1. Click "Search".
- **Expected result:** A statement API request is sent; the table shows the expected "This month" result for D1 (5 rows, last row `05.10.2026 12:00 … -15.00 PLN`).

### AC9 — Empty result

Techniques: equivalence partitioning (no transactions; only non-completed transactions); state transition (Results → Empty).

#### ST-035 — No transactions in the period
- **AC:** AC9, Story › Empty Table · **Priority:** High
- **Preconditions:** Common; C1 has only transaction 1008 (05.10.2026).
- **Steps:** 1. Keep "This week". 2. Click "Search".
- **Expected result:** The table shows "No trx found"; no pagination block.

#### ST-036 — Only pending, declined and reversed transactions in the period
- **AC:** AC9, AC6 · **Priority:** Medium
- **Preconditions:** Common; C1 has only transactions 1005, 1006, 1007.
- **Steps:** 1. Keep "This week". 2. Click "Search".
- **Expected result:** The table shows "No trx found"; no pagination block.

#### ST-037 — Search with results followed by a search with an empty result
- **AC:** AC9, AC8 · **Priority:** Medium
- **Preconditions:** Common; C1 has only transaction 1008 (05.10.2026).
- **Steps:** 1. Select "This month", click "Search". 2. Select "This week", click "Search".
- **Expected result:** After step 1 one row is shown (`05.10.2026 12:00`). After step 2 the table shows "No trx found"; no pagination block.

### AC10 — Table content

Techniques: equivalence partitioning (value present / empty; CET / CEST); boundary value analysis (title length 19/20/21).

#### ST-038 — Column headers and order
- **AC:** AC10 · **Priority:** High
- **Preconditions:** Common; D1 seeded; "This week" search run.
- **Steps:** 1. Look at the table header.
- **Expected result:** Columns are, in this order: Date, Recipient, Payer, Title, Amount.

#### ST-039 — All row values match the test data
- **AC:** AC10, AC11, Story › Results Table; see Q7 · **Priority:** High
- **Preconditions:** Common; D1 seeded.
- **Steps:** 1. Select "This month". 2. Click "Search". 3. Compare every cell with the expected "This month" result for D1.
- **Expected result:** Every cell of every row equals the expected table built from D1 (5 rows).

#### ST-040 — Date format and Europe/Warsaw time on both sides of DST
- **AC:** AC10, Story › Period Selection (time zone) · **Priority:** High
- **Preconditions:** Common; for C1 seed completed transactions: 2026-03-05T08:07:00Z, 2026-07-15T12:05:00Z, 2026-10-14T22:30:00Z.
- **Steps:** 1. Select "This year". 2. Click "Search".
- **Expected result:** Date column shows `15.10.2026 00:30` (UTC date 14.10 becomes 15.10 local), `15.07.2026 14:05` (UTC+2), `05.03.2026 09:07` (UTC+1, leading zeros kept), in this order; 24-hour format, no seconds.

#### ST-041 — Seconds are not shown and are not rounded
- **AC:** AC10; assumption A2 · **Priority:** Low
- **Preconditions:** Common; for C1 seed a completed transaction at 2026-07-15T12:05:59Z.
- **Steps:** 1. Select "This year". 2. Click "Search".
- **Expected result:** Date shows `15.07.2026 14:05`.

#### ST-042 — Empty Recipient and Payer are shown as "—"
- **AC:** AC10, Story › Results Table; assumption A3 · **Priority:** High
- **Preconditions:** Common; for C1 seed, this week, completed transactions: R1 with Recipient `null`, Payer "ACME"; R2 with Recipient "" (empty string), Payer "ACME"; R3 with Recipient "Anna Nowak", Payer `null`; R4 with Recipient "Anna Nowak", Payer "".
- **Steps:** 1. Keep "This week". 2. Click "Search".
- **Expected result:** The empty Recipient of R1 and R2 and the empty Payer of R3 and R4 are shown as `—`; the filled values are shown as in the data.

#### ST-043 — Title length boundaries and empty title
- **AC:** AC10, Story › Results Table (Title); assumptions A3, A9 · **Priority:** High
- **Preconditions:** Common; for C1 seed, this week, completed transactions with these titles:

  | Title in data | Length | Expected in table |
  | --- | --- | --- |
  | *(empty string)* | 0 | `—` |
  | `A` | 1 | `A` |
  | `ABCDEFGHIJKLMNOPQRS` | 19 | `ABCDEFGHIJKLMNOPQRS` |
  | `ABCDEFGHIJKLMNOPQRST` | 20 | `ABCDEFGHIJKLMNOPQRST` |
  | `ABCDEFGHIJKLMNOPQRSTU` | 21 | `ABCDEFGHIJKLMNOPQRST` |
  | `Payment for invoice 2026/10/0001` | 32 | `Payment for invoice ` (20 characters, trailing space) |

- **Steps:** 1. Keep "This week". 2. Click "Search". 3. Hover over each cut title.
- **Expected result:** Titles are shown as in the "Expected in table" column; no ellipsis; no tooltip appears on hover.

### AC11 — Amount format

Techniques: equivalence partitioning (incoming / outgoing); boundary value analysis (smallest amount, no decimals, large amount).

#### ST-044 — Incoming amount: green, no sign
- **AC:** AC11; assumptions A4, A5 · **Priority:** High
- **Preconditions:** Common; D1 seeded.
- **Steps:** 1. Keep "This week". 2. Click "Search".
- **Expected result:** Row 15.10.2026 10:30 shows `123.25 USD` in green, with no sign.

#### ST-045 — Outgoing amount: red, minus sign
- **AC:** AC11; assumptions A4, A5 · **Priority:** High
- **Preconditions:** Common; D1 seeded.
- **Steps:** 1. Keep "This week". 2. Click "Search".
- **Expected result:** Row 14.10.2026 18:45 shows `-45.05 EUR` in red.

#### ST-046 — Two decimals, dot separator, no thousands separator
- **AC:** AC11, Story › Results Table (Amount) · **Priority:** Medium
- **Preconditions:** Common; for C1 seed, this week, completed transactions:

  | Direction | Amount | Currency | Expected |
  | --- | --- | --- | --- |
  | credit | 0.01 | PLN | `0.01 PLN` (green) |
  | credit | 0.5 | PLN | `0.50 PLN` (green) |
  | debit | 1000 | PLN | `-1000.00 PLN` (red) |
  | credit | 1234567.89 | EUR | `1234567.89 EUR` (green) |
  | debit | 1234567.8 | USD | `-1234567.80 USD` (red) |

- **Steps:** 1. Keep "This week". 2. Click "Search".
- **Expected result:** Amounts are shown as in the "Expected" column: exactly two decimals, dot as the decimal separator, no thousands separator, currency after a single space.

### AC12 — Sorting

Technique: state transition (Descending ⇄ Ascending).

#### ST-047 — Clicking the Date header toggles the order without re-querying
- **AC:** AC12 · **Priority:** High
- **Preconditions:** Common; D1 seeded; "This week" search run (descending).
- **Steps:** 1. Click the Date header. 2. Click the Date header again.
- **Expected result:** After step 1 the rows are 12.10.2026 11:00, 13.10.2026 09:05, 14.10.2026 18:45, 15.10.2026 10:30 and the arrow shows ascending. After step 2 the order is descending again and the arrow shows descending. No statement API request is sent in either step.

#### ST-048 — Other columns are not sortable
- **AC:** AC12 · **Priority:** Medium
- **Preconditions:** Common; D1 seeded; "This week" search run.
- **Steps:** Click the headers Recipient, Payer, Title and Amount, one after another.
- **Expected result:** The row order stays Date descending; no sort arrow appears on these headers; no statement API request is sent.

#### ST-049 — Sorting applies to the whole result set (interim, without pagination)
- **AC:** AC12, AC13 note, Story › Pagination (Dependency); see Q8 · **Priority:** High
- **Preconditions:** Common; for C1 seed 25 completed transactions this week, one per hour from 2026-10-13T00:00:00Z to 2026-10-14T00:00:00Z, titles "T01" (oldest) … "T25" (newest). Pagination module not delivered.
- **Steps:** 1. Keep "This week", click "Search". 2. Click the Date header.
- **Expected result:** After step 1 the table shows "T25" … "T06" (the 20 newest). After step 2 the table shows "T01" … "T20" (the 20 oldest), ascending.

#### ST-050 — Sorting returns the table to page 1 — **BLOCKED (pagination module)**
- **AC:** AC12, AC13 · **Priority:** Medium
- **Preconditions:** Common; pagination module delivered; 25 transactions as in ST-049.
- **Steps:** 1. Click "Search". 2. Click "Next" (page 2 of 2). 3. Click the Date header.
- **Expected result:** After step 3 the table is on page 1 (`Page 1 of 2`) and shows "T01" … "T20" ascending.

### AC13 — Pagination

Techniques: boundary value analysis (20/21, 39/40/41 transactions); state transition (page navigation).

#### Interim behaviour (executable now)

#### ST-051 — Before the pagination module: only the first 20 rows are shown
- **AC:** AC13 note, Story › Pagination (Dependency); see Q8 · **Priority:** High
- **Preconditions:** As ST-049.
- **Steps:** 1. Keep "This week". 2. Click "Search".
- **Expected result:** The table shows 20 rows, "T25" … "T06", descending.

#### Pagination cases — all **BLOCKED until the pagination module is delivered**

Preconditions for ST-052 – ST-059: Common; pagination module delivered; C1 has N completed transactions this week with distinct timestamps (titles "T01" oldest … "TN" newest); "This week" search run.

| Case | N | Steps | Expected result | Priority |
| --- | --- | --- | --- | --- |
| ST-052 | 20 | Search | 20 rows (T20 … T01); pagination block hidden | High |
| ST-053 | 21 | Search | 20 rows (T21 … T02); block shown with First, Previous, `Page 1 of 2`, Next, Last in this order; First/Previous disabled; Next/Last enabled | High |
| ST-054 | 21 | Search → Next | `Page 2 of 2`; 1 row (T01); First/Previous enabled; Next/Last disabled | High |
| ST-055 | 39 | Search → Next | `Page 2 of 2`; 19 rows (T19 … T01); First/Previous enabled; Next/Last disabled | High |
| ST-056 | 40, then 41 | Search with N = 40; then seed one more and Search | N = 40: `Page 1 of 2`, page 2 has 20 rows. N = 41: `Page 1 of 3`, page 3 has 1 row (T01) | Medium |
| ST-057 | 45 | Search → Next → Last → Previous → First | After Next: `Page 2 of 3`, rows T25 … T06, all four buttons enabled. After Last: `Page 3 of 3`, rows T05 … T01, Next/Last disabled. After Previous: `Page 2 of 3`. After First: `Page 1 of 3`, rows T45 … T26, First/Previous disabled | High |
| ST-058 | 25 | Search (block shown) → select "Today" with no transactions today → Search | "No trx found"; pagination block hidden | Medium |
| ST-059 | 25 | Search (block shown) → select "This month" → Clear | Dropdown "This week"; "No trx found"; pagination block hidden | Medium |

- **AC:** AC13 (all); ST-053 – ST-057 also assumption A6; ST-058 also AC9; ST-059 also AC14.

### AC14 — Clear

Technique: state transition (Results / Selection changed / Default → Cleared).

#### ST-060 — Clear after a search with another period
- **AC:** AC14, Story › Buttons · **Priority:** High
- **Preconditions:** Common; D1 seeded.
- **Steps:** 1. Select "This month", click "Search" (5 rows). 2. Click "Clear".
- **Expected result:** Dropdown shows "This week"; the table shows "No trx found"; no pagination block.

#### ST-061 — Clear with a changed but not searched period
- **AC:** AC14 · **Priority:** Medium
- **Preconditions:** Common; D1 seeded.
- **Steps:** 1. Keep "This week", click "Search" (4 rows). 2. Select "This year" without searching. 3. Click "Clear".
- **Expected result:** Dropdown shows "This week"; the table shows "No trx found".

#### ST-062 — Search after Clear uses "This week"
- **AC:** AC14, AC6 · **Priority:** Medium
- **Preconditions:** As ST-060 after step 2.
- **Steps:** 1. Click "Search".
- **Expected result:** The table shows the expected "This week" result for D1 (4 rows).

#### ST-063 — Clear in the default state keeps the default state
- **AC:** Story › Buttons (Clear) · **Priority:** Low
- **Preconditions:** Common; D1 seeded; Statement page just opened.
- **Steps:** 1. Click "Clear".
- **Expected result:** Dropdown shows "This week"; the table shows "No trx found".

### AC15 — Refresh and return

Technique: state transition (Results → Refresh / Leave and return → Default).

#### ST-064 — Browser refresh returns the page to the default state
- **AC:** AC15, Story › Page State · **Priority:** High
- **Preconditions:** Common; D1 seeded.
- **Steps:** 1. Select "This month", click "Search" (5 rows). 2. Refresh the browser (F5).
- **Expected result:** The Statement page is shown with "This week" in the dropdown and "No trx found" in the table; no statement API request is sent after the refresh.

#### ST-065 — Leaving the page and coming back returns the default state
- **AC:** AC15, Story › Page State · **Priority:** High
- **Preconditions:** Common; D1 seeded.
- **Steps:** 1. Select "This month", click "Search" (5 rows). 2. Click another left-menu item. 3. Click "Statement".
- **Expected result:** Dropdown shows "This week"; the table shows "No trx found"; no statement API request is sent on return.

## 6. Coverage matrix

| AC | Test cases | Status |
| --- | --- | --- |
| AC1 — Menu item | ST-001, ST-002, ST-003, ST-004 | Ready |
| AC2 — Feature flag off | ST-005, ST-006 | Ready |
| AC3 — Default state | ST-007 | Ready |
| AC4 — Filter layout and buttons | ST-008, ST-009 | Ready |
| AC5 — Period selection | ST-010, ST-011, ST-032 | Ready |
| AC6 — Search | ST-012, ST-013, ST-014, ST-015, ST-016, ST-017, ST-036, ST-062 | Ready |
| AC7 — Period boundaries | ST-018 – ST-031 | Ready (needs test clock, A1) |
| AC8 — Repeated search, stale filter | ST-032, ST-033, ST-034, ST-037 | Ready |
| AC9 — Empty result | ST-035, ST-036, ST-037; ST-058 | Ready; ST-058 blocked |
| AC10 — Table content | ST-038, ST-039, ST-040, ST-041, ST-042, ST-043 | Ready |
| AC11 — Amount format | ST-039, ST-044, ST-045, ST-046 | Ready |
| AC12 — Sorting | ST-017, ST-047, ST-048, ST-049; ST-050 | Ready; ST-050 blocked |
| AC13 — Pagination | ST-051 (interim); ST-052 – ST-059 | ST-051 ready; ST-052 – ST-059 **blocked until the pagination module is delivered** |
| AC14 — Clear | ST-060, ST-061, ST-062; ST-059 | Ready; ST-059 blocked |
| AC15 — Refresh and return | ST-064, ST-065 | Ready |

Story sections without an AC that have cases: Description — not the default page (ST-003); Sorting — tie-break by ID (ST-016); Buttons — Clear in the default state (ST-063).

ACs without test cases: none. Parts of ACs not covered because of open questions: AC12 tie-break in ascending order (Q1), AC6 blocking of the left menu (Q4), multi-account clients (Q3).
