package com.finbank.audit.config;

import com.finbank.audit.event.TransactionEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConsumerConfig {
    @Bean
    public ConsumerFactory<String, TransactionEvent> consumerFactory(@Value("${spring.kafka.bootstrap-servers}") String servers) {
        JsonDeserializer<TransactionEvent> deserializer = new JsonDeserializer<>(TransactionEvent.class);
        deserializer.addTrustedPackages("com.finbank.*");
        Map<String,Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, servers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "audit-service");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        return new org.springframework.kafka.core.DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), deserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, TransactionEvent> kafkaListenerContainerFactory(ConsumerFactory<String, TransactionEvent> factory) {
        ConcurrentKafkaListenerContainerFactory<String, TransactionEvent> factoryBean = new ConcurrentKafkaListenerContainerFactory<>();
        factoryBean.setConsumerFactory(factory);
        return factoryBean;
    }
}