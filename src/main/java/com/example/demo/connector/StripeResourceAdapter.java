package com.example.demo.connector;

import jakarta.resource.ResourceException;
import jakarta.resource.spi.ActivationSpec;
import jakarta.resource.spi.BootstrapContext;
import jakarta.resource.spi.Connector;
import jakarta.resource.spi.ResourceAdapter;
import jakarta.resource.spi.ResourceAdapterInternalException;
import jakarta.resource.spi.endpoint.MessageEndpointFactory;

import javax.transaction.xa.XAResource;
import java.io.Serial;
import java.io.Serializable;

@Connector(
        displayName = "Stripe Resource Adapter",
        vendorName = "ITMO",
        eisType = "Payment Server",
        version = "1.0"
)
public class StripeResourceAdapter implements ResourceAdapter, Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    @SuppressWarnings("unused")
    private transient BootstrapContext bootstrapContext;

    @Override
    public void start(BootstrapContext bootstrapContext) throws ResourceAdapterInternalException {
        this.bootstrapContext = bootstrapContext;
    }

    @Override
    public void stop() {

    }

    @Override
    public void endpointActivation(MessageEndpointFactory messageEndpointFactory, ActivationSpec activationSpec) throws ResourceException {

    }

    @Override
    public void endpointDeactivation(MessageEndpointFactory messageEndpointFactory, ActivationSpec activationSpec) {

    }

    @Override
    public XAResource[] getXAResources(ActivationSpec[] activationSpecs) throws ResourceException {
        return new XAResource[0];
    }
}
