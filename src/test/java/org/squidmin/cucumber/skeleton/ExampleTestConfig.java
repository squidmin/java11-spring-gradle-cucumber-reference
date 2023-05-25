package org.squidmin.cucumber.skeleton;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class ExampleTestConfig {

    @Value("${cukes}")
    private String cukes;

    private Belly belly;

    @Bean
    public String exampleBean() {
        return cukes;
    }

    @Bean
    public Belly belly() {
        return new Belly();
    }

}
