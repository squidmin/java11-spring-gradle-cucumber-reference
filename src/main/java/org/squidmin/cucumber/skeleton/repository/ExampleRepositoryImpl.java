package org.squidmin.java.spring.gradle.bigquery.repository;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.squidmin.java.spring.gradle.bigquery.dao.RecordExample;
import org.squidmin.java.spring.gradle.bigquery.dto.ExampleResponse;
import org.squidmin.java.spring.gradle.bigquery.dto.ExampleResponseItem;
import org.squidmin.java.spring.gradle.bigquery.dto.Query;
import org.squidmin.java.spring.gradle.bigquery.service.BigQueryAdminClient;
import org.squidmin.java.spring.gradle.bigquery.util.BigQueryUtil;

import java.io.IOException;
import java.util.List;

@Repository
public class ExampleRepositoryImpl implements ExampleRepository {

    private final BigQueryAdminClient bqAdminClient;

    public ExampleRepositoryImpl(BigQueryAdminClient bqAdminClient) {
        this.bqAdminClient = bqAdminClient;
    }

    @Override
    public ExampleResponse restfulQuery(Query query) throws IOException {
        return bqAdminClient.restfulQuery(query).getBody();
    }

    @Override
    public ExampleResponse query(Query query) {
        return ExampleResponse.builder().body(BigQueryUtil.toList(bqAdminClient.query(query.getQuery()))).build();
    }

    @Override
    public int insert(String projectId, String dataset, String table, List<RecordExample> records) {
        int numRowsInserted = bqAdminClient.insert(projectId, dataset, table, records).size();
        return 0 < numRowsInserted ? numRowsInserted : -1;
    }

}
