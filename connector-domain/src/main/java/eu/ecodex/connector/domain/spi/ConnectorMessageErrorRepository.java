/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.domain.spi;

import eu.ecodex.connector.domain.model.message.ConnectorMessageError;
import jakarta.annotation.Nonnull;
import java.util.List;

/**
 * Repository interface for managing and persisting {@link ConnectorMessageError} entities.
 *
 * <p>Provides methods to handle error information associated with connector messages. This
 * interface serves as a contract for implementing persistence and retrieval logic for
 * {@link ConnectorMessageError} objects, enabling the tracking and storage of errors tied to a
 * specific message.
 */
public interface ConnectorMessageErrorRepository {
    /**
     * Persists the provided list of {@link ConnectorMessageError} entities associated with a
     * specific message identifier.
     *
     * @param messageIdentifier the unique identifier of the message for which errors are being
     *                          saved; must not be null.
     * @param errors            the list of {@link ConnectorMessageError} entities to be persisted;
     *                          must not be null.
     *
     * @return the list of {@link ConnectorMessageError} entities that were successfully saved.
     */
    List<ConnectorMessageError> save(
            @Nonnull String messageIdentifier,
            @Nonnull List<ConnectorMessageError> errors);
}
