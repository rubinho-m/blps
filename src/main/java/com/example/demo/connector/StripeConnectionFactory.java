package com.example.demo.connector;

import jakarta.resource.ResourceException;
import jakarta.resource.cci.Connection;
import jakarta.resource.cci.ConnectionFactory;
import jakarta.resource.cci.ConnectionSpec;
import jakarta.resource.cci.RecordFactory;
import jakarta.resource.cci.ResourceAdapterMetaData;
import jakarta.resource.spi.ConnectionManager;

import javax.naming.Reference;

public class StripeConnectionFactory implements ConnectionFactory {
    private final StripeManagedConnectionFactory stripeManagedConnectionFactory;
    private final ConnectionManager connectionManager;

    public StripeConnectionFactory(StripeManagedConnectionFactory stripeManagedConnectionFactory, ConnectionManager connectionManager) {
        this.stripeManagedConnectionFactory = stripeManagedConnectionFactory;
        this.connectionManager = connectionManager;
    }

    @Override
    public Connection getConnection(ConnectionSpec connectionSpec) throws ResourceException {
        if (connectionSpec instanceof StripeConnectionSpec stripeConnectionSpec) {
            final StripeConnectionRequestInfo stripeConnectionRequestInfo = new StripeConnectionRequestInfo(
                    stripeConnectionSpec.apiKey()
            );
            if (connectionManager != null) {
                return (StripeConnection) connectionManager.allocateConnection(
                        stripeManagedConnectionFactory, stripeConnectionRequestInfo
                );
            }
            final StripeManagedConnection stripeManagedConnection = (StripeManagedConnection) stripeManagedConnectionFactory.createManagedConnection(null, stripeConnectionRequestInfo);
            return (StripeConnection) stripeManagedConnection.getConnection();
        }
        throw new ResourceException("ConnectionSpec is not a StripeConnectionSpec");

    }

    @Override
    public Connection getConnection() throws ResourceException {
        final StripeManagedConnection stripeManagedConnection = (StripeManagedConnection) stripeManagedConnectionFactory.createManagedConnection(null, null);
        return (StripeConnection) stripeManagedConnection.getConnection();
    }

    @Override
    public ResourceAdapterMetaData getMetaData() {
        return new StripeResourceAdapterMetaData();
    }

    @Override
    public RecordFactory getRecordFactory() {
        throw new UnsupportedOperationException("Record factories are not supported");
    }
    @Override
    public void setReference(Reference reference) {
        throw new UnsupportedOperationException("JNDI lookup not supported");
    }

    @Override
    public Reference getReference() {
        throw new UnsupportedOperationException("JNDI lookup not supported");
    }
}
