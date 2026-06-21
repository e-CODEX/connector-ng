/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.service.message;

import static org.junit.jupiter.api.Assertions.assertThrows;

import eu.ecodex.connector.MessageTestFixtures;
import eu.ecodex.connector.application.service.impl.message.ConnectorMessagePartiesVerifierService;
import eu.ecodex.connector.application.service.usecase.message.ConnectorMessagePartiesVerifier;
import eu.ecodex.connector.domain.exception.ConnectorMessagePartyException;
import eu.ecodex.connector.domain.model.message.ConnectorMessageDirection;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@SuppressWarnings("DataFlowIssue")
@ExtendWith(MockitoExtension.class)
public class ConnectorMessagePartiesVerifierServiceTest {
    private final ConnectorMessagePartiesVerifier partiesVerifierService =
            new ConnectorMessagePartiesVerifierService();

    @Test
    void should_check_outgoing_message_parties_info_successfully() {
        var message = MessageTestFixtures.createOutboundBusinessMessage();
        // no thrown exception mean from and to parties are set correctly
        this.partiesVerifierService.verify(message);
    }

    @Test
    void should_throw_exception_when_outbound_message_from_party_info_is_null() {
        var message = MessageTestFixtures.createNullFromPartyOutboundBusinessMessage();
        assertThrows(
                ConnectorMessagePartyException.class,
                () -> partiesVerifierService.verify(message)
        );
    }

    @Test
    void should_throw_exception_when_outbound_message_to_party_info_is_null() {
        var message = MessageTestFixtures.createNullToPartyOutboundBusinessMessage();
        assertThrows(
                ConnectorMessagePartyException.class,
                () -> partiesVerifierService.verify(message)
        );
    }

    @Test
    void should_throw_exception_when_outbound_message_from_party_info_are_incorrect() {
        var message = MessageTestFixtures.createInvalidFromPartyOutboundBusinessMessage();
        assertThrows(
                ConnectorMessagePartyException.class,
                () -> partiesVerifierService.verify(message)
        );
    }

    @Test
    void should_throw_exception_when_outbound_message_to_party_info_are_incorrect() {
        var message = MessageTestFixtures.createInvalidToPartyOutboundBusinessMessage();
        assertThrows(
                ConnectorMessagePartyException.class,
                () -> partiesVerifierService.verify(message)
        );
    }

    @Test
    void should_throw_exception_when_outbound_message_direction_is_incorrect() {
        var message = MessageTestFixtures.createOutboundBusinessMessage()
                                         .toBuilder()
                                         .direction(ConnectorMessageDirection.GATEWAY_TO_BACKEND)
                                         .build();
        assertThrows(
                UnsupportedOperationException.class,
                () -> partiesVerifierService.verify(message)
        );
    }

    @Test
    void should_throw_null_pointer_exception_when_message_is_null_when_checking_outgoing_message_parties_info() {
        assertThrows(
                NullPointerException.class, () -> partiesVerifierService.verify(null));
    }
}
