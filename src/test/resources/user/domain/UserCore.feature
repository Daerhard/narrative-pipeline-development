Feature: User core operations

  Scenario: Get an existing user
    Given a user exists in the repository with id 1
    When I get the user with id 1
    Then the user with id 1 is returned

  Scenario: Create a valid user
    Given a core user with zip code 86920
    When the user is created
    Then the user is saved to the repository

  Scenario: Reject creating a user with an invalid zip code
    Given a core user with zip code 100001
    When the user is created
    Then an exception is thrown

  Scenario: Update a valid existing user
    Given a core user with zip code 86920
    And the user exists in the repository
    When the user is updated
    Then the user is updated in the repository

  Scenario: Reject updating a user that does not exist
    Given a core user with zip code 86920
    And the user does not exist in the repository
    When the user is updated
    Then a NotFoundException is thrown

  Scenario: Reject updating a user with an invalid zip code
    Given a core user with zip code 100001
    When the user is updated
    Then an exception is thrown
