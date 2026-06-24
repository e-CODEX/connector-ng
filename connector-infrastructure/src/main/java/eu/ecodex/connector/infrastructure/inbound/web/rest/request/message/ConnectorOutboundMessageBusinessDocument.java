/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.inbound.web.rest.request.message;

import eu.ecodex.connector.domain.model.message.content.ConnectorBusinessDocumentAESType;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.springframework.web.multipart.MultipartFile;

/**
 * Represents a request for attaching a business document to an outbound message.
 *
 * @param document          the business document to attach; must not be {@code null}
 * @param detachedSignature optional detached signature information for the business document; may
 *                          be {@code null} if no signature is provided
 * @param aesType           the AES encryption type to apply to the business document; may be
 *                          {@code null} if encryption is not required or determined elsewhere
 */
@Builder(toBuilder = true)
@SuppressWarnings("checkstyle:LineLength")
public record ConnectorOutboundMessageBusinessDocument(
        @NotNull(message = "The business business document must not be null.")
        MultipartFile document,
        ConnectorOutboundMessageDetachedSignature detachedSignature,
        ConnectorBusinessDocumentAESType aesType
) {
}
