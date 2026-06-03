package com.kowallo.accounts.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Programmatic route definitions for the API Gateway.
 *
 * <p>Supplements the YAML-based route configuration with Java-defined routes.
 * This approach is preferable for routes that require complex predicates,
 * custom filter chains, or conditional logic that is cumbersome to express in YAML.</p>
 *
 * <p>Routes defined here coexist with the YAML routes — Spring Cloud Gateway
 * merges both sources into a single route registry.</p>
 */
@Configuration
public class RouteConfig {

    /**
     * Defines programmatic routes for services discovered via Eureka.
     *
     * <p>The {@code lb://} prefix enables client-side load balancing through
     * Spring Cloud LoadBalancer, distributing requests across all registered
     * instances of the target service.</p>
     */
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

                // Budget Service — Swagger/OpenAPI docs (proxied for aggregated docs)
                .route("budget-swagger-route", r -> r
                        .path("/budget-service/v3/api-docs/**")
                        .filters(f -> f.rewritePath(
                                "/budget-service/v3/api-docs(?<segment>.*)",
                                "/v3/api-docs${segment}"))
                        .uri("lb://budget-service"))

                .build();
    }
}
