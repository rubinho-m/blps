package com.example.demo.connector;

import com.stripe.Stripe;
import jakarta.resource.ResourceException;
import jakarta.resource.cci.Connection;
import jakarta.resource.cci.ConnectionMetaData;
import jakarta.resource.cci.Interaction;
import jakarta.resource.cci.LocalTransaction;
import jakarta.resource.cci.RecordFactory;
import jakarta.resource.cci.ResultSetInfo;

import java.util.Map;

public class StripeConnection implements Connection {
    private volatile boolean closed = false;
    private final RecordFactory recordFactory = new StripeRecordFactory();

    public StripeConnection(String apiKey) {
        Stripe.apiKey = apiKey;
    }

    public boolean makePayment(int amount) {
        return makePaymentInternal(amount, StripeFunction.CREATE_PAYMENT);
    }

    public boolean makeErrorPayment(int amount) {
        return makePaymentInternal(amount, StripeFunction.CREATE_ERROR_PAYMENT);
    }

    private boolean makePaymentInternal(int amount, StripeFunction stripeFunction) {
        try {
            final Map<String, Object> metadata = Map.of(
                    "currency", "usd",
                    "amount", (long) amount
            );
            final Interaction interaction = createInteraction();
            final StripeMappedRecord input = (StripeMappedRecord) recordFactory.createMappedRecord("Stripe Payment Request");
            final StripeMappedRecord output = (StripeMappedRecord) recordFactory.createMappedRecord("Stripe Payment Response");
            input.setParameters(metadata);
            final StripeInteractionSpec interactionSpec = new StripeInteractionSpec(stripeFunction);
            return interaction.execute(interactionSpec, input, output);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Interaction createInteraction() throws ResourceException {
        checkIfClosed();
        return new StripeInteraction(this);
    }

    @Override
    public LocalTransaction getLocalTransaction() throws ResourceException {
        checkIfClosed();
        throw new ResourceException("Local transactions are not supported");
    }

    @Override
    public ConnectionMetaData getMetaData() throws ResourceException {
        checkIfClosed();
        return new StripeConnectionMetaData();
    }

    @Override
    public ResultSetInfo getResultSetInfo() throws ResourceException {
        checkIfClosed();
        throw new ResourceException("ResultSet is not supported");
    }

    @Override
    public void close() {
        closed = true;
    }

    private void checkIfClosed() throws ResourceException {
        if (closed) {
            throw new ResourceException("Connection is already closed");
        }
    }
}
