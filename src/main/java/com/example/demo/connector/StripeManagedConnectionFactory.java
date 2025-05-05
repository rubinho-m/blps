package com.example.demo.connector;

import jakarta.resource.ResourceException;
import jakarta.resource.cci.Connection;
import jakarta.resource.cci.ConnectionFactory;
import jakarta.resource.spi.ConnectionDefinition;
import jakarta.resource.spi.ConnectionManager;
import jakarta.resource.spi.ConnectionRequestInfo;
import jakarta.resource.spi.ManagedConnection;
import jakarta.resource.spi.ManagedConnectionFactory;
import jakarta.resource.spi.ResourceAdapter;
import jakarta.resource.spi.ResourceAdapterAssociation;

import javax.security.auth.Subject;
import java.io.PrintWriter;
import java.util.Objects;
import java.util.Set;

@ConnectionDefinition(connectionFactory = ConnectionFactory.class, connectionFactoryImpl = StripeConnectionFactory.class, connection = Connection.class, connectionImpl = StripeConnection.class)
public class StripeManagedConnectionFactory implements ManagedConnectionFactory, ResourceAdapterAssociation {
    private final String apiKey;
    private PrintWriter logWriter;
    private ResourceAdapter resourceAdapter;

    public StripeManagedConnectionFactory(String apiKey) {
        this.apiKey = apiKey;
    }

    @Override
    public Object createConnectionFactory(ConnectionManager connectionManager) throws ResourceException {
        return new StripeConnectionFactory(this, connectionManager);
    }

    @Override
    public Object createConnectionFactory() throws ResourceException {
        return new StripeConnectionFactory(this, null);
    }

    @Override
    public ManagedConnection createManagedConnection(Subject subject, ConnectionRequestInfo connectionRequestInfo) throws ResourceException {
        if (connectionRequestInfo == null) {
            return new StripeManagedConnection(apiKey);
        }
        if (connectionRequestInfo instanceof StripeConnectionRequestInfo stripeConnectionRequestInfo) {
            return new StripeManagedConnection(stripeConnectionRequestInfo.apiKey());
        }
        throw new ResourceException("Invalid connection request info: " + connectionRequestInfo);
    }

    @Override
    public ManagedConnection matchManagedConnections(Set set, Subject subject, ConnectionRequestInfo connectionRequestInfo) throws ResourceException {
        for (Object connection : set) {
            if (connection instanceof StripeManagedConnection stripeManagedConnection) {
               return stripeManagedConnection;
            }
        }
        return null;
    }

    @Override
    public void setLogWriter(PrintWriter printWriter) throws ResourceException {
        this.logWriter = printWriter;
    }

    @Override
    public PrintWriter getLogWriter() throws ResourceException {
        return logWriter;
    }

    @Override
    public ResourceAdapter getResourceAdapter() {
        return resourceAdapter;
    }

    @Override
    public void setResourceAdapter(ResourceAdapter resourceAdapter) throws ResourceException {
        if (resourceAdapter instanceof StripeResourceAdapter stripeResourceAdapter) {
            this.resourceAdapter = stripeResourceAdapter;
        }
        throw new ResourceException("This stripe resource adapter not supported");
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        StripeManagedConnectionFactory that = (StripeManagedConnectionFactory) object;
        return Objects.equals(apiKey, that.apiKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(apiKey);
    }
}
