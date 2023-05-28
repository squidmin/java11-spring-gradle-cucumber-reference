#!/bin/zsh

# Example usage:
# ./scripts/create_dataset.sh lofty-root-378503 test_dataset_integration

GCP_DEFAULT_USER_PROJECT_ID="$1"
GCP_DEFAULT_USER_DATASET="$2"

bq --location=us mk \
  --dataset \
  --default_partition_expiration=3600 \
  --default_table_expiration=3600 \
  --description="An example." \
  --label=test_label_1:test_value_1 \
  --label=test_label_2:test_value_2 \
  --max_time_travel_hours=168 \
  --storage_billing_model=LOGICAL \
  ${GCP_DEFAULT_USER_PROJECT_ID}:${GCP_DEFAULT_USER_DATASET}
