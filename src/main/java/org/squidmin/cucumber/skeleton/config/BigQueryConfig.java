package org.squidmin.cucumber.skeleton.config;

import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.BigQueryOptions;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.squidmin.cucumber.skeleton.config.tables.sandbox.SchemaDefault;
import org.squidmin.cucumber.skeleton.config.tables.sandbox.SelectFieldsDefault;
import org.squidmin.cucumber.skeleton.config.tables.sandbox.WhereFieldsDefault;
import org.squidmin.cucumber.skeleton.logger.Logger;
import org.squidmin.cucumber.skeleton.util.BigQueryUtil.CLI_ARG_KEYS;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Date;
import java.time.Instant;

@Configuration
@ComponentScan(basePackages = {
    "org.squidmin.cucumber.skeleton"
})
@Getter
@Slf4j
public class BigQueryConfig {

    private final String gcpDefaultUserProjectId;
    private final String gcpDefaultUserDataset;
    private final String gcpDefaultUserTable;

    private final String gcpSaProjectId;
    private final String gcpSaDataset;
    private final String gcpSaTable;

    private final String gcpSaKeyPath;
    private final String gcpAdcAccessToken;
    private final String gcpSaAccessToken;

    private final String queryUri;

    private final SchemaDefault schemaDefault;
    private final DataTypes dataTypes;
    private final SelectFieldsDefault selectFieldsDefault;
    private final WhereFieldsDefault whereFieldsDefault;

    private final BigQuery bigQuery;

