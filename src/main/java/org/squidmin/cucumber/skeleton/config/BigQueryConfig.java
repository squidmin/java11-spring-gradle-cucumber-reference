package org.squidmin.java.spring.gradle.bigquery.config;

import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.BigQueryOptions;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.squidmin.java.spring.gradle.bigquery.config.tables.sandbox.SchemaDefault;
import org.squidmin.java.spring.gradle.bigquery.config.tables.sandbox.SelectFieldsDefault;
import org.squidmin.java.spring.gradle.bigquery.config.tables.sandbox.WhereFieldsDefault;
import org.squidmin.java.spring.gradle.bigquery.logger.Logger;
import org.squidmin.java.spring.gradle.bigquery.util.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@Configuration
@ComponentScan(basePackages = {
    "org.squidmin.java.spring.gradle.bigquery"
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
                          WhereFieldsDefault whereFieldsDefault) {

        this.gcpDefaultUserProjectId = gcpDefaultUserProjectId;
        this.gcpDefaultUserDataset = gcpDefaultUserDataset;
        this.gcpDefaultUserTable = gcpDefaultUserTable;

        this.gcpSaProjectId = gcpSaProjectId;
        this.gcpSaDataset = gcpSaDataset;
        this.gcpSaTable = gcpSaTable;

        this.queryUri = queryUri;

        this.gcpSaKeyPath = System.getProperty("GCP_SA_KEY_PATH");
//        Logger.log(String.format("BQ JDK: GCP_SA_KEY_PATH == %s", this.gcpSaKeyPath), Logger.LogType.CYAN);
        File credentialsPath = new File(gcpSaKeyPath);

        this.gcpAdcAccessToken = System.getProperty("GCP_ADC_ACCESS_TOKEN");
//        Logger.log(String.format("GCP_ADC_ACCESS_TOKEN == %s", this.gcpAdcAccessToken), Logger.LogType.CYAN);

        this.gcpSaAccessToken = System.getProperty("GCP_SA_ACCESS_TOKEN");
//        Logger.log(String.format("GCP_SA_ACCESS_TOKEN == %s", this.gcpSaAccessToken), Logger.LogType.CYAN);

        this.schemaDefault = schemaDefault;
        this.dataTypes = dataTypes;
        this.selectFieldsDefault = selectFieldsDefault;
        this.whereFieldsDefault = whereFieldsDefault;

        BigQueryOptions.Builder bqOptionsBuilder = BigQueryOptions.newBuilder();
        bqOptionsBuilder.setProjectId(gcpDefaultUserProjectId).setLocation("us");
        GoogleCredentials credentials;
        boolean isBqJdkAuthenticatedUsingSaKeyFile;
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

        String adcAccessToken = System.getProperty("GCP_ADC_ACCESS_TOKEN");
        if (!isBqJdkAuthenticatedUsingSaKeyFile && StringUtils.isNotEmpty(adcAccessToken)) {
            bigQuery = bqOptionsBuilder.setCredentials(
                GoogleCredentials.newBuilder()
                    .setAccessToken(AccessToken.newBuilder().setTokenValue(adcAccessToken).build())
                    .build()
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

}