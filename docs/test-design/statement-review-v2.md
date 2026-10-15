# Story review — Internet Banking: Statement (v2)

Source: `docs/stories/en_internet_banking_statement_task.md`.
The story is the only source of truth. No test cases are written here.
Items listed under "Out of Scope" in the story are not counted as gaps.

## Step 1 — Readiness

| Item | Status |
| --- | --- |
| Acceptance criteria | **Present** — AC1–AC15, Given/When/Then. Not a Critical gap. |
| Mockups / screenshots | **None.** Layout, colours, sort arrow, spinner and the "No trx found" label have no visual reference. |
| Example data | **Partial.** Inline examples only: date `15.10.2026 14:05`, amounts `123.25 USD` / `-45.05 EUR`, pagination table (20 / 21 / 39 / 45 rows). No test data set (accounts, transactions, statuses, types). |
| Links to related documents | **None.** No links to the feature-flag, pagination module, data model or "standard application behaviour" for errors. |

## Step 2 — Content

### Critical

**C1. Which transactions belong to the client's statement**

> "The table shows **completed** transactions of all types created within the selected period."

- Why it blocks testing: the story never says whose transactions are listed — one account, all of the client's accounts, or cards too — and there is no account selector. For a client with several accounts, or a transfer between the client's own accounts (one row or two?), the expected row set cannot be stated, so AC6, AC9, AC10 and AC13 have no determinable expected result.
- Question for the analyst: Which accounts/products does the statement cover for a client with several accounts, and how is a transfer between the client's own accounts shown (one row, two rows, or hidden)?

**C2. How a transaction is classified as incoming or outgoing**

> "Incoming: green, e.g. `123.25 USD`. Outgoing: red, with a minus sign, e.g. `-45.05 EUR`"

- Why it blocks testing: "of all types" includes types whose direction is not obvious — own-account transfers, currency exchange (two amounts, two currencies), fees, interest, refunds. Without a rule, the expected sign, colour, amount and currency of these rows (AC11) cannot be set.
- Question for the analyst: What field or rule decides incoming vs outgoing, and for a currency exchange which amount and currency are shown?

### Major

- [ ] Empty Title — Recipient and Payer show `—` when empty, Title has no rule. What is shown when the title is empty?
- [ ] Amount number format — only two-decimal examples. Are there thousand separators, which decimal separator, and how many decimals for currencies with 0 or 3 minor units (e.g. JPY, BHD)?
- [ ] Zero amount — neither incoming nor outgoing by sign. Can a zero amount occur, and how is it shown (colour, sign)?
- [ ] Sort tie-break — Date is shown to the minute; order of transactions with the same timestamp (or the same minute) is undefined. Is there a secondary sort key?
- [ ] Sort key precision — is sorting by the full stored timestamp (seconds/milliseconds) or by the displayed `HH:mm`?
- [ ] Ascending sort before pagination is delivered — "first 20 rows of the sorted result": after switching to ascending, are the 20 oldest of the whole result shown (not the 20 newest reversed)? Please confirm.
- [ ] Truncation before pagination is delivered — nothing tells the user more than 20 transactions were found. Is a "showing 20 of N" hint or any indicator expected?
- [ ] Field mapping per transaction type — "all values come from the database (transaction details)": which fields fill Recipient, Payer and Title for card payments, fees, interest, cash withdrawals?
- [ ] Scope of blocking during search — "all interaction on the page is blocked": does this include the left menu, header and browser navigation, or only the main content area?
- [ ] Position of the menu item — where does "Statement" appear in the left menu (order relative to existing items)?

### Minor

- [ ] Time display — are seconds truncated or rounded for `HH:mm` (e.g. 14:05:59 → 14:05 or 14:06)?
- [ ] DST fall-back hour — two transactions at 02:30 CEST and 02:30 CET look identical; is that acceptable, ordered by UTC?
- [ ] Title length unit — are 20 "characters" counted as Unicode characters/graphemes (emoji, diacritics)? Are leading/trailing spaces trimmed before cutting?
- [ ] Long Recipient / Payer — no length limit or truncation rule; wrap, cut or ellipsis?
- [ ] "Empty" Recipient / Payer — does whitespace-only or null count as empty and show `—`?
- [ ] Colours — exact green and red values (or design tokens)?
- [ ] Sort arrow by default — is the arrow shown (pointing down) right after a search, before any click on the header?
- [ ] Table header in empty state — are the column headers shown with "No trx found", and can Date be clicked then?
- [ ] Page after a new search — does a new Search always return to page 1 and descending order (AC6 implies descending; page not stated)?
- [ ] Buttons during search — are Search/Clear shown as disabled, or only blocked by an overlay?
- [ ] Clicking "Statement" while already on the Statement page — does it reset to the default state like leaving and returning?
- [ ] Feature flag scope — is the flag global or per client, and what happens to a user who is on the page when the flag is switched off?
- [ ] Direct URL — what is the Statement page URL to use in AC2?
- [ ] Reversed transactions — is only the original hidden, or also the separate reversal entry if one exists?
- [ ] Supported Chrome version(s) and browser zoom level for "Chrome, Full HD"?
- [ ] Keyboard use — must the dropdown, buttons and Date header be operable by keyboard?
