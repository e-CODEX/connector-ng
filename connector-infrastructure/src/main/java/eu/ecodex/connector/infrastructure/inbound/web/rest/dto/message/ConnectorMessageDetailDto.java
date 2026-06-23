/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.inbound.web.rest.dto.message;

import eu.ecodex.connector.domain.model.message.ConnectorMessageAS4Properties;
import eu.ecodex.connector.domain.model.message.ConnectorMessageDirection;
import eu.ecodex.connector.domain.model.message.ConnectorMessageError;
import eu.ecodex.connector.domain.model.message.attachment.ConnectorMessageAttachment;
import java.time.Instant;
import java.util.List;
import lombok.Builder;

/**
 * Detailed representation of a connector message exchanged between a backend system and a gateway.
 *
 * @param businessDomainIdentifier            identifier of the business domain to which the message
 *                                            belongs
 * @param identifier                          unique identifier of the connector message
 * @param backendMessageIdentifier            identifier assigned to the message by the backend
 *                                            system
 * @param referenceToBackendMessageIdentifier reference to another backend message identifier,
 *                                            typically used for replies, acknowledgements, or
 *                                            correlations
 * @param backendName                         name of the backend system associated with the
 *                                            message
 * @param gatewayName                         name of the gateway responsible for transmitting or
 *                                            receiving the message
 * @param as4Properties                       AS4-specific metadata and properties associated with
 *                                            the message
 * @param direction                           direction of the message flow (e.g., inbound or
 *                                            outbound)
 * @param isBusiness                          indicates whether the message is a business message
 * @param createdAt                           timestamp when the message was created
 * @param updatedAt                           timestamp when the message was last updated
 * @param deletedAt                           timestamp when the message was deleted
 * @param rejectedAt                          timestamp when the message was rejected
 * @param confirmedAt                         timestamp when the message was confirmed
 * @param deliveredToGatewayAt                timestamp when the message was delivered to the
 *                                            gateway
 * @param deliveredToBackendAt                timestamp when the message was delivered to the
 *                                            backend system
 * @param attachments                         list of attachments associated with the message
 * @param evidences                           list of evidences related to message processing or
 *                                            delivery
 * @param errors                              list of processing or delivery errors associated with
 *                                            the message
 */
@Builder
public record ConnectorMessageDetailDto(
        String businessDomainIdentifier,
        String identifier,
        String backendMessageIdentifier,
        String referenceToBackendMessageIdentifier,
        String backendName,
        String gatewayName,
        ConnectorMessageAS4Properties as4Properties,
        ConnectorMessageDirection direction,
        boolean isBusiness,
        Instant createdAt,
        Instant updatedAt,
        Instant deletedAt,
        Instant rejectedAt,
        Instant confirmedAt,
        Instant deliveredToGatewayAt,
        Instant deliveredToBackendAt,
        List<ConnectorMessageAttachment> attachments,
        List<ConnectorMessageEvidenceDto> evidences,
        List<ConnectorMessageError> errors
) {
}
