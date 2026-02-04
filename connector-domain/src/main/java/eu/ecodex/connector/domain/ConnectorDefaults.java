/*
 * Copyright 2025 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.domain;

/**
 * The ConnectorDefaults class provides default constants used within the connector configuration.
 *
 * <p>This class contains predefined values that serve as default settings for various components
 * of the system, ensuring a consistent configuration when other custom values are not provided.
 *
 * <p>The constants defined in this class are immutable and globally accessible. They are primarily
 * intended for internal use by the connector and related components.
 *
 * <p>Key characteristics:
 * <ul>
 *     <li>Provides predefined default values.</li>
 *     <li>Facilitates standardized configuration across components.</li>
 * </ul>
 */
public class ConnectorDefaults {
    public static final String DEFAULT_BACKEND_NAME = "default_backend";
    public static final String DEFAULT_GATEWAY_NAME = "default_gateway";
}
