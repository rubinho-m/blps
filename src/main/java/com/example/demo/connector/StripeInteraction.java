package com.example.demo.connector;

import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import jakarta.resource.cci.Connection;
import jakarta.resource.cci.Interaction;
import jakarta.resource.cci.InteractionSpec;
import jakarta.resource.cci.Record;
import jakarta.resource.cci.ResourceWarning;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StripeInteraction implements Interaction {
    private final Connection connection;

    public StripeInteraction(Connection connection) {
        this.connection = connection;
    }

    @Override
    public boolean execute(InteractionSpec interactionSpec, Record input, Record output)  {
        if (interactionSpec instanceof StripeInteractionSpec stripeInteractionSpec) {
            final InteractionResponse response = switch (stripeInteractionSpec.stripeFunction()) {
                case CREATE_PAYMENT -> executeInternal(input, "pm_card_visa");
                case CREATE_ERROR_PAYMENT -> executeInternal(input, "pm_card_chargeDeclined");
            };
            if (output instanceof StripeMappedRecord stripeMappedRecord) {
                stripeMappedRecord.setResponse(response.response());
                return response.success();
            }
            return false;
        }
        return false;
    }

    @Override
    public Record execute(InteractionSpec interactionSpec, Record input) {
        final StripeMappedRecord output = new StripeMappedRecord("Stripe response");
        if (interactionSpec instanceof StripeInteractionSpec stripeInteractionSpec) {
            final InteractionResponse response = switch (stripeInteractionSpec.stripeFunction()) {
                case CREATE_PAYMENT -> executeInternal(input, "pm_card_visa");
                case CREATE_ERROR_PAYMENT -> executeInternal(input, "pm_card_chargeDeclined");
            };
            output.setResponse(response.response());
            return output;
        }
        output.setResponse("Error: interactionSpec is not StripeInteractionSpec");
        return output;
    }

    private InteractionResponse executeInternal(Record input, String paymentMethod) {
        if (input instanceof StripeMappedRecord stripeMappedRecord) {
            final String currency = (String) stripeMappedRecord.getParameters().get("currency");
            final Long amount = (Long) stripeMappedRecord.getParameters().get("amount");
            final PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount(amount)
                    .setCurrency(currency)
                    .setPaymentMethod(paymentMethod)
                    .setAutomaticPaymentMethods(
                            PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                    .setEnabled(true)
                                    .setAllowRedirects(
                                            PaymentIntentCreateParams.AutomaticPaymentMethods.AllowRedirects.NEVER
                                    )
                                    .build()
                    )
                    .setConfirm(true)
                    .build();
            try {
                final PaymentIntent paymentIntent = PaymentIntent.create(params);
                LOGGER.info("Успешное списание! ID: {}", paymentIntent.getId());
                return new InteractionResponse("Payment success", true);
            } catch (Exception e) {
                return new InteractionResponse("Произошла ошибка при списании средств", false);
            }
        }
        return new InteractionResponse("Record is not StripeMappedRecord", false);
    }

    private record InteractionResponse(String response, boolean success){}

    @Override
    public Connection getConnection() {
        return connection;
    }


    @Override
    public ResourceWarning getWarnings() {
        return null;
    }

    @Override
    public void clearWarnings() {

    }

    @Override
    public void close() {
    }
}
