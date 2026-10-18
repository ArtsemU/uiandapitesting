Feature: Feature to test Google search functionality

  Scenario: Validate Google search is working
    Given browser is open
    And user is on Google Search page
    When user enters a text in Search box
    And hits enter
    Then user is navigated to search results