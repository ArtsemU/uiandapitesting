Feature: Login Functionality

   Scenario: Check login is sucessful with valid credentials

     Given user is on login page
     When user enters username and password
     And click on login button
     Then user is navigated to home page