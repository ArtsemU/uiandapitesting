# Internet Banking — Statement

## Description

In internet banking, the user logs in to the client side with a login and password.

As part of the update, a new **"Statement"** item appears in the left menu. The item is available to **all clients**.

Clicking **"Statement"** in the left menu loads the Statement page in the main content area (the same way as any other left-menu item; not a pop-up or panel). The Statement page is not the default page after login.

The Statement page consists of two blocks:
- the filter block;
- the results table.

The feature is delivered behind a **feature flag**. When the flag is off, the "Statement" menu item is hidden and a direct URL to the page redirects to the default page after login. The rollout plan is decided by the Delivery Manager and is out of scope of this story.

## Filter Block

Elements are placed in this order: **"Select period" dropdown → "Search" button → "Clear" button**.

### Period Selection

A **"Select period"** dropdown with the following values, in this order:
- Today
- This week
- This month
- This year

Default value: **"This week"**.

A period is applied to the **transaction creation date and time**.

**Time zone:** transactions are stored in UTC. Period boundaries and displayed dates use the **Europe/Warsaw** time zone, including daylight saving time (UTC+02:00 in summer, UTC+01:00 in winter).

**Period boundaries** (calendar periods, upper boundary is the moment of search):
- **Today** — from 00:00:00 of the current day.
- **This week** — from Monday 00:00:00 of the current week.
- **This month** — from 00:00:00 on the 1st day of the current month.
- **This year** — from 00:00:00 on 1 January of the current year.

The search runs **only after clicking the "Search" button**. Changing the dropdown value does not trigger a search and does not change the table.

### Buttons

Both buttons are **always enabled** (except while a search is in progress, see Loading State).

- **Search** — runs the search for the selected period. Every click re-queries the data and refreshes the table, even if the period has not changed.
- **Clear** — resets the page to its default state: the dropdown returns to "This week" and the table shows the label **"No trx found"**.

### Loading State

While a search is in progress, a **spinner** is shown and all interaction on the page is blocked until the search completes.

## Results Table

The table shows **completed** transactions of all types created within the selected period. Pending, declined and reversed transactions are not shown. All displayed values come from the transaction data returned by the API: direction (debit/credit), type, amount, currency, recipient, payer, title and creation date. The UI does not derive or change them; it only formats and sorts them.

| # | Column | Content |
| --- | --- | --- |
| 1 | Date | Transaction creation date and time, format `DD.MM.YYYY HH:mm`, e.g. `15.10.2026 14:05` |
| 2 | Recipient | Recipient name from the transaction details; `—` if empty |
| 3 | Payer | Payer name from the transaction details; `—` if empty |
| 4 | Title | Transaction title, max **20 characters**;  `—` if empty; longer values are cut off at 20 characters, with no ellipsis and no tooltip |
| 5 | Amount | Format `<amount> <currency>`. Incoming: green, e.g. `123.25 USD`. Outgoing: red, with a minus sign, e.g. `-45.05 EUR` Two decimals, dot as decimal separator, no thousands separator.|

### Sorting

- Default: **by Date, descending** — the most recent transactions first.
- Clicking the **Date column header** switches between descending and ascending; an arrow in the header shows the current order.
- Sorting applies to the whole result set, without re-querying the data, and returns the table to page 1.
- Other columns are not sortable.
- Transactions with the same creation date and time are ordered by transaction ID, descending.

### Pagination

- Maximum **20 rows per page**.
- The pagination block is shown only if the search returns **21 or more** transactions.
- The block contains, in this order: **First**, **Previous**, **page indicator** (`Page 2 of 3`), **Next**, **Last**.
- **First** and **Previous** are enabled only if there are pages to the left of the current page; **Next** and **Last** — only if there are pages to the right.

| Transactions | Current page | Pagination | First / Previous | Next / Last |
| --- | --- | --- | --- | --- |
| 20 | 1 of 1 | Hidden | — | — |
| 21 | 1 of 2 | Shown | Disabled | Enabled |
| 39 | 2 of 2 | Shown | Enabled | Disabled |
| 45 | 2 of 3 | Shown | Enabled | Enabled |

- **Dependency:** …the table shows the first 20 rows of the whole sorted result (for ascending order — the 20 oldest)

### Empty Table

The table shows the label **"No trx found"** (no pagination block):
- when the Statement page is opened (no search is run on open);
- when a search returns no transactions;
- after clicking "Clear".

## Page State

