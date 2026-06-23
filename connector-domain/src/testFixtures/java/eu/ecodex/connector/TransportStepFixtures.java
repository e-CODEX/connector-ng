/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector;

import eu.ecodex.connector.domain.model.message.transport.ConnectorMessageTransportStatus;
import eu.ecodex.connector.domain.model.message.transport.ConnectorMessageTransportStep;
import eu.ecodex.connector.domain.model.message.transport.ConnectorMessageTransportStepStatus;
import java.time.Instant;
import java.util.HashSet;

@SuppressWarnings({"MissingJavadocType", "MissingJavadocMethod", "LineLength"})
public class TransportStepFixtures {
    private static final String TRANSPORT_STEP_ID = "1c544c2e-7e60-4647-a4b2-c3c89958b2a7@connector.ecodex.eu";

    public static ConnectorMessageTransportStep createTransportStep() {
        var statuses = new HashSet<ConnectorMessageTransportStepStatus>();
        statuses.add(
                ConnectorMessageTransportStepStatus.builder()
                                                   .status(ConnectorMessageTransportStatus.SUBMITTED)
                                                   .createdAt(Instant.now())
                                                   .build()
        );
        return ConnectorMessageTransportStep
                .builder()
                .identifier(TRANSPORT_STEP_ID)
                .transportedMessageIdentifier(
                        MessageTestFixtures.createEvidenceMessage().identifier()
                )
                .remoteSystemIdentifier(
                        MessageTestFixtures.createEvidenceMessage().backendMessageIdentifier()
                )
                .numberOfAttempts(0)
                .status(ConnectorMessageTransportStatus.SUBMITTED)
                .statuses(statuses)
                .linkPartnerName("backend_alice")
                .transportedMessage(MessageTestFixtures.createEvidenceMessage())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }
}
