/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.config;

import jakarta.jms.ConnectionFactory;
import jakarta.jms.XAConnectionFactory;
import org.apache.activemq.ActiveMQXAConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jms.XAConnectionFactoryWrapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.JacksonJsonMessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;
import org.springframework.transaction.jta.JtaTransactionManager;

/**
 * JmsConfig class for configuring the messaging service.
 */
@EnableJms
@Configuration
@SuppressWarnings("checkstyle:MissingJavadocMethod")
public class JmsConfig {
    @Bean
    public XAConnectionFactory xaConnectionFactory(
        @Value("${spring.activemq.broker-url}") String brokerUrl,
        @Value("${spring.activemq.user}") String user,
        @Value("${spring.activemq.password}") String password) {
        var xaConnectionFactory = new ActiveMQXAConnectionFactory();

        xaConnectionFactory.setBrokerURL(brokerUrl);
        xaConnectionFactory.setUserName(user);
        xaConnectionFactory.setPassword(password);
        xaConnectionFactory.setTrustAllPackages(true);

        return xaConnectionFactory;
    }

    @Bean
    @Primary
    public ConnectionFactory connectionFactory(
        XAConnectionFactory xaConnectionFactory,
        XAConnectionFactoryWrapper wrapper) throws Exception {
        return wrapper.wrapConnectionFactory(xaConnectionFactory);
    }

    @Bean
    public DefaultJmsListenerContainerFactory jmsListenerContainerFactory(
        ConnectionFactory connectionFactory,
        JtaTransactionManager transactionManager,
        MessageConverter messageConverter) {
        var factory = new DefaultJmsListenerContainerFactory();

        factory.setConnectionFactory(connectionFactory);
        factory.setTransactionManager(transactionManager);
        factory.setSessionTransacted(false);
        factory.setMessageConverter(messageConverter);

        return factory;
    }

    @Bean
    public JmsTemplate jmsTemplate(
        ConnectionFactory connectionFactory, MessageConverter messageConverter) {
        var template = new JmsTemplate(connectionFactory);
        template.setMessageConverter(messageConverter);
        template.setPubSubDomain(false);

        return template;
    }

    @Bean
    public MessageConverter jacksonConverter() {
        var converter = new JacksonJsonMessageConverter();
        converter.setTargetType(MessageType.TEXT);
        converter.setTypeIdPropertyName("_type");
        return converter;
    }
}
