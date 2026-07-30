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

import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertThrows;

import eu.ecodex.connector.MessageTestFixtures;
import eu.ecodex.connector.application.exception.ConnectorMessagePartyException;
import eu.ecodex.connector.application.port.api.message.ConnectorMessagePartiesVerifier;
import eu.ecodex.connector.domain.model.message.ConnectorMessageDirection;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@SuppressWarnings("DataFlowIssue")
@ExtendWith(MockitoExtension.class)
@DisplayName("ConnectorMessagePartiesVerifierService")
public class ConnectorMessagePartiesVerifierServiceTest {
    private final ConnectorMessagePartiesVerifier partiesVerifierService =
        new ConnectorMessagePartiesVerifierService();

    @Nested
    @DisplayName("when the outbound message is valid")
    class WhenValid {
        @Test
        void should_verify_the_message_without_error() {
            var message = MessageTestFixtures.createOutboundBusinessMessage();
            // no exception means the from and to parties are set correctly
            assertThatCode(() -> partiesVerifierService.verify(message))
                .doesNotThrowAnyException();
        }
    }

    @Nested
    @DisplayName("when the message parties are invalid")
    class WhenPartiesAreInvalid {
        @Test
        void should_fail_when_the_from_party_is_null() {
            var message = MessageTestFixtures.createNullFromPartyOutboundBusinessMessage();

            assertThrows(
                ConnectorMessagePartyException.class,
                () -> partiesVerifierService.verify(message)
            );
        }

        @Test
        void should_fail_when_the_from_party_is_incorrect() {
            var message = MessageTestFixtures.createInvalidFromPartyOutboundBusinessMessage();

            assertThrows(
                ConnectorMessagePartyException.class,
                () -> partiesVerifierService.verify(message)
            );
        }

        @Test
        void should_fail_when_the_to_party_is_null() {
            var message = MessageTestFixtures.createNullToPartyOutboundBusinessMessage();

            assertThrows(
                ConnectorMessagePartyException.class,
                () -> partiesVerifierService.verify(message)
            );
        }

        @Test
        void should_fail_when_the_to_party_is_incorrect() {
            var message = MessageTestFixtures.createInvalidToPartyOutboundBusinessMessage();

            assertThrows(
                ConnectorMessagePartyException.class,
                () -> partiesVerifierService.verify(message)
            );
        }
    }

    @Nested
    @DisplayName("when the message cannot be verified")
    class WhenNotVerifiable {
        @Test
        void should_fail_when_the_message_is_null() {
            assertThrows(
                NullPointerException.class,
                () -> partiesVerifierService.verify(null)
            );
        }

        @Test
        void should_fail_when_the_direction_is_null() {
            var message = MessageTestFixtures.createOutboundBusinessMessage()
                                             .toBuilder()
                                             .direction(null)
                                             .build();

            assertThrows(
                IllegalStateException.class,
                () -> partiesVerifierService.verify(message)
            );
        }

        @Test
        void should_fail_when_the_direction_is_not_outbound() {
            var message = MessageTestFixtures.createOutboundBusinessMessage()
                                             .toBuilder()
                                             .direction(ConnectorMessageDirection.GATEWAY_TO_BACKEND)
                                             .build();

            assertThrows(
                UnsupportedOperationException.class,
                () -> partiesVerifierService.verify(message)
            );
        }
    }
}


