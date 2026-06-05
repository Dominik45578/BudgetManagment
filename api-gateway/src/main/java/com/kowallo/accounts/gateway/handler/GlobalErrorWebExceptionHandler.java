package com.kowallo.accounts.gateway.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Component
@Order(-1)
public class GlobalErrorWebExceptionHandler implements ErrorWebExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalErrorWebExceptionHandler.class);
    private final ObjectMapper objectMapper;

    public GlobalErrorWebExceptionHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        ServerHttpResponse response = exchange.getResponse();

        if (response.isCommitted()) {
            return Mono.error(ex);
        }

        // Ustawienie Content-Type na application/json
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        String message = "An unexpected error occurred on the gateway server.";

        // Rozpoznawanie typów wyjątków
        if (ex instanceof ResponseStatusException) {
            status = HttpStatus.valueOf(((ResponseStatusException) ex).getStatusCode().value());
            message = ((ResponseStatusException) ex).getReason();
        } else if (ex.getClass().getSimpleName().equals("ConnectException") || 
                   (ex.getMessage() != null && ex.getMessage().contains("Connection refused"))) {
            status = HttpStatus.SERVICE_UNAVAILABLE;
            message = "The destination service is unavailable. Please ensure budget-service is running.";
        }

        response.setStatusCode(status);

        // Tworzenie struktury błędu
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("timestamp", LocalDateTime.now().toString());
        errorDetails.put("path", exchange.getRequest().getPath().value());
        errorDetails.put("status", status.value());
        errorDetails.put("error", status.getReasonPhrase());
        errorDetails.put("message", message != null ? message : ex.getMessage());
        errorDetails.put("exception", ex.getClass().getName());

        log.error("Błąd Gateway na ścieżce {}: {} (Status: {})", 
                exchange.getRequest().getPath().value(), ex.getMessage(), status.value(), ex);

        byte[] bytes;
        try {
            bytes = objectMapper.writeValueAsBytes(errorDetails);
        } catch (JsonProcessingException e) {
            bytes = "{\"error\": \"Internal Server Error\"}".getBytes();
        }

        DataBuffer buffer = response.bufferFactory().wrap(bytes);
        return response.writeWith(Mono.just(buffer));
    }
}
