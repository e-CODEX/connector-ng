/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.port.api.message.inbound;

import eu.ecodex.connector.domain.model.businessdomain.ConnectorBusinessDomainIdentifier;
import eu.ecodex.connector.domain.model.message.ConnectorMessageAS4Properties;
import eu.ecodex.connector.domain.model.message.evidence.ConnectorMessageEvidence;
import jakarta.annotation.Nonnull;
import java.util.List;
import lombok.Builder;

/**
 * Represents a record for sending outbound business messages through the connector system.
 *
 * @param businessDomainIdentifier Represents the unique identifier of the business domain
 *                                 associated with this message.
 * @param gatewayName              Represents the name of the gateway system responsible for this
 *                                 message. This field is required and cannot be null.
 * @param as4Properties            Encapsulates the AS4-specific properties related to the message,
 *                                 including sender, recipient, and routing details. This field is
 *                                 mandatory.
 * @param transportedEvidences     A list of evidences that are transported with the message.
 */
@Builder
public record ConnectorInboundEvidenceMessageCommand(
    @Nonnull ConnectorBusinessDomainIdentifier businessDomainIdentifier,
    @Nonnull String gatewayName,
    @Nonnull ConnectorMessageAS4Properties as4Properties,
    @Nonnull List<ConnectorMessageEvidence> transportedEvidences
) {
    /**
     * Constructor for the ConnectorInboundEvidenceMessageCommand class.
     */
    public ConnectorInboundEvidenceMessageCommand {
        if (transportedEvidences.isEmpty()) {
            throw new IllegalStateException("transportedEvidences must not be empty");
        }
    }
}
