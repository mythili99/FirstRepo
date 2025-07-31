Feature: UI Testing Examples
  As a tester
  I want to test web application UI
  So that I can verify user interface functionality

  @ui
  Scenario: Navigate to home page and verify title
    Given I navigate to the application
    Then the page title should contain "OpenCart"
    And I should see text "Featured" on the page

  @ui
  Scenario: Search for a product
    Given I navigate to the application
    When I enter "iPhone" in field with xpath "//input[@name='search']"
    And I click on element with xpath "//button[@class='btn btn-default btn-lg']"
    Then I should see text "iPhone" on the page
    And the current URL should contain "search"

  @ui
  Scenario: Navigate to login page
    Given I navigate to the application
    When I click on element with xpath "//a[@title='My Account']"
    And I click on element with xpath "//a[text()='Login']"
    Then the page title should contain "Account Login"
    And I should see text "Returning Customer" on the page

  @ui
  Scenario: Attempt login with invalid credentials
    Given I navigate to "https://demo.opencart.com/index.php?route=account/login"
    When I enter "invalid@test.com" in field with xpath "//input[@id='input-email']"
    And I enter "wrongpassword" in field with xpath "//input[@id='input-password']"
    And I click on element with xpath "//input[@value='Login']"
    Then I should see text "Warning: No match for E-Mail Address and/or Password." on the page

  @ui
  Scenario: Browse categories
    Given I navigate to the application
    When I click on element with xpath "//a[text()='Desktops']"
    And I click on element with xpath "//a[text()='PC (0)']"
    Then the current URL should contain "path=20_26"
    And I should see text "PC" on the page

  @ui
  Scenario: View product details
    Given I navigate to the application
    When I click on element with xpath "//img[@title='MacBook']"
    Then the page title should contain "MacBook"
    And I should see text "Product Code:" on the page
    And I should see element with xpath "//button[@id='button-cart']"