package com.example.demo;

public class PaymentException extends RuntimeException {
    public PaymentException(String message) {
        super(message);
    }
}
