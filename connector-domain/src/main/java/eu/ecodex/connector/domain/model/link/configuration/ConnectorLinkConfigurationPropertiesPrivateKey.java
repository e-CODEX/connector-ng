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
 * Represents the private key configuration used in a connector link.
 *
 * <p>This record stores the essential details required to identify and protect a private key,
 * including the alias and the password used to secure the private key. This configuration is
 * critical for establishing secure communications and maintaining the integrity of sensitive
 * operations within the connector system.
 *
 * @param alias    The alias identifying the specific private key in the keystore.
 * @param password The password used to secure the private key.
 */
@Builder
public record ConnectorLinkConfigurationPropertiesPrivateKey(
        String alias,
        String password
) {
}
