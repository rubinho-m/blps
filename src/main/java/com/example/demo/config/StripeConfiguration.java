package com.example.demo.config;

import com.example.demo.connector.StripeConnection;
import com.example.demo.connector.StripeConnectionFactory;
import com.example.demo.connector.StripeManagedConnectionFactory;
import com.example.demo.connector.StripeResourceAdapter;
import jakarta.resource.ResourceException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jca.support.ResourceAdapterFactoryBean;

@Configuration
public class StripeConfiguration {
    @Value("${stripe.api}")
    private String apiKey;

    @Bean
    public ResourceAdapterFactoryBean resourceAdapter() {
        ResourceAdapterFactoryBean bean = new ResourceAdapterFactoryBean();
        bean.setResourceAdapter(new StripeResourceAdapter());
        return bean;
    }

    @Bean
    @Primary
    public StripeManagedConnectionFactory stripeManagedConnectionFactory() {
        return new StripeManagedConnectionFactory(apiKey);
    }

    @Bean
    public StripeConnectionFactory stripeConnectionFactory(StripeManagedConnectionFactory stripeManagedConnectionFactory) {
        return new StripeConnectionFactory(stripeManagedConnectionFactory, null);
    }

    @Bean
    public StripeConnection stripeConnection(StripeConnectionFactory stripeConnectionFactory) throws ResourceException {
        return (StripeConnection) stripeConnectionFactory.getConnection();
    }
}
