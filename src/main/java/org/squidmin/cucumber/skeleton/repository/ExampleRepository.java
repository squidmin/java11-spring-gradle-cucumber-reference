package org.squidmin.java.spring.gradle.bigquery.repository;

import org.squidmin.java.spring.gradle.bigquery.dao.RecordExample;
import org.squidmin.java.spring.gradle.bigquery.dto.ExampleResponse;
import org.squidmin.java.spring.gradle.bigquery.dto.Query;

import java.io.IOException;
import java.util.List;

public interface ExampleRepository {

    ExampleResponse restfulQuery(Query query) throws IOException;

    ExampleResponse query(Query query) throws IOException;

    int insert(String projectId, String dataset, String table, List<RecordExample> records);

}
