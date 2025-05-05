package com.example.demo.config;

import com.example.demo.model.PaidSubscriptionEvent;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
public class KafkaConfiguration {
    @Value(value = "${kafka.bootstrap-servers}")
    private String bootstrapAddress;

    @Bean
    public KafkaProducer<String, PaidSubscriptionEvent> kafkaProducer() {
        Properties props = new Properties();
        props.put("bootstrap.servers", bootstrapAddress);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "com.example.demo.serialization.JsonSerializer");

        return new KafkaProducer<>(props);
    }
}
