package com.example.demo.connector;

import jakarta.resource.cci.InteractionSpec;

public record StripeInteractionSpec(StripeFunction stripeFunction) implements InteractionSpec {
}
