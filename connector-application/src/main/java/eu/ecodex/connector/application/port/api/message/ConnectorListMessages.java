/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.port.api.message;

import eu.ecodex.connector.domain.model.filter.ConnectorMessagesListFilter;
import eu.ecodex.connector.domain.model.message.ConnectorBusinessMessage;
import eu.ecodex.connector.domain.model.paging.ConnectorPageResult;
import jakarta.annotation.Nonnull;

/**
 * Use case for listing the connector business messages.
 */
public interface ConnectorListMessages {
    /**
     * Retrieves a page of {@link ConnectorBusinessMessage} objects based on the pagination
     * parameters and the optional filter criteria carried by the provided
     * {@link ConnectorMessagesListFilter}. All filters are combined with a logical
     * {@code AND}; a filter whose value is {@code null} or empty is ignored.
     *
     * @param filter the pagination parameters and filter criteria used to retrieve the messages
     *               (must not be null)
     *
     * @return a {@link ConnectorPageResult} containing a page of {@link ConnectorBusinessMessage}
     *     objects matching the given filter
     *
     * @throws IllegalArgumentException if the page request contained in {@code filter} is invalid
     */
    ConnectorPageResult<ConnectorBusinessMessage> execute(
        @Nonnull ConnectorMessagesListFilter filter);
}
