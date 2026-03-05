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

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Builder;

/**
 * Represents a request to submit an outbound message to the connector.
 *
 * <p>This record encapsulates the data required to stage and transmit
 * a business message, including the business content, optional attachments, backend identifiers,
 * and AS4 protocol metadata.</p>
 *
 * <p>Bean Validation annotations define mandatory fields and are expected
 * to be enforced during request validation.</p>
 *
 * @param businessDomainIdentifier            the identifier of the business domain associated with
 *                                            the message; may be null
 * @param backendMessageIdentifier            the unique identifier of the message in the backend
 *                                            system; must not be blank
 * @param referenceToBackendMessageIdentifier an optional reference to a related backend message
 *                                            identifier (e.g. for replies or correlation purposes);
 *                                            may be null
 * @param businessContent                     the {@link ConnectorOutboundMessageBusinessDocument}
 *                                            containing the primary business businessDocument and
 *                                            related metadata; must not be null
 * @param attachments                         a list of attachment identifiers associated with the
 *                                            message; may be null or empty
 * @param as4Properties                       the {@link ConnectorOutboundMessageAS4Properties}
 *                                            defining the AS4 messaging parameters; must not be
 *                                            null
 */
@Builder(toBuilder = true)
public record ConnectorOutboundMessageRequest(
        String businessDomainIdentifier,
        @NotBlank(message = "The backend identifier must not be blank.")
        String backendMessageIdentifier,
        String referenceToBackendMessageIdentifier,
        @NotNull(message = "The business businessDocument must not be null.")
        ConnectorOutboundMessageBusinessDocument businessContent,
        List<String> attachments,
        @NotNull(message = "The as4 properties must not be null")
        ConnectorOutboundMessageAS4Properties as4Properties
) {
}
