Feature: User registration
  A visitor registers an account with a unique username and a password that
  satisfies the bookstore's password rules.

  @BS-013 @regression @api
  Scenario Outline: Registration is rejected when the password is <rule>
    When a new user registers with the password "<password>"
    Then the registration is rejected for breaking the password rules

    Examples:
      | rule                        | password |
      | shorter than 8 characters   | Ab1!xyz  |
      | without an uppercase letter | abcdef1! |
      | without a lowercase letter  | ABCDEF1! |
      | without a digit             | Abcdefg! |
      | without a special character | Abcdefg1 |
