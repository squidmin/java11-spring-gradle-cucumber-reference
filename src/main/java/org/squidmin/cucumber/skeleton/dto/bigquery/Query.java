package org.squidmin.cucumber.skeleton.dto.bigquery;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Query {

    private String query;
    private String useLegacySql;

}
