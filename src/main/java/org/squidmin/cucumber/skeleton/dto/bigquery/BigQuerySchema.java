package org.squidmin.cucumber.skeleton.dto.bigquery;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BigQuerySchema {

    private List<BigQuerySchemaItem> fields;

}
