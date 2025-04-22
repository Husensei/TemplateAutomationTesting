@example
Feature: Google Search

  Scenario: User searches for Selenium on Google
    Given I am on the Google homepage
    When I search for "Selenium"
    Then I should see results related to "Selenium"