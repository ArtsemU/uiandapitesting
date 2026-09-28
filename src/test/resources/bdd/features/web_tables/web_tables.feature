Feature: Web Tables
  A visitor manages the records of the Web Tables page through its
  registration form.

  @WT-001 @smoke @ui
  Scenario: A record added through the registration form appears in the table
    Given the user is on the "Web Tables" page
    When the user adds a new record through the registration form
    Then the table gains exactly one row holding all the values of the new record
