/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.property.c2ctest;

import eu.ecodex.connector.domain.ConnectorDefaults;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration properties for connector-to-connector test messages.
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "connector.c2ctests")
public class Connector2ConnectorTestMessageProperties {
    boolean enabled = true;
    String action = ConnectorDefaults.DEFAULT_TEST_ACTION_NAME;
    TestServiceProperties service = new TestServiceProperties();
}
