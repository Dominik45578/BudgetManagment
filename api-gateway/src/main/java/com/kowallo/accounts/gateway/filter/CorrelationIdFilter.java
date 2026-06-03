package com.kowallo.accounts.gateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * Global gateway filter that ensures every request passing through the gateway
 * carries a unique correlation ID for distributed tracing and log correlation.
 *
 * <p>If the incoming request already contains an {@code X-Correlation-Id} header
 * (e.g., from an upstream load balancer or external caller), that value is preserved.
 * Otherwise, a new UUID is generated and injected.</p>
 *
 * <p>The correlation ID is propagated in three places:</p>
 * <ol>
 *   <li>Forwarded to downstream services via the mutated request header</li>
 *   <li>Returned to the client via the response header</li>
 *   <li>Logged at the gateway level for observability</li>
 * </ol>
 *
 * <p>Runs at {@link Ordered#HIGHEST_PRECEDENCE} to guarantee it executes
 * before any other filter in the chain.</p>
 */
@Component
public class CorrelationIdFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(CorrelationIdFilter.class);

    static final String CORRELATION_ID_HEADER = "X-Correlation-Id";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        String correlationId = extractOrGenerate(request);

        ServerHttpRequest mutatedRequest = request.mutate()
                .header(CORRELATION_ID_HEADER, correlationId)
                .build();

        ServerHttpResponse response = exchange.getResponse();
        response.getHeaders().add(CORRELATION_ID_HEADER, correlationId);

        log.debug("Correlation ID [{}] assigned to {} {}",
                correlationId,
                request.getMethod(),
                request.getURI().getPath());

        return chain.filter(exchange.mutate().request(mutatedRequest).build());
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    private String extractOrGenerate(ServerHttpRequest request) {
        String existing = request.getHeaders().getFirst(CORRELATION_ID_HEADER);
        if (existing != null && !existing.isBlank()) {
            return existing;
        }
        return UUID.randomUUID().toString();
    }
}
