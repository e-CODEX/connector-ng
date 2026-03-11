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

import eu.ecodex.connector.infrastructure.property.ActiveMQAddressSettingsProperties;
import eu.ecodex.connector.infrastructure.property.ConnectorQueueProperties;
import org.apache.activemq.RedeliveryPolicy;
import org.apache.activemq.broker.region.policy.RedeliveryPolicyMap;
import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.boot.activemq.autoconfigure.ActiveMQConnectionFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * ActiveMQConfig class for configuring the ActiveMQ connection factory.
 */
@Configuration
@SuppressWarnings("checkstyle:MissingJavadocMethod")
public class ActiveMQConfig {
    @Bean
    public ActiveMQConnectionFactoryCustomizer connectionFactoryCustomizer(
            ActiveMQAddressSettingsProperties settingsProperties,
            ConnectorQueueProperties queueProperties) {

        return factory -> {
            factory.setRedeliveryPolicy(defaultRedeliveryPolicy(settingsProperties));

            var policyMap = new RedeliveryPolicyMap();

            addQueue(
                    policyMap, queueProperties.getOutboundMessageStagingQueue(),
                    settingsProperties
            );
            addQueue(
                    policyMap, queueProperties.getOutboundMessageProcessingQueue(),
                    settingsProperties
            );

            factory.setRedeliveryPolicyMap(policyMap);
        };
    }

    private void addQueue(
            RedeliveryPolicyMap policyMap,
            String queueName,
            ActiveMQAddressSettingsProperties settingsProperties) {
        policyMap.put(new ActiveMQQueue(queueName), defaultRedeliveryPolicy(settingsProperties));
    }

    private RedeliveryPolicy defaultRedeliveryPolicy(
            ActiveMQAddressSettingsProperties settingsProperties) {
        var policy = new RedeliveryPolicy();
        policy.setMaximumRedeliveries(settingsProperties.getMaxDeliveryAttempts());
        policy.setInitialRedeliveryDelay(settingsProperties.getRedeliveryDelay());
        policy.setBackOffMultiplier(settingsProperties.getRedeliveryMultiplier());
        policy.setUseExponentialBackOff(settingsProperties.getRedeliveryMultiplier() > 1.0);

        return policy;
    }
}
