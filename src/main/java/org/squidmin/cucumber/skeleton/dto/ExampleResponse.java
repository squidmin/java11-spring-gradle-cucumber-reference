package org.squidmin.cucumber.skeleton.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class ExampleResponse {

    private List<ExampleResponseItem> entries;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<String> errors;

}
