Feature: Customer core operations

  Scenario: Get an existing customer
    Given a customer exists in the repository with id 1
    When I get the customer with id 1
    Then the customer with id 1 is returned

  Scenario: Create a valid customer
    Given a core customer with zip code 86920
    When the customer is created
    Then the customer is saved to the repository

  Scenario: Reject creating a customer with an invalid zip code
    Given a core customer with zip code 100001
    When the customer is created
    Then an exception is thrown for customer

  Scenario: Update a valid existing customer
    Given a core customer with zip code 86920
    And the customer exists in the repository
    When the customer is updated
    Then the customer is updated in the repository

  Scenario: Reject updating a customer that does not exist
    Given a core customer with zip code 86920
    And the customer does not exist in the repository
    When the customer is updated
    Then a NotFoundException is thrown for customer

  Scenario: Reject updating a customer with an invalid zip code
    Given a core customer with zip code 100001
    When the customer is updated
    Then an exception is thrown for customer
