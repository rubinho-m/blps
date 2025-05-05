package com.example.demo.services.impl;

import com.example.demo.model.PaidSubscriptionEvent;
import com.example.demo.services.KafkaProducerService;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerServiceImpl implements KafkaProducerService {
    private final KafkaProducer<String, PaidSubscriptionEvent> kafkaProducer;

    @Value("${kafka.topic}")
    private String topic;


    @Autowired
    public KafkaProducerServiceImpl(KafkaProducer<String, PaidSubscriptionEvent> kafkaProducer) {
        this.kafkaProducer = kafkaProducer;
    }

    @Override
    public void sendEvent(PaidSubscriptionEvent paidSubscriptionEvent) {
        kafkaProducer.send(new ProducerRecord<>(topic, paidSubscriptionEvent));
    }
}
