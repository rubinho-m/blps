package com.example.demo.config;

import com.example.demo.model.PaidSubscriptionEvent;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class ContextClosedListener {
    private final KafkaProducer<String, PaidSubscriptionEvent> kafkaProducer;

    @Autowired
    public ContextClosedListener(KafkaProducer<String, PaidSubscriptionEvent> kafkaProducer) {
        this.kafkaProducer = kafkaProducer;
    }

    @EventListener
    public void handleContextClosed(ContextClosedEvent event) {
        kafkaProducer.close();
    }
}