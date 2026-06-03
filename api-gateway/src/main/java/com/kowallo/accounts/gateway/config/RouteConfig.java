package com.kowallo.accounts.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RouteConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()

                // Budget Service — Accounts API
                .route("budget-accounts-route", r -> r
                        .path("/api/v1/accounts/**")
                        .uri("lb://budget-service"))

                // Budget Service — Transactions API
                .route("budget-transactions-route", r -> r
                        .path("/api/v1/transactions/**")
                        .uri("lb://budget-service"))

                // Budget Service — Budget limits API
                .route("budget-limits-route", r -> r
                        .path("/api/v1/budgets/**")
                        .uri("lb://budget-service"))

                // Budget Service — Summary / Reports API
                .route("budget-summary-route", r -> r
                        .path("/api/v1/summary/**")
                        .uri("lb://budget-service"))

                // Budget Service — Swagger/OpenAPI docs
                .route("budget-swagger-route", r -> r
                        .path("/budget-service/v3/api-docs/**")
                        .filters(f -> f.rewritePath(
                                "/budget-service/v3/api-docs(?<segment>.*)",
                                "/v3/api-docs${segment}"))
                        .uri("lb://budget-service"))

                // Budget Service — Swagger UI HTML
                .route("budget-swagger-ui-html", r -> r
                        .path("/swagger-ui.html")
                        .uri("lb://budget-service"))

                // Budget Service — Swagger UI resources
                .route("budget-swagger-ui-resources", r -> r
                        .path("/swagger-ui/**")
                        .uri("lb://budget-service"))

                // Budget Service — OpenAPI docs directly
                .route("budget-openapi-docs", r -> r
                        .path("/v3/api-docs/**")
                        .uri("lb://budget-service"))

                .build();
    }
}
