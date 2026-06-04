package com.kowallo.accounts.budget.common.exception;

import org.springframework.http.HttpStatus;

public class ResourceConflictException extends BusinessException {

    public ResourceConflictException(String message, String errorCode) {
        super(message, HttpStatus.CONFLICT, errorCode);
    }
}
