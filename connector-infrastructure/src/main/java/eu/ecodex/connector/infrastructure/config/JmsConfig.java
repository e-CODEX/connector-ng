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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.JacksonJsonMessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;

/**
 * JmsConfig class for configuring the messaging service.
 */
@EnableJms
@Configuration
@SuppressWarnings("checkstyle:MissingJavadocMethod")
public class JmsConfig {
    @Bean
    public DefaultJmsListenerContainerFactory jmsListenerContainerFactory(
            @Qualifier("jmsConnectionFactory") ConnectionFactory connectionFactory) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setPubSubDomain(false);
        factory.setMessageConverter(jacksonConverter());
        return factory;
    }

    @Bean
    public JmsTemplate jmsTemplate(
            @Qualifier("jmsConnectionFactory") ConnectionFactory connectionFactory) {
        JmsTemplate template = new JmsTemplate(connectionFactory);
        template.setMessageConverter(jacksonConverter());
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
