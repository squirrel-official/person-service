package com.squirrel.persons;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableScheduling
public class PersonServiceApplication {
    private static final Logger LOGGER = LogManager.getLogger(PersonServiceApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(PersonServiceApplication.class, args);
    }

    @Bean
    public CommandLineRunner CommandLineRunnerBean() {
        return (args) -> {
            Process process = Runtime.getRuntime().exec("sh /usr/local/person-service/src/main/resources/detection.sh");
            LOGGER.debug("detection process started {}", process);
        };
    }
}
