package com.example.demo.connector;

import jakarta.resource.cci.ResourceAdapterMetaData;

public class StripeResourceAdapterMetaData implements ResourceAdapterMetaData {
    @Override
    public String getAdapterVersion() {
        return "1";
    }

    @Override
    public String getAdapterVendorName() {
        return "ITMO";
    }

    @Override
    public String getAdapterName() {
        return "Stripe Connector";
    }

    @Override
    public String getAdapterShortDescription() {
        return "Connector to payment service";
    }

    @Override
    public String getSpecVersion() {
        return "1.7";
    }

    @Override
    public String[] getInteractionSpecsSupported() {
        return new String[]{"com.example.demo.connector.StripeInteractionSpec"};
    }

    @Override
    public boolean supportsExecuteWithInputAndOutputRecord() {
        return true;
    }

    @Override
    public boolean supportsExecuteWithInputRecordOnly() {
        return true;
    }

    @Override
    public boolean supportsLocalTransactionDemarcation() {
        return false;
    }
}
