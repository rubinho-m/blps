package com.example.demo.connector;

import jakarta.resource.ResourceException;
import jakarta.resource.cci.ConnectionMetaData;
import jakarta.resource.spi.ManagedConnectionMetaData;

public class StripeConnectionMetaData implements ConnectionMetaData, ManagedConnectionMetaData {
    @Override
    public String getEISProductName() throws ResourceException {
        return "Stripe";
    }

    @Override
    public String getEISProductVersion() throws ResourceException {
        return "29";
    }

    @Override
    public int getMaxConnections() throws ResourceException {
        return 10;
    }

    @Override
    public String getUserName() throws ResourceException {
        return "stripe_service_user";
    }
}
