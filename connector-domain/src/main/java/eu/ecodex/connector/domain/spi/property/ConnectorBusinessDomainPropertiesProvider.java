/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.domain.spi.property;

import eu.ecodex.connector.domain.model.property.ConnectorBusinessDomainProperties;

/**
 * Provides access to the configuration properties associated with the business domain in the
 * connector system.
 *
 * <p>Implementors of this interface, such as configuration classes, enable externalized
 * configuration of the business domain, allowing dynamic customization of business domain settings
 * during deployment or runtime.
 */
public interface ConnectorBusinessDomainPropertiesProvider {
    /**
     * Retrieves the configuration properties associated with the business domain in the connector
     * system.
     *
     * <p>The returned properties define the metadata and configuration options of the business
     * domain. These properties enable externalized configuration and dynamic customization of
     * business domain settings.
     *
     * @return an instance of {@code ConnectorBusinessDomainProperties} containing the business
     *         domain configuration details.
     */
    ConnectorBusinessDomainProperties getProperties();
}
