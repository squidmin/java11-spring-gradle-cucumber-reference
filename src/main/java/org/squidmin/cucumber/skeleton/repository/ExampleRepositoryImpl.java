package org.squidmin.cucumber.skeleton.repository;

import org.springframework.stereotype.Repository;
import org.squidmin.cucumber.skeleton.dao.RecordExample;
import org.squidmin.cucumber.skeleton.dto.ExampleResponse;
import org.squidmin.cucumber.skeleton.dto.Query;
import org.squidmin.cucumber.skeleton.service.BigQueryAdminClient;
import org.squidmin.cucumber.skeleton.util.BigQueryUtil;

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
        return ExampleResponse.builder().entries(BigQueryUtil.toList(bqAdminClient.query(query.getQuery()))).build();
    }

    @Override
    public int insert(String projectId, String dataset, String table, List<RecordExample> records) {
        int numRowsInserted = bqAdminClient.insert(projectId, dataset, table, records).size();
        return 0 < numRowsInserted ? numRowsInserted : -1;
    }

}
