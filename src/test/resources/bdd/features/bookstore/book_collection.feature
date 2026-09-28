Feature: Book collection
  A registered user keeps a personal collection of books chosen from the
  bookstore catalogue.

  @BS-001 @smoke @api
  Scenario: A user adds a book to their collection, removes it and deletes their account
    When a new user registers
    Then the user is registered with an empty collection
    When the user logs in
    Then the user receives a token
    When the user views their account
    Then the user's collection is empty
    When the user browses the catalogue
    Then the catalogue has books
    When the user adds the first book of the catalogue to their collection
    Then the book is added to the user's collection
    When the user views their account
    Then the user's collection holds only that book
    When the user removes the book from their collection
    Then the book is removed from the user's collection
    When the user views their account
    Then the user's collection is empty
    When the user deletes their account
    Then the user's account is deleted
    When the user views their account
    Then the user's account is not found
