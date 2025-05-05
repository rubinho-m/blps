package com.example.demo.services;

import com.example.demo.model.PaidSubscriptionEvent;

public interface KafkaProducerService {
    void sendEvent(PaidSubscriptionEvent paidSubscriptionEvent);
}
