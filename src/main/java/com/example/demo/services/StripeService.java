package com.example.demo.services;

import com.example.demo.exceptions.StripeProcessException;

public interface StripeService {
    void makePayment(int amount) throws StripeProcessException;

    void makeErrorPayment(int amount) throws StripeProcessException;
}
