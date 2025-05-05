package com.example.demo.connector;

import jakarta.resource.ResourceException;
import jakarta.resource.spi.ConnectionEventListener;
import jakarta.resource.spi.ConnectionRequestInfo;
import jakarta.resource.spi.LocalTransaction;
import jakarta.resource.spi.ManagedConnection;
import jakarta.resource.spi.ManagedConnectionMetaData;
import org.apache.commons.lang3.StringUtils;

import javax.security.auth.Subject;
import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class StripeManagedConnection implements ManagedConnection {
    private PrintWriter logWriter;
    private final List<ConnectionEventListener> listeners = new ArrayList<>();
    private final List<StripeConnection> connections = new ArrayList<>();
    private final String apiKey;


    public StripeManagedConnection(String apiKey) {
        this.apiKey = apiKey;
    }

    @Override
    public Object getConnection(Subject subject, ConnectionRequestInfo connectionRequestInfo) throws ResourceException {
        if (connectionRequestInfo instanceof StripeConnectionRequestInfo stripeConnectionRequestInfo) {
            StripeConnection connection = new StripeConnection(stripeConnectionRequestInfo.apiKey());
            connections.add(connection);
            return connection;
        }
       throw new ResourceException("Unsupported connectionRequestInfo: " + connectionRequestInfo);
    }

    public Object getConnection() throws ResourceException {
        if (StringUtils.isBlank(apiKey)) {
            throw new ResourceException("apiKey is required");
        }
        return new StripeConnection(apiKey);
    }

    @Override
    public void destroy() throws ResourceException {
        for (StripeConnection connection : connections) {
            connection.close();
        }
        connections.clear();
    }

    @Override
    public void cleanup() throws ResourceException {
        for (StripeConnection connection : connections) {
            connection.close();
        }
        connections.clear();
    }

    @Override
    public void associateConnection(Object o) throws ResourceException {
        if (o instanceof StripeConnection stripeConnection) {
            connections.add(stripeConnection);
        } else {
            throw new ResourceException("Unsupported connection: " + o);
        }
    }

    @Override
    public void addConnectionEventListener(ConnectionEventListener connectionEventListener) {
        listeners.add(connectionEventListener);
    }

    @Override
    public void removeConnectionEventListener(ConnectionEventListener connectionEventListener) {
        listeners.remove(connectionEventListener);
    }

    @Override
    public XAResource getXAResource() throws ResourceException {
        return null;
    }

    @Override
    public LocalTransaction getLocalTransaction() throws ResourceException {
        return null;
    }

    @Override
    public ManagedConnectionMetaData getMetaData() throws ResourceException {
        return new StripeConnectionMetaData();
    }

    @Override
    public void setLogWriter(PrintWriter printWriter) throws ResourceException {
        this.logWriter = printWriter;
    }

    @Override
    public PrintWriter getLogWriter() throws ResourceException {
        return logWriter;
    }
}
