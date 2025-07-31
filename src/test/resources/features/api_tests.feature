Feature: API Testing Examples
  As a tester
  I want to test REST API endpoints
  So that I can verify API functionality

  @api
  Scenario: Get list of users
    Given I have the API client configured
    When I send a GET request to "/users"
    Then the response should be successful
    And the response time should be less than 5000 milliseconds
    And the response should have content type "application/json"
    And the response body should not be empty
    And the response should contain field "data"

  @api
  Scenario: Get a single user
    Given I have the API client configured
    When I send a GET request to "/users/2"
    Then the response status code should be 200
    And the response should contain field "data.id" with value "2"
    And the response should contain field "data.email"
    And the response should contain field "data.first_name"
    And the response should contain field "data.last_name"

  @api
  Scenario: Create a new user
    Given I have the API client configured
    And I set the request body to '{"name": "John Doe", "job": "Software Tester"}'
    When I send a POST request to "/users"
    Then the response status code should be 201
    And the response should contain field "name" with value "John Doe"
    And the response should contain field "job" with value "Software Tester"
    And the response should contain field "id"
    And the response should contain field "createdAt"

  @api
  Scenario: Update an existing user
    Given I have the API client configured
    And I set the request body to '{"name": "Jane Doe", "job": "Senior Tester"}'
    When I send a PUT request to "/users/2"
    Then the response status code should be 200
    And the response should contain field "name" with value "Jane Doe"
    And the response should contain field "job" with value "Senior Tester"
    And the response should contain field "updatedAt"

  @api
  Scenario: Delete a user
    Given I have the API client configured
    When I send a DELETE request to "/users/2"
    Then the response status code should be 204

  @api
  Scenario: Get users with pagination
    Given I have the API client configured
    When I send a GET request to "/users?page=1"
    Then the response should be successful
    And the response should contain field "page" with value "1"
    And the response should contain field "per_page"
    And the response should contain field "total"
    And the response should contain field "data"