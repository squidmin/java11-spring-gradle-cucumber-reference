package org.squidmin.cucumber.skeleton;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import org.springframework.http.ResponseEntity;
import org.squidmin.cucumber.skeleton.dto.ExampleResponse;
import org.squidmin.cucumber.skeleton.dto.Query;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

public class BigQueryStepDefinitions extends SpringIntegrationTest {

    @Given("the table {string} exists")
    public void the_table_exists(String table) {
        Assertions.assertTrue(bqAdminClient.tableExists(bqAdminClient.getGcpDefaultUserDataset(), table));
    }

    @When("I fetch rows from the table given query: {string}")
    public void get_row_from_table(String query) throws IOException {
        ResponseEntity<ExampleResponse> responseEntity = bqAdminClient.restfulQuery(Query.builder().query(query).useLegacySql(false).build());
        testContext.put("responseEntity", responseEntity);
    }

    @Then("I receive a non-empty result")
    public void i_receive_a_non_empty_result() {
        Object obj = testContext.getOrDefault("responseEntity", ResponseEntity.of(Optional.of(new Object())));
        ResponseEntity<ExampleResponse> responseEntity = null;
        if (obj instanceof ResponseEntity && ((ResponseEntity<?>) Objects.requireNonNull(obj)).getBody() instanceof ExampleResponse) {
            responseEntity = (ResponseEntity<ExampleResponse>) obj;
        }
        Assertions.assertNotNull(responseEntity);
        Assertions.assertNotNull(responseEntity.getBody());
        Assertions.assertNotNull(responseEntity.getBody().getEntries());
        Assertions.assertTrue(0 < responseEntity.getBody().getEntries().size());
    }

}
