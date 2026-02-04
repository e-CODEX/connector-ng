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

import eu.ecodex.connector.domain.model.message.ConnectorMessageAttachment;
import jakarta.annotation.Nonnull;

/**
 * Service interface for managing {@link ConnectorMessageAttachment} entities.
 *
 * <p>This interface provides functionality for handling attachments associated with messages.
 * These
 * attachments are typically documents, such as PDFs, that go with a message in the system.
 */
public interface ConnectorMessageAttachmentService {
    /**
     * Registers a new {@link ConnectorMessageAttachment} into the system.
     *
     * @param attachment the {@link ConnectorMessageAttachment} to be registered. Must not be null.
     *
     * @return the registered {@link ConnectorMessageAttachment} instance, typically enriched with
     *         system-generated values such as identifiers.
     */
    ConnectorMessageAttachment register(@Nonnull ConnectorMessageAttachment attachment);
}
