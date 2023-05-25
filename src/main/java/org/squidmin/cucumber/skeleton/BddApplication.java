package org.squidmin.cucumber.skeleton;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude={DataSourceAutoConfiguration.class})
public class BddApplication {

    public static void main(String[] args) {
        SpringApplication.run(BddApplication.class, args);
    }

}
