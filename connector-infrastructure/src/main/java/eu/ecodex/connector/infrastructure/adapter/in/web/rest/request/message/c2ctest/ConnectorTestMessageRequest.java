/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.adapter.in.web.rest.request.message.c2ctest;

import eu.ecodex.connector.infrastructure.adapter.in.web.rest.request.message.outbound.ConnectorOutboundMessageBusinessContent;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Builder;

/**
 * Represents a request to submit an outbound test message to the connector.
 *
 * <p>This record encapsulates the data required to stage and transmit a test business message,
 * including the business content, optional attachments, backend identifiers and AS4 protocol
 * metadata.
 *
 * <p>Bean Validation annotations define the mandatory fields and are expected to be enforced
 * during request validation.
 *
 * @param businessDomainIdentifier the identifier of the business domain associated with the message
 *                                 (must not be blank)
 * @param backendMessageIdentifier the unique identifier of the message in the backend system (must
 *                                 not be blank)
 * @param businessContent          the {@link ConnectorOutboundMessageBusinessContent} containing
 *                                 the primary business document and related metadata (must not be
 *                                 null)
 * @param as4Properties            the {@link ConnectorTestMessageAS4Properties} defining the AS4
 *                                 messaging parameters (must not be null, and validated
 *                                 recursively)
 * @param attachments              the identifiers of the attachments associated with the message;
 *                                 may be {@code null} or empty
 */
@Builder(toBuilder = true)
public record ConnectorTestMessageRequest(
    @NotBlank(message = "The [businessDomainIdentifier] must not be blank.")
    String businessDomainIdentifier,

    @NotBlank(message = "The [backendMessageIdentifier] must not be blank.")
    @Size(max = 255, message = "The [backendMessageIdentifier] must not exceed 255")
    String backendMessageIdentifier,

    @Valid
    @NotNull(message = "The [businessContent] must not be null.")
    ConnectorOutboundMessageBusinessContent businessContent,

    @Valid
    @NotNull(message = "The [as4Properties] must not be null")
    ConnectorTestMessageAS4Properties as4Properties,
    List<String> attachments
) {
}
