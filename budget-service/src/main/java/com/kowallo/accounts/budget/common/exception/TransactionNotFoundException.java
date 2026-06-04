package com.kowallo.accounts.budget.common.exception;

import java.util.UUID;

public class TransactionNotFoundException extends ResourceNotFoundException {
    public TransactionNotFoundException(UUID transactionId) {
        super("Transaction", transactionId);
    }
}
