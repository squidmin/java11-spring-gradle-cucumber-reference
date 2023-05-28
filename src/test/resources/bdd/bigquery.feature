Feature: BigQuery
  Testing Cucumber / Spring integration with BigQuery

  Scenario Outline: Get rows from a table
    Given the table "<table>" exists
    When I fetch rows from the table given query: "<query>"
    Then I receive a non-empty result

    Examples:
      | table                  | query                                                                                      |
      | test_table_integration | SELECT * FROM `lofty-root-378503.test_dataset_integration.test_table_integration` LIMIT 10 |
