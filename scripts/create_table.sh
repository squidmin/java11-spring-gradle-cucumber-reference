#!/bin/zsh

# Example usage:
# ./scripts/create_table.sh lofty-root-378503 test_dataset_integration test_table_integration

GCP_DEFAULT_USER_PROJECT_ID="$1"
GCP_DEFAULT_USER_DATASET="$2"
GCP_DEFAULT_USER_TABLE="$3"

bq --location=us load \
  --source_format=CSV \
  ${GCP_DEFAULT_USER_PROJECT_ID}:${GCP_DEFAULT_USER_DATASET}.${GCP_DEFAULT_USER_TABLE} \
  ./csv/example.csv \
  ./schema/example.json
