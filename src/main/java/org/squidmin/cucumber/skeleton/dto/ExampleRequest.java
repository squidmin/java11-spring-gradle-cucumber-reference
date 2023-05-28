package org.squidmin.cucumber.skeleton.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExampleRequest {

    private String projectId;
    private String dataset;
    private String table;

    private List<ExampleRequestItem> body;

}
