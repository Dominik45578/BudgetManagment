package com.kowallo.accounts.gateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;

/**
 * Global gateway filter that logs the duration of every proxied request.
 *
 * <p>Captures the start time before the downstream call and calculates
 * the elapsed duration after the response is committed. This provides
 * a single, consistent point for request-level timing across all routes,
 * independent of individual service instrumentation.</p>
 *
 * <p>Runs at {@code Ordered.HIGHEST_PRECEDENCE + 1} — immediately after
 * the {@link CorrelationIdFilter}, so the correlation ID is already
 * available in the exchange for log correlation.</p>
 */
@Component
public class RequestTimingFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(RequestTimingFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        Instant start = Instant.now();

        return chain.filter(exchange)
                .then(Mono.fromRunnable(() -> {
                    Duration elapsed = Duration.between(start, Instant.now());
                    String correlationId = exchange.getResponse().getHeaders()
                            .getFirst(CorrelationIdFilter.CORRELATION_ID_HEADER);

                    log.info("Gateway | {} {} | status={} | duration={}ms | correlationId={}",
                            exchange.getRequest().getMethod(),
                            exchange.getRequest().getURI().getPath(),
                            exchange.getResponse().getStatusCode(),
                            elapsed.toMillis(),
                            correlationId);
                }));
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 1;
    }
}
