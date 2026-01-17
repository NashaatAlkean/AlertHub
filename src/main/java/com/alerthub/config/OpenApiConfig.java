package com.alerthub.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI evaluationServiceOpenAPI() {
        Server devServer = new Server();
        devServer.setUrl("http://localhost:8085");
        devServer.setDescription("Development Server");

        Contact contact = new Contact();
        contact.setName("Alert Hub Team");
        contact.setEmail("support@alerthub.com");

        License license = new License()
                .name("Apache 2.0")
                .url("https://www.apache.org/licenses/LICENSE-2.0");

        Info info = new Info()
                .title("Evaluation Service API")
                .version("1.0.0")
                .contact(contact)
                .description("This API provides endpoints for evaluating developer performance " +
                        "and workload based on task management data from various platforms " +
                        "(Jira, GitHub, ClickUp).")
                .license(license);

        return new OpenAPI()
                .info(info)
                .servers(List.of(devServer));
    }
}