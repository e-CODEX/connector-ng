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

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import eu.ecodex.connector.BusinessMessageTestFixtures;
import eu.ecodex.connector.application.exception.ConnectorMessageNotFoundException;
import eu.ecodex.connector.application.port.spi.message.ConnectorMessageRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@SuppressWarnings("DataFlowIssue")
@ExtendWith(MockitoExtension.class)
@DisplayName("ConnectorRetrieveMessageService")
public class ConnectorRetrieveMessageServiceTest {
    @Mock
    private ConnectorMessageRepository messageRepository;

    @InjectMocks
    private ConnectorRetrieveMessageService retrieveMessageService;

    @Nested
    @DisplayName("when retrieval succeeds")
    class WhenRetrievalSucceeds {
        @Test
        void should_return_the_message() {
            var message = BusinessMessageTestFixtures.createOutboundMessage();
            when(messageRepository.findByIdentifier(any())).thenReturn(message);

            var retrievedMessage = retrieveMessageService.execute(
                "223caef9-cae9-4387-a38c-ad4879f94b4e@connector.ecodex.eu");

            assertThat(retrievedMessage).isNotNull();
            assertThat(retrievedMessage).isEqualTo(message);
        }
    }

    @Nested
    @DisplayName("when retrieval fails")
    class WhenRetrievalFails {
        @Test
        void should_fail_when_the_message_identifier_is_null() {
            assertThrows(
                NullPointerException.class,
                () -> retrieveMessageService.execute(null)
            );
        }

        @Test
        void should_fail_when_the_message_is_not_found() {
            when(messageRepository.findByIdentifier(any())).thenReturn(null);

            assertThrows(
                ConnectorMessageNotFoundException.class,
                () -> retrieveMessageService.execute("message-identifier")
            );
        }
    }
}
