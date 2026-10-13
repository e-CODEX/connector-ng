/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.port.spi.message;

import eu.ecodex.connector.domain.model.message.ConnectorMessageError;
import jakarta.annotation.Nonnull;
import java.util.List;

/**
 * Repository (port) for persisting the errors associated with connector messages.
 */
public interface ConnectorMessageErrorRepository {
    /**
     * Persists the given errors for the message identified by the given identifier.
     *
     * @param messageIdentifier the unique identifier of the message the errors belong to (must
     *                          not be null)
     * @param errors            the {@link ConnectorMessageError} objects to persist (must not be
     *                          null)
     *
     * @return the {@link ConnectorMessageError} objects that were saved, including any values
     *     generated during persistence
     */
    List<ConnectorMessageError> save(
        @Nonnull String messageIdentifier,
        @Nonnull List<ConnectorMessageError> errors);
}
