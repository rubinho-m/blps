package com.example.demo.connector;

import jakarta.resource.ResourceException;
import jakarta.resource.cci.IndexedRecord;
import jakarta.resource.cci.MappedRecord;
import jakarta.resource.cci.RecordFactory;

public class StripeRecordFactory implements RecordFactory {
    @Override
    public MappedRecord createMappedRecord(String recordName) {
        return new StripeMappedRecord(recordName);
    }

    @Override
    public IndexedRecord createIndexedRecord(String s) throws ResourceException {
        throw new ResourceException("IndexedRecord not supported by Stripe connector");
    }
}
