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
 * Represents the configuration of a keystore for a connector link.
 *
 * <p>This record encapsulates the essential properties required to define and manage a
 * keystore, including its file system path and the associated password for access. It is primarily
 * used to enforce security measures within the connector system by securing sensitive data
 * exchanges.
 *
 * @param path     The file system path to the keystore.
 * @param password The password used to access the keystore.
 */
@Builder
public record ConnectorLinkConfigurationPropertiesKeystore(
        String path,
        String password
) {
}
