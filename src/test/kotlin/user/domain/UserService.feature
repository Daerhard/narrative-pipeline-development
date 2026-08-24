Feature: User validation

  Scenario: Validate a user with a valid zip code
    Given a user with zip code 9999
    When the user is validated
    Then the user should be valid

  Scenario: Reject a user with an invalid zip code
    Given a user with zip code 100001
    When the user is validated
    Then the user should be invalid
