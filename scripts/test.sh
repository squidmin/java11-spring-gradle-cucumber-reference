#!/bin/zsh


# Example usage:
# ./scripts/test.sh local lofty-root-378503 test_dataset_integration test_table_integration $HOME/.config/gcloud "$(gcloud auth application-default print-access-token)"


LOCAL_GCLOUD_AUTH_DIRECTORY=$HOME/.config/gcloud         # Local system directory containing GCP service account key file.
CONTAINER_GCLOUD_AUTH_DIRECTORY=/root/.config/gcloud     # Container directory containing GDP service account key file.
GCLOUD_SA_KEY_FILENAME=sa-private-key.json               # Name of GCP service account key file.
LOCAL_MAVEN_REPOSITORY=$HOME/.m2                         # Maven repository local system directory.
CONTAINER_MAVEN_REPOSITORY=/root/.m2                     # Maven repository container directory.

PROFILE="$1"
GCP_DEFAULT_USER_PROJECT_ID="$2"
GCP_DEFAULT_USER_DATASET="$3"
GCP_DEFAULT_USER_TABLE="$4"
GCP_SA_KEY_PATH="$5"
GCP_ADC_ACCESS_TOKEN="$6"

echo "Profile: ${PROFILE}"
echo "Project ID: ${GCP_DEFAULT_USER_PROJECT_ID}"
echo "Dataset: ${GCP_DEFAULT_USER_DATASET}"
echo "Table: ${GCP_DEFAULT_USER_TABLE}"


chmod +x ./scripts/create_dataset.sh
chmod +x ./scripts/delete_dataset.sh
chmod +x ./scripts/create_table.sh


./scripts/create_dataset.sh ${GCP_DEFAULT_USER_PROJECT_ID} ${GCP_DEFAULT_USER_DATASET}
sleep 1
./scripts/create_table.sh ${GCP_DEFAULT_USER_PROJECT_ID} ${GCP_DEFAULT_USER_DATASET} ${GCP_DEFAULT_USER_TABLE}
docker build \
  --build-arg PROFILE=local \
  --build-arg GCP_SA_KEY_PATH=${GCP_SA_KEY_PATH} \
  --build-arg GCP_DEFAULT_USER_PROJECT_ID=${GCP_DEFAULT_USER_PROJECT_ID} \
  --build-arg GCP_DEFAULT_USER_DATASET=${GCP_DEFAULT_USER_DATASET} \
  --build-arg GCP_DEFAULT_USER_TABLE=${GCP_DEFAULT_USER_TABLE} \
  -t java11-spring-gradle-cucumber-reference .
echo "Starting Cucumber tests! 🥒"
docker run --rm \
  --env PROFILE=local \
  --env GCP_ADC_ACCESS_TOKEN="$(gcloud auth application-default print-access-token)" \
  -v $HOME/.config/gcloud:/root/.config/gcloud \
  -v $HOME/google-cloud-sdk:/root/google-cloud-sdk \
  -v $HOME/.m2:/root/.m2 \
  java11-spring-gradle-cucumber-reference
sleep 1
./scripts/delete_dataset.sh ${GCP_DEFAULT_USER_PROJECT_ID} ${GCP_DEFAULT_USER_DATASET}


echo "Done...."