- On opening the page: dropdown = "This week", table = "No trx found".
- After a browser refresh or after leaving the page and coming back: the page returns to the same default state. The selected period and results are not kept.

## Constraints

- UI language: **English only**.
- Supported environment: **Chrome**, **Full HD** resolution.
- No masking of displayed data is required.

## Out of Scope (not in MVP)

- Custom date range.
- Export, print, download.
- Transaction details view.
- Error handling and unavailable-service behaviour — the standard application behaviour applies.
- Data freshness and other non-functional requirements.
- Feature-flag rollout plan.

## Acceptance Criteria

**AC1 — Menu item**
Given a logged-in client and the feature flag is on
Then the left menu contains the "Statement" item
When the client clicks "Statement"
Then the Statement page is loaded in the main content area

**AC2 — Feature flag off**
Given the feature flag is off
Then the "Statement" menu item is hidden
And opening the page by direct URL redirects to the default page after login

**AC3 — Default state on opening**
Given the client opens the Statement page
Then the "Select period" dropdown shows "This week"
And the table shows the label "No trx found"
And no search is run

**AC4 — Filter block layout and buttons**
Given the Statement page is open
Then the elements are placed in this order: "Select period" dropdown, "Search" button, "Clear" button
And "Search" and "Clear" are enabled in every state when no search is in progress

**AC5 — Period selection**
Given the Statement page is open
When the client opens the "Select period" dropdown
Then it contains exactly "Today", "This week", "This month", "This year" in this order
When the client selects any value
Then no search is run and the table does not change

**AC6 — Search**
Given a period is selected
When the client clicks "Search"
Then a spinner is shown and all interaction on the page is blocked
And after the search completes the table shows completed transactions created within the selected period
And pending, declined and reversed transactions are not shown
And the rows are sorted by Date descending

**AC7 — Period boundaries**
Given transactions exist at 23:59:59 of the day before each period start and at 00:00:00 of the period start (Europe/Warsaw)
When the client searches for "Today", "This week", "This month" and "This year"
Then each result includes the transaction at the period start
And excludes the transaction one second before it
And this holds on both sides of a daylight saving time change

**AC8 — Repeated search and stale filter**
Given a search has already been run
When the client selects another period without clicking "Search"
Then the table still shows the previous results
When the client clicks "Search", with the same or a different period
Then the data is re-queried and the table is refreshed

**AC9 — Empty result**
Given there are no transactions within the selected period
When the client clicks "Search"
Then the table shows the label "No trx found" and no pagination block

**AC10 — Table content**
Given the search returned transactions
Then the table has the columns Date, Recipient, Payer, Title, Amount in this order
And Date is shown as `DD.MM.YYYY HH:mm` in Europe/Warsaw time
And an empty Recipient or Payer is shown as `—`
And a Title longer than 20 characters is cut off at 20 characters, with no ellipsis or tooltip
And all values match the transaction data in the database

**AC11 — Amount format**
Then each amount is shown as `<amount> <currency>`
And an incoming amount is green without a sign, e.g. `123.25 USD`
And an outgoing amount is red with a minus sign, e.g. `-45.05 EUR`

**AC12 — Sorting**
Given the table shows results
When the client clicks the Date column header
Then the whole result set is re-sorted in the opposite order without re-querying the data
And the table returns to page 1
And the arrow in the header shows the current order
And no other column can be sorted

**AC13 — Pagination** *(tested after the pagination module is delivered; until then the table shows the first 20 rows)*
Given the search returned 21 or more transactions
Then the table shows max 20 rows per page
And the pagination block shows First, Previous, `Page X of Y`, Next, Last in this order
And First/Previous are enabled only if there are pages to the left, Next/Last only if there are pages to the right

Examples:
| Transactions | Current page | Pagination | First / Previous | Next / Last |
| 20 | 1 of 1 | Hidden | — | — |
| 21 | 1 of 2 | Shown | Disabled | Enabled |
| 39 | 2 of 2 | Shown | Enabled | Disabled |
| 45 | 2 of 3 | Shown | Enabled | Enabled |

**AC14 — Clear**
Given a period other than "This week" is selected and the table shows results
When the client clicks "Clear"
Then the dropdown is reset to "This week"
And the table shows the label "No trx found" and no pagination block

**AC15 — Refresh and return**
Given a search has been run on the Statement page
When the client refreshes the browser, or leaves the page and comes back
Then the page is in the default state from AC3
