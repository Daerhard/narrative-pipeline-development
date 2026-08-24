Feature: Customer validation

  Scenario: Validate a customer with a valid zip code
    Given a customer with zip code 9999
    When the customer is validated
    Then the customer should be valid

  Scenario: Reject a customer with an invalid zip code
    Given a customer with zip code 100001
    When the customer is validated
    Then the customer should be invalid