    @Autowired
    public BigQueryConfig(@Value("${bigquery.application-default.project-id}") String gcpDefaultUserProjectId,
                          @Value("${bigquery.application-default.dataset}") String gcpDefaultUserDataset,
                          @Value("${bigquery.application-default.table}") String gcpDefaultUserTable,
                          @Value("${bigquery.service-account.project-id}") String gcpSaProjectId,
                          @Value("${bigquery.service-account.dataset}") String gcpSaDataset,
                          @Value("${bigquery.service-account.table}") String gcpSaTable,
                          @Value("${bigquery.uri.queries}") String queryUri,
                          SchemaDefault schemaDefault,
                          DataTypes dataTypes,
                          SelectFieldsDefault selectFieldsDefault,
                          WhereFieldsDefault whereFieldsDefault) throws IOException {

        this.gcpDefaultUserProjectId = isEmpty(getProperty(CLI_ARG_KEYS.GCP_DEFAULT_USER_PROJECT_ID.name())) ? gcpDefaultUserProjectId : getProperty(CLI_ARG_KEYS.GCP_DEFAULT_USER_PROJECT_ID.name());
        this.gcpDefaultUserDataset = isEmpty(getProperty(CLI_ARG_KEYS.GCP_DEFAULT_USER_DATASET.name())) ? gcpDefaultUserDataset : getProperty(CLI_ARG_KEYS.GCP_DEFAULT_USER_DATASET.name());
        this.gcpDefaultUserTable = isEmpty(getProperty(CLI_ARG_KEYS.GCP_DEFAULT_USER_TABLE.name())) ? gcpDefaultUserTable : getProperty(CLI_ARG_KEYS.GCP_DEFAULT_USER_TABLE.name());

        this.gcpSaProjectId = isEmpty(getProperty(CLI_ARG_KEYS.GCP_SA_PROJECT_ID.name())) ? gcpSaProjectId : getProperty(CLI_ARG_KEYS.GCP_SA_PROJECT_ID.name());
        this.gcpSaDataset = isEmpty(getProperty(CLI_ARG_KEYS.GCP_SA_DATASET.name())) ? gcpSaDataset : getProperty(CLI_ARG_KEYS.GCP_SA_DATASET.name());
        this.gcpSaTable = isEmpty(getProperty(CLI_ARG_KEYS.GCP_SA_TABLE.name())) ? gcpSaTable : getProperty(CLI_ARG_KEYS.GCP_SA_TABLE.name());

        this.queryUri = queryUri;

        this.gcpSaKeyPath = getProperty(CLI_ARG_KEYS.GCP_SA_KEY_PATH.name());
//        Logger.log(String.format("BQ JDK: GCP_SA_KEY_PATH == %s", this.gcpSaKeyPath), Logger.LogType.CYAN);
        this.gcpAdcAccessToken = getProperty(CLI_ARG_KEYS.GCP_ADC_ACCESS_TOKEN.name());
//        Logger.log(String.format("GCP_ADC_ACCESS_TOKEN == %s", this.gcpAdcAccessToken), Logger.LogType.CYAN);
        this.gcpSaAccessToken = getProperty(CLI_ARG_KEYS.GCP_SA_ACCESS_TOKEN.name());
//        Logger.log(String.format("GCP_SA_ACCESS_TOKEN == %s", this.gcpSaAccessToken), Logger.LogType.CYAN);

        this.schemaDefault = schemaDefault;
        this.dataTypes = dataTypes;
        this.selectFieldsDefault = selectFieldsDefault;
        this.whereFieldsDefault = whereFieldsDefault;

        BigQueryOptions.Builder bqOptionsBuilder = BigQueryOptions.newBuilder();
        bqOptionsBuilder.setProjectId(gcpDefaultUserProjectId).setLocation("us");
        GoogleCredentials credentials;
        boolean isBqJdkAuthenticatedUsingSaKeyFile;
        File credentialsPath = new File(this.gcpSaKeyPath);
        try (FileInputStream stream = new FileInputStream(credentialsPath)) {
            credentials = ServiceAccountCredentials.fromStream(stream);
            Logger.log("BQ JDK: SETTING SERVICE ACCOUNT CREDENTIALS (GOOGLE_APPLICATION_CREDENTIALS) TO BQ OPTIONS.", Logger.LogType.CYAN);
            bqOptionsBuilder.setCredentials(credentials);
            isBqJdkAuthenticatedUsingSaKeyFile = true;
        } catch (IOException e) {
            Logger.log(e.getMessage(), Logger.LogType.ERROR);
            if (e.getMessage().contains("'type' value 'authorized_user' not recognized. Expecting 'service_account'")) {
                Logger.log("If you're trying to use Application Default Credentials (ADC), use the command:", Logger.LogType.ERROR);
                Logger.log("    gcloud auth application-default print-access-token", Logger.LogType.ERROR);
                Logger.log("to generate a GCP access token and set the output of the command to the \"GCP_ADC_ACCESS_TOKEN\" environment variable.", Logger.LogType.ERROR);
            }
            isBqJdkAuthenticatedUsingSaKeyFile = false;
        }

        logSaKeyFileAuth(isBqJdkAuthenticatedUsingSaKeyFile);

        String adcAccessToken = System.getProperty(CLI_ARG_KEYS.GCP_ADC_ACCESS_TOKEN.name());
        if (!isBqJdkAuthenticatedUsingSaKeyFile && StringUtils.isNotEmpty(adcAccessToken)) {
            GoogleCredentials _credentials = GoogleCredentials.newBuilder()
                .setAccessToken(
                    AccessToken.newBuilder()
                        .setTokenValue(adcAccessToken)
                        .setExpirationTime(Date.from(Instant.now().plusSeconds(60 * 30)))
                        .build()
                )
                .build();
            _credentials.refreshIfExpired();
            bigQuery = bqOptionsBuilder.setCredentials(
                _credentials
            ).build().getService();
            Logger.log("Authenticated successfully using Application Default Credentials (ADC) access token.", Logger.LogType.INFO);
        } else {
            bigQuery = bqOptionsBuilder.build().getService();
            Logger.log("Was not able to authenticate using Application Default Credentials (ADC) access token.", Logger.LogType.INFO);
        }

    }

    private static void logSaKeyFileAuth(boolean isBqJdkAuthenticatedUsingSaKeyFile) {
        if (isBqJdkAuthenticatedUsingSaKeyFile) {
            Logger.log("BigQuery Java SDK has authenticated successfully using a service account key file.", Logger.LogType.INFO);
        } else {
            Logger.log("BigQuery JDK was not able to authenticate using a service account key file.", Logger.LogType.INFO);
            Logger.log("Attempting to authenticate using Application Default Credentials.", Logger.LogType.INFO);
        }
    }

    private static boolean isEmpty(String str) {
        return StringUtils.isEmpty(str);
    }

    private static String getProperty(String key) {
        return System.getProperty(key);
    }

}