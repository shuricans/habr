package no.war.habr.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
            title = "Habr",
            description = "picture service api",
            version = "0.0.1-SNAPSHOT"
        ),
        servers = {
            @Server(url = "/api/v1", description = "Server URL")
        })
public class DocumentationConfiguration {
}