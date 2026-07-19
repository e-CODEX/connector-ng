/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.propertiesprovider;

/**
 * Provides access to the {@link ConnectorMessageProcessingConfiguration} used for message
 * processing within the connector.
 *
 * <p>Implementations of this interface are responsible for supplying the current processing
 * configuration. The configuration may be static, externally configured (e.g. via properties or
 * database), or dynamically resolved at runtime.
 *
 * <p>This abstraction allows message processing components to remain decoupled from the underlying
 * configuration source.
 */
public interface ConnectorMessageProcessingConfigurationProvider {
    /**
     * Retrieves the current configuration for message processing within the connector.
     *
     * <p>This method provides access to a {@link ConnectorMessageProcessingConfiguration} instance
     * that encapsulates the configurable properties and parameters affecting the behaviour of
     * message processing logic. The configuration may include settings for evidence handling,
     * message ID generation, and message verification modes, among others.
     *
     * @return an instance of {@link ConnectorMessageProcessingConfiguration} representing the
     *     current message processing configuration
     */
    ConnectorMessageProcessingConfiguration getConfiguration();
}
