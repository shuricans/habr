package no.war.habr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class BackendApiAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackendApiAppApplication.class, args);
    }

}
