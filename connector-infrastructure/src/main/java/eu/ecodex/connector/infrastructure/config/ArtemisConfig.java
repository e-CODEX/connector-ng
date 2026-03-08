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

import eu.ecodex.connector.infrastructure.property.ArtemisAddressSettingsProperties;
import eu.ecodex.connector.infrastructure.property.ConnectorQueueProperties;
import lombok.NonNull;
import org.apache.activemq.artemis.api.core.RoutingType;
import org.apache.activemq.artemis.api.core.SimpleString;
import org.apache.activemq.artemis.core.settings.impl.AddressSettings;
import org.springframework.boot.artemis.autoconfigure.ArtemisConfigurationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * ArtemisConfig class for configuring the artemis broker messages addresses.
 */
@Configuration
@SuppressWarnings("checkstyle:MissingJavadocMethod")
public class ArtemisConfig {
    @Bean
    public ArtemisConfigurationCustomizer artemisCustomizer(
            ArtemisAddressSettingsProperties settingsProperties,
            ConnectorQueueProperties queueProperties) {
        return configuration -> {
            addQueueWithDLQ(
                    configuration,
                    queueProperties.getOutboundMessageStagingQueue(),
                    settingsProperties
            );
            addQueueWithDLQ(
                    configuration,
                    queueProperties.getOutboundMessageProcessingQueue(),
                    settingsProperties
            );
            // set DLQ addresses configs
            configuration.addAddressSetting("DLQ.#", defaultDLQAddressSettings());
        };
    }

    private void addQueueWithDLQ(
            @NonNull org.apache.activemq.artemis.core.config.Configuration config,
            String queueName, ArtemisAddressSettingsProperties settingsProperties) {
        var addressSettings = defaultAddressSettings(settingsProperties);

        String dlqName = "DLQ." + queueName;
        addressSettings.setDeadLetterAddress(SimpleString.of(dlqName));

        config.addAddressSetting(queueName, addressSettings);
    }

    private AddressSettings defaultAddressSettings(
            ArtemisAddressSettingsProperties settingsProperties) {
        var settings = new AddressSettings();
        settings.setAutoCreateQueues(true);
        settings.setDefaultAddressRoutingType(RoutingType.ANYCAST);
        settings.setAutoCreateDeadLetterResources(true);
        settings.setAutoCreateAddresses(true);
        settings.setAutoCreateExpiryResources(true);
        settings.setAutoDeleteQueues(false);
        settings.setAutoDeleteAddresses(false);
        settings.setDeadLetterAddress(SimpleString.of("DLQ"));
        settings.setExpiryAddress(SimpleString.of("ExpiryQueue"));
        settings.setMaxDeliveryAttempts(settingsProperties.getMaxDeliveryAttempts());
        settings.setRedeliveryDelay(settingsProperties.getRedeliveryDelay());
        settings.setRedeliveryMultiplier(settingsProperties.getRedeliveryMultiplier());

        return settings;
    }

    private AddressSettings defaultDLQAddressSettings() {
        var settings = new AddressSettings();
        settings.setMaxDeliveryAttempts(-1);
        settings.setRedeliveryDelay(0);
        settings.setExpiryAddress(null);

        return settings;
    }
}
