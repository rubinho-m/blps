package com.example.demo.services.impl;

import com.example.demo.connector.StripeConnection;
import com.example.demo.exceptions.StripeProcessException;
import com.example.demo.services.StripeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StripeServiceImpl implements StripeService {
    private final StripeConnection stripeConnection;

    @Autowired
    public StripeServiceImpl(StripeConnection stripeConnection) {
        this.stripeConnection = stripeConnection;
    }

    @Override
    public void makePayment(int amount) throws StripeProcessException {
        final boolean success = stripeConnection.makePayment(amount);
        if (!success) {
            throw new StripeProcessException("Failed to make payment");
        }
    }

    @Override
    public void makeErrorPayment(int amount) throws StripeProcessException {
        final boolean success = stripeConnection.makeErrorPayment(amount);
        if (!success) {
            throw new StripeProcessException("Failed to make payment");
        }
    }
}
