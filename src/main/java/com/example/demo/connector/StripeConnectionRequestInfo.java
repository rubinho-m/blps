package com.example.demo.connector;

import jakarta.resource.spi.ConnectionRequestInfo;

public record StripeConnectionRequestInfo(String apiKey) implements ConnectionRequestInfo {
}
