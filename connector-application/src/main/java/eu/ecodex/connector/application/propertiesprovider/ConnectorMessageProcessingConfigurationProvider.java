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

import eu.ecodex.connector.application.port.api.message.ConnectorMessageIdGenerator;
import eu.ecodex.connector.domain.model.ProcessingModeVerificationMode;

/**
 * Provider (port) giving access to the {@link ConnectorMessageProcessingConfiguration} used for
 * message processing within the connector.
 *
 * <p>Implementations supply the current processing configuration. It may be static, externally
 * configured (for example through properties or a database), or resolved dynamically at runtime.
 *
 * <p>This abstraction keeps the message processing components decoupled from the underlying
 * configuration source.
 */
public interface ConnectorMessageProcessingConfigurationProvider {
    /**
     * Returns the current configuration for message processing within the connector.
     *
     * <p>The configuration encapsulates the properties that affect the behavior of the message
     * processing logic. It may include settings for evidence handling, message ID generation (see
     * {@link ConnectorMessageIdGenerator}) and message verification modes (see
     * {@link ProcessingModeVerificationMode}), among others.
     *
     * @return the current {@link ConnectorMessageProcessingConfiguration}; never {@code null}
     */
    ConnectorMessageProcessingConfiguration getConfiguration();
}
