package org.squidmin.cucumber.skeleton.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Query {

    private String query;
    private boolean useLegacySql;

}
