Feature: Customer repository adapter operations

  Scenario: Get a customer that exists
    Given the database contains a customer with id 1
    When I fetch the customer with id 1
    Then the domain customer with id 1 is returned

  Scenario: Get a customer that does not exist
    Given the database does not contain a customer with id 99
    When I fetch the customer with id 99
    Then the repository throws a NotFoundException for customer

  Scenario: Save a new customer
    Given a domain customer to persist
    When the customer is saved
    Then the repository saved the entity and the domain customer is returned

  Scenario: Update an existing customer
    Given a domain customer to persist
    When the adapter updates the customer
    Then the repository saved the entity and the domain customer is returned

  Scenario: Check existence of a customer that exists
    Given the database contains a customer with id 1
    When I check if the customer with id 1 exists
    Then the customer existence check returns true

  Scenario: Check existence of a customer that does not exist
    Given the database does not contain a customer with id 99
    When I check if the customer with id 99 exists
    Then the customer existence check returns false
