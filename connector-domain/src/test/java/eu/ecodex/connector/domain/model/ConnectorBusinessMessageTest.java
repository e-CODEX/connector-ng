/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.domain.model;


import static org.assertj.core.api.Assertions.assertThat;

import eu.ecodex.connector.BusinessMessageTestFixtures;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("ConnectorBusinessMessage")
public class ConnectorBusinessMessageTest {
    @Nested
    @DisplayName("check if the message has been rejected")
    class CheckIfRejected {
        @Test
        void should_return_true_if_the_message_has_been_rejected() {
            var message = BusinessMessageTestFixtures.createRejectedMessage();

            assertThat(message.isRejected()).isTrue();
        }

        @Test
        void should_return_false_if_the_message_has_not_been_rejected() {
            var message = BusinessMessageTestFixtures.createOutboundMessage();

            assertThat(message.isRejected()).isFalse();
        }
    }
}
