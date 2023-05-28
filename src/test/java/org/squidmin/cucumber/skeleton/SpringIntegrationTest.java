package org.squidmin.cucumber.skeleton;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;
import org.squidmin.cucumber.skeleton.service.BigQueryAdminClient;

import java.util.HashMap;
import java.util.Map;

@CucumberContextConfiguration
@SpringBootTest(classes = BddApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class SpringIntegrationTest {

    @Autowired
    protected Belly belly;

    @Autowired
    protected RestTemplate restTemplate;

    @Autowired
    protected BigQueryAdminClient bqAdminClient;

    protected ObjectMapper mapper = new ObjectMapper();

    protected Map<String, Object> testContext = new HashMap<>();

}
