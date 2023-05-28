package org.squidmin.cucumber.skeleton.repository;

import org.squidmin.cucumber.skeleton.dao.RecordExample;
import org.squidmin.cucumber.skeleton.dto.ExampleResponse;
import org.squidmin.cucumber.skeleton.dto.Query;

import java.io.IOException;
import java.util.List;

public interface ExampleRepository {

    ExampleResponse restfulQuery(Query query) throws IOException;

    ExampleResponse query(Query query) throws IOException;

    int insert(String projectId, String dataset, String table, List<RecordExample> records);

}
