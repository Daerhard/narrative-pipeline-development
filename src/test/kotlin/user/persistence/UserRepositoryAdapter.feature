Feature: User repository adapter operations

  Scenario: Get a user that exists
    Given the database contains a user with id 1
    When I fetch the user with id 1
    Then the domain user with id 1 is returned

  Scenario: Get a user that does not exist
    Given the database does not contain a user with id 99
    When I fetch the user with id 99
    Then the repository throws a NotFoundException

  Scenario: Save a new user
    Given a domain user to persist
    When the user is saved
    Then the repository saved the entity and the domain user is returned

  Scenario: Update an existing user
    Given a domain user to persist
    When the adapter updates the user
    Then the repository saved the entity and the domain user is returned

  Scenario: Check existence of a user that exists
    Given the database contains a user with id 1
    When I check if the user with id 1 exists
    Then the existence check returns true

  Scenario: Check existence of a user that does not exist
    Given the database does not contain a user with id 99
    When I check if the user with id 99 exists
    Then the existence check returns false
