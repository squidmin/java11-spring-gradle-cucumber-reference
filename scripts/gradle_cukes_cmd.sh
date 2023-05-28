#!/bin/zsh

# Example usage:
# ./scripts/gradle_cukes_cmd.sh

./gradlew cukes \
  -DPROFILE=local \
  -DGCP_SA_KEY_PATH=/Users/admin/.config/gcloud \
  -DGCP_ADC_ACCESS_TOKEN="$(gcloud auth application-default print-access-token)"
