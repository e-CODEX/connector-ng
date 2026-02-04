/*
 * Copyright 2025 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.domain.model.link.configuration;

import lombok.Builder;

/**
 * Encapsulates the configuration properties for a connector link.
 *
 * <p>This record stores the essential details required to secure and manage
 * a connector link, including keystores, private key information, and logging configuration.
 * It provides a structured approach to define security-related and operational aspects of the link.
 *
 * @param keyStore       The keystore configuration used for securing the connector link.
 * @param privateKey     The private key configuration for the connector link.
 * @param trustStore     The trust store configuration used for validating certificates.
 * @param loggingEnabled A flag indicating whether logging is enabled for the connector link.
 */
@Builder
public record ConnectorLinkConfigurationProperties(
        ConnectorLinkConfigurationPropertiesKeystore keyStore,
        ConnectorLinkConfigurationPropertiesPrivateKey privateKey,
        ConnectorLinkConfigurationPropertiesKeystore trustStore,
        boolean loggingEnabled
) {
}
