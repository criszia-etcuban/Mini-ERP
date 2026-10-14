package com.example.minierp.exception;

public class InvalidPurchaseOrderException extends RuntimeException {
    public InvalidPurchaseOrderException (String message) {
        super(message);
    }
}
