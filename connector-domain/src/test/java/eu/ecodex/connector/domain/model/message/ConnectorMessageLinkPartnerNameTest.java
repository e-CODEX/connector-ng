/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.domain.model.message;

import static org.assertj.core.api.Assertions.assertThat;

import eu.ecodex.connector.BusinessMessageTestFixtures;
import eu.ecodex.connector.EvidenceMessageTestFixtures;
import org.junit.jupiter.api.Test;

class ConnectorMessageLinkPartnerNameTest {
    private static final String BACKEND_NAME = "backend-a";
    private static final String GATEWAY_NAME = "gateway-a";

    @Test
    void should_be_the_gateway_for_a_business_message_going_to_the_gateway() {
        var message = BusinessMessageTestFixtures.createOutboundMessage().toBuilder()
            .direction(ConnectorMessageDirection.BACKEND_TO_GATEWAY)
            .backendName(BACKEND_NAME)
            .gatewayName(GATEWAY_NAME)
            .build();

        assertThat(message.linkPartnerName()).isEqualTo(GATEWAY_NAME);
    }

    @Test
    void should_be_the_backend_for_a_business_message_going_to_the_backend() {
        var message = BusinessMessageTestFixtures.createInboundMessage().toBuilder()
            .direction(ConnectorMessageDirection.GATEWAY_TO_BACKEND)
            .backendName(BACKEND_NAME)
            .gatewayName(GATEWAY_NAME)
            .build();

        assertThat(message.linkPartnerName()).isEqualTo(BACKEND_NAME);
    }

    @Test
    void should_be_the_gateway_for_an_evidence_message_going_to_the_gateway() {
        var message = EvidenceMessageTestFixtures.createDeliveryEvidenceMessage().toBuilder()
            .direction(ConnectorMessageDirection.BACKEND_TO_GATEWAY)
            .backendName(BACKEND_NAME)
            .gatewayName(GATEWAY_NAME)
            .build();

        assertThat(message.linkPartnerName()).isEqualTo(GATEWAY_NAME);
    }

    @Test
    void should_be_the_backend_for_an_evidence_message_going_to_the_backend() {
        var message = EvidenceMessageTestFixtures.createDeliveryEvidenceMessage().toBuilder()
            .direction(ConnectorMessageDirection.GATEWAY_TO_BACKEND)
            .backendName(BACKEND_NAME)
            .gatewayName(GATEWAY_NAME)
            .build();

        assertThat(message.linkPartnerName()).isEqualTo(BACKEND_NAME);
    }
}
