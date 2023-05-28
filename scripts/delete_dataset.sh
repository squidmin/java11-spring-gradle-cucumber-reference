#!/bin/zsh

# Example usage:
# ./scripts/delete_dataset.sh lofty-root-378503 test_dataset_integration

GCP_DEFAULT_USER_PROJECT_ID="$1"
GCP_DEFAULT_USER_DATASET="$2"

# The -r flag removes all tables in the dataset.
bq rm -r -f -d ${GCP_DEFAULT_USER_PROJECT_ID}:${GCP_DEFAULT_USER_DATASET}
