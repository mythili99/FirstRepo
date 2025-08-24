Feature: User API Management
  As a system administrator
  I want to manage users through the API
  So that I can perform CRUD operations on user data

  Background:
    Given the API base URL is configured
    And I have valid API credentials

  @api @smoke @users
  Scenario: Get all users
    When I send a GET request to "/users"
    Then the response status code should be 200
    And the response should contain a list of users
    And the response time should be less than 2000ms

  @api @users
  Scenario: Get user by ID
    When I send a GET request to "/users/1"
    Then the response status code should be 200
    And the response should contain user details
    And the user ID should be "1"

  @api @users
  Scenario: Create a new user
    When I send a POST request to "/users" with the following data:
      | firstName | lastName | email           |
      | John      | Doe      | john.doe@test.com |
    Then the response status code should be 201
    And the response should contain the created user details
    And the user should have an ID

  @api @users
  Scenario: Update an existing user
    When I send a PUT request to "/users/1" with the following data:
      | firstName | lastName | email           |
      | Jane      | Smith    | jane.smith@test.com |
    Then the response status code should be 200
    And the response should contain the updated user details

  @api @users
  Scenario: Delete a user
    When I send a DELETE request to "/users/1"
    Then the response status code should be 204
    And the user should be deleted

  @api @users @negative
  Scenario: Get non-existent user
    When I send a GET request to "/users/999"
    Then the response status code should be 404
    And the response should contain an error message

  @api @users @negative
  Scenario: Create user with invalid data
    When I send a POST request to "/users" with invalid data:
      | firstName | email |
      |           | invalid-email |
    Then the response status code should be 400
    And the response should contain validation errors

  @api @users @performance
  Scenario: Verify API response time
    When I send a GET request to "/users"
    Then the response time should be less than 1000ms
    And the response should be valid JSON