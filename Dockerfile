FROM eclipse-temurin:11-jdk-alpine

### Build arguments ###
ARG PROFILE
ARG GCP_SA_KEY_PATH
ARG GCP_DEFAULT_USER_PROJECT_ID
ARG GCP_DEFAULT_USER_DATASET
ARG GCP_DEFAULT_USER_TABLE
ARG GCP_SA_PROJECT_ID
ARG GCP_SA_DATASET
ARG GCP_SA_TABLE
###

### Environment variables ###
# OS
ENV APP_DIR=/usr/local/app
# JVM arguments.
ENV PROFILE=${PROFILE}
ENV GCP_SA_KEY_PATH=${GCP_SA_KEY_PATH}
ENV GCP_DEFAULT_USER_PROJECT_ID=${GCP_DEFAULT_USER_PROJECT_ID}
ENV GCP_DEFAULT_USER_DATASET=${GCP_DEFAULT_USER_DATASET}
ENV GCP_DEFAULT_USER_TABLE=${GCP_DEFAULT_USER_TABLE}
ENV GCP_SA_PROJECT_ID=${GCP_SA_PROJECT_ID}
ENV GCP_SA_DATASET=${GCP_SA_DATASET}
ENV GCP_SA_TABLE=${GCP_SA_TABLE}
###

### python/pip ###
ENV PYTHONUNBUFFERED=1
RUN apk add --update --no-cache python3 && ln -sf python3 /usr/bin/python
RUN python3 -m ensurepip
RUN pip3 install --no-cache --upgrade pip setuptools
###

WORKDIR ${APP_DIR}

# Copy the project into the container.
#ADD . ${APP_DIR}
COPY . .

#ENTRYPOINT ["sh", "-c", "cd ${APP_DIR} && sh"]
#ENTRYPOINT ["./gradlew", "cukes", "-DPROFILE=${PROFILE}", "-DGCP_SA_KEY_PATH=${GCP_SA_KEY_PATH}", "-DGCP_ADC_ACCESS_TOKEN=$GCP_ADC_ACCESS_TOKEN"]
ENTRYPOINT ./gradlew cukes -DPROFILE=${PROFILE} -DGCP_SA_KEY_PATH=${GCP_SA_KEY_PATH} -DGCP_ADC_ACCESS_TOKEN=${GCP_ADC_ACCESS_TOKEN}
