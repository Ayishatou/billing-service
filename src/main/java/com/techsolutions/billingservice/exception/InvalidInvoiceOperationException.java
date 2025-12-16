package com.techsolutions.billingservice.exception;

public class InvalidInvoiceOperationException extends RuntimeException {
    public InvalidInvoiceOperationException(String message) {
        super(message);
    }
}
