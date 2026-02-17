/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.property;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration properties for the default artemis address settings.
 */
@Getter
@Configuration
@ConfigurationProperties(prefix = "connector.artemis.address-settings")
public class ArtemisAddressSettingsProperties {
    private final int maxDeliveryAttempts = 5;
    private final int redeliveryDelay = 60000; // set 60s redelivery delay
    private final int redeliveryMultiplier = 2;
}
