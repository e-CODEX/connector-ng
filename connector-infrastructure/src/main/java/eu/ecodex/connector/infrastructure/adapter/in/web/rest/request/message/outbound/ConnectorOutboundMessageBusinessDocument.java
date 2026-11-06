/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.adapter.in.web.rest.request.message.outbound;

import static lombok.AccessLevel.PRIVATE;

import eu.ecodex.connector.domain.model.message.content.ConnectorBusinessDocumentAESType;
import eu.ecodex.connector.infrastructure.adapter.in.web.rest.validator.constraints.NotEmptyFile;
import jakarta.validation.Valid;
import java.io.Serializable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

/**
 * Represents a request for attaching a business document to an outbound message.
 *
 * @param document          the business document to attach (must not be null or empty)
 * @param detachedSignature optional detached signature information for the business document; may
 *                          be {@code null} if no signature is provided
 * @param aesType           the AES encryption type ({@link ConnectorBusinessDocumentAESType}) to
 *                          apply to the business document; may be {@code null} if encryption is not
 *                          required or determined elsewhere
 */
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = PRIVATE)
public class ConnectorOutboundMessageBusinessDocument implements Serializable {
    @NotEmptyFile(message = "The [document] file must not be empty.")
    MultipartFile document;

    @Valid
    ConnectorOutboundMessageDetachedSignature detachedSignature;

    ConnectorBusinessDocumentAESType aesType;
}
