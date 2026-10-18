@ui
Feature: OrangeHRM login

  Scenario: User logs in with valid credentials
    Given the user is on the OrangeHRM login page
    When the user logs in with valid credentials
    Then the user sees the dashboard

  Scenario: User cannot log in with a wrong password
    Given the user is on the OrangeHRM login page
    When the user logs in as "Admin" with password "wrong"
    Then the user sees the "Invalid credentials" error