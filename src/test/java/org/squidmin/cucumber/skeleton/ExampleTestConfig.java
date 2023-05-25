package org.squidmin.cucumber.skeleton;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class ExampleTestConfig {

    @Bean
    public Belly belly() {
        return new Belly();
    }

}
