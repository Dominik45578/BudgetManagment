package com.kowallo.accounts.budget.common.exception;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends BusinessException {

    public ResourceNotFoundException(String resourceName, Object resourceId) {
        super(String.format("%s not found with identifier: %s", resourceName, resourceId), 
              HttpStatus.NOT_FOUND, 
              resourceName.toUpperCase() + "_NOT_FOUND");
    }
}
