Feature: User Login Functionality
  As a user
  I want to be able to login to the application
  So that I can access my account

  Background:
    Given I am on the login page
    And the login form is displayed

  @smoke @login
  Scenario: Successful login with valid credentials
    When I enter valid username "testuser"
    And I enter valid password "password123"
    And I click the login button
    Then I should be logged in successfully
    And I should see the dashboard page

  @login @negative
  Scenario: Failed login with invalid credentials
    When I enter invalid username "invaliduser"
    And I enter invalid password "wrongpass"
    And I click the login button
    Then I should see an error message
    And I should remain on the login page

  @login @negative
  Scenario: Failed login with empty username
    When I leave the username field empty
    And I enter valid password "password123"
    And I click the login button
    Then I should see a validation error for username
    And I should remain on the login page

  @login @negative
  Scenario: Failed login with empty password
    When I enter valid username "testuser"
    And I leave the password field empty
    And I click the login button
    Then I should see a validation error for password
    And I should remain on the login page

  @login @security
  Scenario: Verify password field masks input
    When I enter password "password123"
    Then the password field should mask the input
    And the password should not be visible in plain text