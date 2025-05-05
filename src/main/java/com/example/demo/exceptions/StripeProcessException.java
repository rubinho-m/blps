package com.example.demo.exceptions;

public class StripeProcessException extends Exception {
    public StripeProcessException(String message) {
        super(message);
    }
}
