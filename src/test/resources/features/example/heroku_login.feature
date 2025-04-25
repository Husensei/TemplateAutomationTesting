@heroku
Feature: Login to Heroku

  @parallel
  Scenario: Login with valid credentials
    Given the user is on the Heroku login page
    When the user enters correct username and password
    And clicks on the login button
    Then the user should be redirected to the secure area

  @parallel
  Scenario: Login with invalid credentials
    Given the user is on the Heroku login page
    When the user enters incorrect username and password
    And clicks on the login button
    Then the user should see the error message