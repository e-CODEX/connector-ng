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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import eu.ecodex.connector.BusinessDomainIdentifierTestFixtures;
import eu.ecodex.connector.MessageContentTestFixtures;
import eu.ecodex.connector.application.port.spi.message.ConnectorMessageRepository;
import eu.ecodex.connector.application.service.attachement.ConnectorAttachmentFilesDeleter;
import eu.ecodex.connector.domain.model.message.ConnectorBusinessMessage;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("ConnectorCleanupUnresolvedMessagesAttachmentsService")
class ConnectorCleanupUnresolvedMessagesAttachmentsServiceTest {
    private static final Instant THRESHOLD = Instant.parse("2026-10-23T22:00:00Z");

    @Mock
    private ConnectorMessageRepository messageRepository;
    @Mock
    private ConnectorAttachmentFilesDeleter filesDeleter;

    @InjectMocks
    private ConnectorCleanupUnresolvedMessagesAttachmentsService service;

    private ConnectorBusinessMessage message(String identifier) {
        return ConnectorBusinessMessage
            .builder()
            .businessDomainIdentifier(
                BusinessDomainIdentifierTestFixtures.createDefaultBusinessDomainIdentifier())
            .identifier(identifier)
            .businessContent(MessageContentTestFixtures.createContent())
            .build();
    }

    @Nested
    @DisplayName("execute")
    class Execute {
        @Test
        void should_not_call_the_deleter_when_there_are_no_unresolved_messages() {
            when(messageRepository.findAllUnresolved(THRESHOLD)).thenReturn(List.of());

            service.execute(THRESHOLD);

            verifyNoInteractions(filesDeleter);
        }

        @Test
        void should_clean_up_the_attachments_of_every_unresolved_message() {
            when(messageRepository.findAllUnresolved(THRESHOLD))
                .thenReturn(List.of(message("msg-1"), message("msg-2")));
            when(filesDeleter.deleteActiveAttachmentsOf(anyString()))
                .thenReturn(new ConnectorAttachmentFilesDeleter.Result(1, 0));

            service.execute(THRESHOLD);

            verify(filesDeleter).deleteActiveAttachmentsOf("msg-1");
            verify(filesDeleter).deleteActiveAttachmentsOf("msg-2");
        }

        @Test
        void should_continue_with_the_next_message_when_one_cleanup_throws() {
            when(messageRepository.findAllUnresolved(THRESHOLD))
                .thenReturn(List.of(message("msg-1"), message("msg-2"), message("msg-3")));
            when(filesDeleter.deleteActiveAttachmentsOf("msg-1"))
                .thenReturn(ConnectorAttachmentFilesDeleter.Result.NONE);
            doThrow(new RuntimeException("database error"))
                .when(filesDeleter).deleteActiveAttachmentsOf("msg-2");
            when(filesDeleter.deleteActiveAttachmentsOf("msg-3"))
                .thenReturn(ConnectorAttachmentFilesDeleter.Result.NONE);

            assertThatCode(() -> service.execute(THRESHOLD)).doesNotThrowAnyException();

            verify(filesDeleter).deleteActiveAttachmentsOf("msg-3");
        }
    }
}
