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
 * Represents a contract for retrieving a paginated list of connector messages. The implementers of
 * this interface are responsible for defining the logic to retrieve the messages based on the given
 * pagination request.
 */
public interface ConnectorListMessages {
    /**
     * Executes a paginated process to retrieve a list of connector messages based on the specified
     * page request.
     *
     * @param filter the pagination parameters and filter criteria used to retrieve the messages
     *               (must not be null)
     *
     * @return a {@link ConnectorPageResult} containing a list of {@link ConnectorBusinessMessage}
     *     objects and pagination metadata.
     */
    ConnectorPageResult<ConnectorBusinessMessage> execute(
        @Nonnull ConnectorMessagesListFilter filter);
}
