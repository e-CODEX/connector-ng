/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.domain.api.service;

import eu.ecodex.connector.domain.model.message.content.ConnectorMessageContent;
import jakarta.annotation.Nonnull;

/**
 * Interface defining operations for managing message content in the connector domain.
 *
 * <p>This service provides an abstraction for handling and processing message content that is
 * exchanged or used within the connector system. It facilitates interaction with underlying
 * components or repositories to support seamless operations relevant to message data.
 *
 * <p>The primary focus of this interface is to centralize content-related logic and enable
 * consistent handling of message content across the connector ecosystem.
 */
public interface ConnectorMessageContentService {
    /**
     * Registers the provided message content within the connector system.
     *
     * @param content the {@link ConnectorMessageContent} to be registered. Must not be
     *                {@code null}.
     *
     * @return the registered {@link ConnectorMessageContent} instance, potentially enriched or
     *         modified during the registration process.
     */
    ConnectorMessageContent register(@Nonnull ConnectorMessageContent content);
}
