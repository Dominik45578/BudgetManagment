package com.kowallo.accounts.budget.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Value("${spring.application.name:budget-service}")
    private String applicationName;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Personal Budget Management API")
                        .version("v1.0")
                        .description("REST API for personal budget management. " +
                                     "Supports multiple accounts, transactions with automatic balance adjustments, " +
                                     "and monthly limits per category.")
                        .contact(new Contact()
                                .name("Software Engineer Intern Recruitment Team")
                                .email("dominikkoralik59@gmail.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("API Gateway (Standard entry point)"),
                        new Server().url("http://localhost:8081").description("Direct Microservice URL (Development)")
                ))
                .tags(List.of(
                        new Tag().name("Accounts").description("Operations related to managing accounts"),
                        new Tag().name("Budgets").description("Operations related to managing monthly category budget limits"),
                        new Tag().name("Transactions").description("Operations related to managing financial transactions"),
                        new Tag().name("Summaries").description("Operations related to retrieving account financial summaries")
                ));
    }
}
