package com.example.demo.connector;

import jakarta.resource.cci.ConnectionSpec;

public record StripeConnectionSpec(String apiKey) implements ConnectionSpec {
}
