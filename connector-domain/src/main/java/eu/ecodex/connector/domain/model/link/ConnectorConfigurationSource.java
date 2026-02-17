/*
 * Copyright 2025 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.domain.model.link;

/**
 * Specifies the source of a link configuration within the connector system.
 *
 * <p>This enum identifies the origin of configuration data for a link and provides
 * three distinct options for categorizing the source:
 * <ul>
 *   <li>DATABASE: Indicates that the configuration is retrieved from a database.</li>
 *   <li>IMPLEMENTATION: Indicates that the configuration is hardcoded or embedded
 *       directly in the implementation.</li>
 *   <li>ENVIRONMENT: Indicates that the configuration is derived from environmental
 *       variables or external runtime context.</li>
 *       <li>APPLICATION: Indicates that the configuration is hardcoded.</li>
 * </ul>
 *
 * <p>It is used to enable clear classification of configuration sources, supporting
 * modularity and flexibility in the handling of link setups.
 */
public enum ConnectorConfigurationSource {
    DATABASE,
    IMPLEMENTATION,
    ENVIRONMENT,
    APPLICATION
}
