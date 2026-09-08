# 2. Book equality is defined by ISBN alone

- **Date:** 2026-09-03
- **Status:** Accepted

## Context

The Bookstore API returns books in two different shapes. `GET /BookStore/v1/Books`
and `GET /BookStore/v1/Book` return the full object — title, author, publisher,
page count. `POST /BookStore/v1/Books`, which adds a book to a user's
collection, returns only the ISBN.

A test that adds a book and then verifies it was added has to compare across
those two shapes. With equality defined over all fields, that comparison is
impossible: the objects differ in eight fields out of nine, even when they
describe the same book.

The alternative considered was keeping full-field equality and comparing ISBNs
field by field in every test that crosses this boundary. Rejected as noise —
the comparison is not about individual fields, it is about whether the same
book came back.

## Decision

`Book.equals` and `Book.hashCode` are defined over `isbn` only.

The catalogue is static and ISBN is its natural key, so two books with the same
ISBN are the same book. Every other field is descriptive.

`toString` still prints all fields — assertion failures need to be readable.

## Consequences

- A wrong title, author or page count will not fail an object comparison.
  Where those matter, assert them field by field and say so in the test.
- Do not "fix" equality to cover all fields. It has been considered and
  rejected here.
- The separate `Isbn` model class is unnecessary: `Book` with only the ISBN
  populated is equal to the full catalogue object, so `Book` covers both shapes.
