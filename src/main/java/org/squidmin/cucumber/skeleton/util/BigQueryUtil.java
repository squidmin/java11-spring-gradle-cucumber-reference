package org.squidmin.java.spring.gradle.bigquery.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.gax.paging.Page;
import com.google.cloud.bigquery.*;
import lombok.extern.slf4j.Slf4j;
import org.squidmin.java.spring.gradle.bigquery.config.DataTypes;
import org.squidmin.java.spring.gradle.bigquery.config.tables.sandbox.SchemaDefault;
import org.squidmin.java.spring.gradle.bigquery.config.tables.sandbox.SelectFieldsDefault;
import org.squidmin.java.spring.gradle.bigquery.dto.ExampleResponseItem;
import org.squidmin.java.spring.gradle.bigquery.dto.bigquery.BigQueryRestServiceResponse;
import org.squidmin.java.spring.gradle.bigquery.dto.bigquery.BigQueryRow;
import org.squidmin.java.spring.gradle.bigquery.dto.bigquery.BigQueryRowValue;
import org.squidmin.java.spring.gradle.bigquery.logger.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class BigQueryUtil {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static List<ExampleResponseItem> toList(TableResult tableResult) {
        List<ExampleResponseItem> response = new ArrayList<>();
        if (null != tableResult && 0 < tableResult.getTotalRows()) {
            tableResult.iterateAll().forEach(
                row -> response.add(
                    ExampleResponseItem.builder()
                        .id(row.get(0).getStringValue())
                        .creationTimestamp(row.get(1).getStringValue())
                        .lastUpdateTimestamp(row.get(2).getStringValue())
                        .columnA(row.get(3).getStringValue())
                        .columnB(row.get(4).getStringValue())
                        .build()
                )
            );
        }
        return response;
    }

    public static List<ExampleResponseItem> toList(byte[] tableResult, SelectFieldsDefault selectFieldsDefault, boolean isSelectAll) throws IOException {
        List<ExampleResponseItem> response = new ArrayList<>();
        BigQueryRestServiceResponse bqResponse = mapper.readValue(tableResult, BigQueryRestServiceResponse.class);
        List<BigQueryRow> rows = bqResponse.getRows();
        if (null != rows) {
            for (BigQueryRow r : rows) {
                ExampleResponseItem exampleResponseItem = new ExampleResponseItem();
                List<BigQueryRowValue> f = r.getF();
                if (!isSelectAll) {
                    for (int j = 0; j < selectFieldsDefault.getFields().size(); j++) {
                        String name = selectFieldsDefault.getFields().get(j);
                        setResponseItem(exampleResponseItem, f, j, name);
                    }
                    response.add(exampleResponseItem);
                } else {
                    for (int j = 0; j < bqResponse.getSchema().getFields().size(); j++) {
                        String name = bqResponse.getSchema().getFields().get(j).getName();
                        setResponseItem(exampleResponseItem, f, j, name);
                    }
                    response.add(exampleResponseItem);
                }
            }
        }
        return response;
    }

    private static void setResponseItem(ExampleResponseItem exampleResponseItem, List<BigQueryRowValue> f, int index, String name) {
        BigQueryRowValue v = f.get(index);
        String value = v.getV();
        exampleResponseItem.setFromBigQueryResponse(name, value);
    }

    public static void logDataTypes(DataTypes dataTypes) {
        Logger.log("Supported data types: ", Logger.LogType.CYAN);
        dataTypes.getDataTypes().forEach(type -> Logger.log(type, Logger.LogType.CYAN));
    }

    public static void logTableInfo(TableInfo tableInfo) {
        log.info("Friendly name: " + tableInfo.getFriendlyName());
        log.info("Description: " + tableInfo.getDescription());
        log.info("Creation time: " + tableInfo.getCreationTime());
        log.info("Expiration time: " + tableInfo.getExpirationTime());
    }

    public static void logDatasets(String projectId, Page<Dataset> datasets) {
        Logger.echoHorizontalLine(Logger.LogType.INFO);
        Logger.log(String.format("Project \"%s\" datasets:", projectId), Logger.LogType.INFO);
        Logger.echoHorizontalLine(Logger.LogType.INFO);
        datasets.iterateAll().forEach(
            dataset -> Logger.log(
                String.format("Dataset ID: %s", dataset.getDatasetId()),
                Logger.LogType.INFO
            )
        );
        Logger.echoHorizontalLine(Logger.LogType.INFO);
    }

    public static void logCreateTable(TableInfo tableInfo) {
        Logger.echoHorizontalLine(Logger.LogType.INFO);
        Logger.log(
            String.format("Creating table \"%s\". Find the table information below:", tableInfo.getTableId()),
            Logger.LogType.INFO
        );
        logTableInfo(tableInfo);
        Logger.echoHorizontalLine(Logger.LogType.INFO);
    }

    public static void logRunConfig() {
        Logger.echoHorizontalLine(Logger.LogType.CYAN);
        Logger.log("Run config:", Logger.LogType.CYAN);
        Logger.echoHorizontalLine(Logger.LogType.CYAN);
        Logger.log(String.format("PROFILE                         %s", System.getProperty("PROFILE")), Logger.LogType.CYAN);
        Logger.log(String.format("GCP_SA_KEY_PATH                 %s", System.getProperty("GCP_SA_KEY_PATH")), Logger.LogType.CYAN);
        Logger.log(String.format("GCP_ADC_ACCESS_TOKEN            %s", System.getProperty("GCP_ADC_ACCESS_TOKEN").substring(0, 9).concat("...")), Logger.LogType.CYAN);
        Logger.log(String.format("GCP_SA_ACCESS_TOKEN             %s", System.getProperty("GCP_SA_ACCESS_TOKEN")).substring(0, 9).concat("..."), Logger.LogType.CYAN);
        Logger.log(String.format("GCP_DEFAULT_USER_PROJECT_ID     %s", System.getProperty("GCP_DEFAULT_USER_PROJECT_ID")), Logger.LogType.CYAN);
        Logger.log(String.format("GCP_DEFAULT_USER_DATASET        %s", System.getProperty("GCP_DEFAULT_USER_DATASET")), Logger.LogType.CYAN);
        Logger.log(String.format("GCP_DEFAULT_USER_TABLE          %s", System.getProperty("GCP_DEFAULT_USER_TABLE")), Logger.LogType.CYAN);
        Logger.log(String.format("GCP_SA_PROJECT_ID               %s", System.getProperty("GCP_SA_PROJECT_ID")), Logger.LogType.CYAN);
        Logger.log(String.format("GCP_SA_DATASET                  %s", System.getProperty("GCP_SA_DATASET")), Logger.LogType.CYAN);
        Logger.log(String.format("GCP_SA_TABLE                    %s", System.getProperty("GCP_SA_TABLE")), Logger.LogType.CYAN);
        Logger.echoHorizontalLine(Logger.LogType.CYAN);
    }

    public enum ProfileOption {DEFAULT, OVERRIDDEN, ACTIVE}

    public static void logBqProperties(RunEnvironment runEnvironment, ProfileOption profileOption) {
        Logger.echoHorizontalLine(Logger.LogType.INFO);
        if (profileOption == ProfileOption.DEFAULT) {
            Logger.log("--- BigQuery default properties ---", Logger.LogType.CYAN);
            logBqProperties(
                runEnvironment.getGcpDefaultUserProjectIdDefault(),
                runEnvironment.getGcpDefaultUserDatasetDefault(),
                runEnvironment.getGcpDefaultUserTableDefault(),
                runEnvironment.getGcpSaProjectIdDefault(),
                runEnvironment.getGcpSaDatasetDefault(),
                runEnvironment.getGcpSaTableDefault()
            );
        } else if (profileOption == ProfileOption.OVERRIDDEN) {
            Logger.log("--- BigQuery overridden properties ---", Logger.LogType.CYAN);
            logBqProperties(
                runEnvironment.getGcpDefaultUserProjectIdOverride(),
                runEnvironment.getGcpDefaultUserDatasetOverride(),
                runEnvironment.getGcpDefaultUserTableOverride(),
                runEnvironment.getGcpSaProjectIdOverride(),
                runEnvironment.getGcpSaDatasetOverride(),
                runEnvironment.getGcpSaTableOverride()
            );
        } else if (profileOption == ProfileOption.ACTIVE) {
            Logger.log("BigQuery resource properties currently configured:", Logger.LogType.CYAN);
            logBqProperties(
                runEnvironment.getGcpDefaultUserProjectId(),
                runEnvironment.getGcpDefaultUserDataset(),
                runEnvironment.getGcpDefaultUserTable(),
                runEnvironment.getGcpSaProjectId(),
                runEnvironment.getGcpSaDataset(),
                runEnvironment.getGcpSaTable()
            );
        } else {
            log.error("Error: IntegrationTest.echoBigQueryResourceMetadata(): Invalid option specified.");
        }
        Logger.echoHorizontalLine(Logger.LogType.INFO);
    }

    public static void logBqProperties(
        String gcpDefaultUserProjectId, String gcpDefaultUserDataset, String gcpDefaultUserTable,
        String gcpSaProjectId, String gcpSaDataset, String gcpSaTable) {

        Logger.log(String.format("Default Project ID: %s", gcpDefaultUserProjectId), Logger.LogType.INFO);
        Logger.log(String.format("Default Dataset name: %s", gcpDefaultUserDataset), Logger.LogType.INFO);
        Logger.log(String.format("Default Table name: %s", gcpDefaultUserTable), Logger.LogType.INFO);

        Logger.log(String.format("Service account Project ID: %s", gcpSaProjectId), Logger.LogType.INFO);
        Logger.log(String.format("Service account Dataset name: %s", gcpSaDataset), Logger.LogType.INFO);
        Logger.log(String.format("Service account Table name: %s", gcpSaTable), Logger.LogType.INFO);
    }

    public static class InlineSchemaTranslator {
        public static Schema translate(SchemaDefault schemaDefault, DataTypes dataTypes) {
            Logger.log(String.format("Generating Schema object using: \"%s\"...", schemaDefault.getFields()), Logger.LogType.CYAN);
            List<Field> fields = new ArrayList<>();
            schemaDefault.getFields().forEach(
                f -> {
                    log.info("name={}, type={}", f.getName(), f.getType());
                    fields.add(
                        com.google.cloud.bigquery.Field.of(
                            f.getName(),
                            translateType(f.getType(), dataTypes)
                        )
                    );
                }
            );
            Logger.log("Finished generating schema.", Logger.LogType.CYAN);
            return com.google.cloud.bigquery.Schema.of(fields);
        }

        public static Schema translate(String schema, DataTypes dataTypes) {
            Logger.log(String.format("Generating Schema object using CLI arg: \"%s\"...", schema), Logger.LogType.CYAN);
            List<Field> fields = new ArrayList<>();
            List<String> _fields = Arrays.stream(schema.split(",")).collect(Collectors.toList());
            _fields.forEach(
                f -> {
                    String[] split = f.split(":");
                    String name = split[0], type = split[1];
                    log.info("name={}, type={}", name, type);
                    fields.add(
                        com.google.cloud.bigquery.Field.of(
                            name,
                            translateType(type, dataTypes)
                        )
                    );
                }
            );
            Logger.log("Finished generating schema.", Logger.LogType.CYAN);
            return com.google.cloud.bigquery.Schema.of(fields);
        }

        private static StandardSQLTypeName translateType(String type, DataTypes dataTypes) {
            if (dataTypes.getDataTypes().contains(type)) {
                return StandardSQLTypeName.valueOf(type);
            } else {
                Logger.log(
                    "Error: BigQueryConfig.translateType(): Data type not supported. Defaulting to 'StandardSQLTypeNames.STRING'.",
                    Logger.LogType.ERROR
                );
                logDataTypes(dataTypes);
                return StandardSQLTypeName.STRING;
            }
        }
    }

}
