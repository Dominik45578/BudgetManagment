package com.kowallo.accounts.budget.advisor;

import com.kowallo.accounts.budget.common.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.time.Instant;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE) // Handle domain business errors first
public class DomainExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(DomainExceptionHandler.class);

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ProblemDetail> handleBusinessException(BusinessException ex) {
        log.warn("Business rule violation [{}]: {}", ex.getErrorCode(), ex.getMessage());

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                ex.getStatus(),
                ex.getMessage()
        );
        
        problemDetail.setTitle("Business Exception");
        problemDetail.setType(URI.create("https://api.kowallo.com/errors/" + ex.getErrorCode().toLowerCase().replace("_", "-")));
        problemDetail.setProperty("code", ex.getErrorCode());
        problemDetail.setProperty("timestamp", Instant.now());

        return ResponseEntity.status(ex.getStatus()).body(problemDetail);
    }
}
