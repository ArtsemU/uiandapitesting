Feature: Text Box
  A visitor fills in the Text Box form and sees the submitted values echoed
  back in the output block.

  @TB-002 @regression @ui
  Scenario: Submitted data is displayed in the output block
    Given the user is on the "Text Box" page
    When the user submits their full name, email, current address and permanent address
    Then the output shows the submitted full name, email, current address and permanent address
