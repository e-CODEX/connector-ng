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

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import eu.ecodex.connector.BusinessDomainIdentifierTestFixtures;
import eu.ecodex.connector.MessageContentTestFixtures;
import eu.ecodex.connector.application.exception.message.ConnectorMessageAttachmentException;
import eu.ecodex.connector.application.port.spi.ConnectorFileStorageProvider;
import eu.ecodex.connector.application.port.spi.message.ConnectorMessageAttachmentRepository;
import eu.ecodex.connector.application.port.spi.message.ConnectorMessageRepository;
import eu.ecodex.connector.domain.model.message.ConnectorBusinessMessage;
import eu.ecodex.connector.domain.model.message.attachment.ConnectorMessageAttachment;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("ConnectorCleanupUnresolvedMessagesAttachmentsService")
class ConnectorCleanupUnresolvedMessagesAttachmentsServiceTest {
    private static final Instant NOW = Instant.parse("2026-10-29T19:00:00Z");
    private static final Instant THRESHOLD = Instant.parse("2026-10-22T19:00:00Z");

    @Mock
    private ConnectorMessageRepository messageRepository;
    @Mock
    private ConnectorMessageAttachmentRepository attachmentRepository;
    @Mock
    private ConnectorFileStorageProvider fileStorageProvider;

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

    private ConnectorMessageAttachment attachment(String identifier) {
        return ConnectorMessageAttachment.builder()
                                         .identifier(identifier)
                                         .build();
    }

    @BeforeEach
    void setUp() {
        var clock = Clock.fixed(NOW, ZoneOffset.UTC);
        service = new ConnectorCleanupUnresolvedMessagesAttachmentsService(
            messageRepository, attachmentRepository, fileStorageProvider, clock);
    }

    @Nested
    @DisplayName("when there is nothing to clean up")
    class NothingToCleanUp {
        @Test
        @DisplayName("does nothing when there are no unresolved messages")
        void should_do_nothing_when_no_unresolved_messages() {
            when(messageRepository.findAllUnresolved(THRESHOLD)).thenReturn(List.of());

            service.execute(THRESHOLD);

            verifyNoInteractions(attachmentRepository, fileStorageProvider);
        }

        @Test
        @DisplayName("skips a message that has no active attachments")
        void should_skip_message_without_active_attachments() {
            when(messageRepository.findAllUnresolved(THRESHOLD))
                .thenReturn(List.of(message("msg-1")));
            when(attachmentRepository.findAllActiveByMessageIdentifier("msg-1"))
                .thenReturn(List.of());

            service.execute(THRESHOLD);

            verifyNoInteractions(fileStorageProvider);
            verify(attachmentRepository, never()).markAsDeleted(anyList(), any());
        }
    }

    @Nested
    @DisplayName("when attachments are cleaned up")
    class CleaningUpAttachments {
        @Test
        @DisplayName("deletes the content and marks the attachments as deleted "
            + "with the clock instant")
        void should_delete_content_and_marks_attachments_as_deleted() {
            when(messageRepository.findAllUnresolved(THRESHOLD))
                .thenReturn(List.of(message("msg-1")));
            when(attachmentRepository.findAllActiveByMessageIdentifier("msg-1"))
                .thenReturn(List.of(attachment("att-1"), attachment("att-2")));

            service.execute(THRESHOLD);

            verify(fileStorageProvider).deleteByIdentifier("att-1");
            verify(fileStorageProvider).deleteByIdentifier("att-2");
            verify(attachmentRepository).markAsDeleted(List.of("att-1", "att-2"), NOW);
        }

        @Test
        @DisplayName("processes every unresolved message")
        void should_processe_every_unresolved_message() {
            when(messageRepository.findAllUnresolved(THRESHOLD))
                .thenReturn(List.of(message("msg-1"), message("msg-2")));
            when(attachmentRepository.findAllActiveByMessageIdentifier("msg-1"))
                .thenReturn(List.of(attachment("att-1")));
            when(attachmentRepository.findAllActiveByMessageIdentifier("msg-2"))
                .thenReturn(List.of(attachment("att-2")));

            service.execute(THRESHOLD);

            verify(attachmentRepository).markAsDeleted(List.of("att-1"), NOW);
            verify(attachmentRepository).markAsDeleted(List.of("att-2"), NOW);
        }
    }

    @Nested
    @DisplayName("when the storage deletion fails")
    class StorageDeletionFails {
        @Test
        @DisplayName("marks as deleted only the attachments whose content was deleted")
        void should_mark_only_successfully_deleted_attachments() {
            when(messageRepository.findAllUnresolved(THRESHOLD))
                .thenReturn(List.of(message("msg-1")));
            when(attachmentRepository.findAllActiveByMessageIdentifier("msg-1"))
                .thenReturn(
                    List.of(attachment("att-1"), attachment("att-2"), attachment("att-3")));
            lenient().doThrow(new ConnectorMessageAttachmentException("storage error"))
                .when(fileStorageProvider).deleteByIdentifier("att-2");

            service.execute(THRESHOLD);

            verify(fileStorageProvider).deleteByIdentifier("att-1");
            verify(fileStorageProvider).deleteByIdentifier("att-2");
            verify(fileStorageProvider).deleteByIdentifier("att-3");
            verify(attachmentRepository).markAsDeleted(List.of("att-1", "att-3"), NOW);
        }

        @Test
        @DisplayName("does not mark anything as deleted when every content deletion fails")
        void should_not_mark_anything_when_all_deletions_fail() {
            when(messageRepository.findAllUnresolved(THRESHOLD))
                .thenReturn(List.of(message("msg-1")));
            when(attachmentRepository.findAllActiveByMessageIdentifier("msg-1"))
                .thenReturn(List.of(attachment("att-1")));
            doThrow(new ConnectorMessageAttachmentException("storage error"))
                .when(fileStorageProvider).deleteByIdentifier("att-1");

            service.execute(THRESHOLD);

            verify(attachmentRepository, never()).markAsDeleted(anyList(), any());
        }
    }

    @Nested
    @DisplayName("when an unexpected failure occurs")
    class UnexpectedFailure {
        @Test
        @DisplayName("continues with the other messages when one message fails")
        void should_continue_when_one_message_fails() {
            when(messageRepository.findAllUnresolved(THRESHOLD))
                .thenReturn(List.of(message("msg-1"), message("msg-2")));
            when(attachmentRepository.findAllActiveByMessageIdentifier("msg-1"))
                .thenThrow(new IllegalStateException("database error"));
            when(attachmentRepository.findAllActiveByMessageIdentifier("msg-2"))
                .thenReturn(List.of(attachment("att-2")));

            service.execute(THRESHOLD);

            verify(fileStorageProvider, never()).deleteByIdentifier("att-1");
            verify(fileStorageProvider).deleteByIdentifier("att-2");
            verify(attachmentRepository).markAsDeleted(List.of("att-2"), NOW);
        }

        @Test
        @DisplayName("continues with the other messages when marking as deleted fails")
        void should_continue_when_mark_as_deleted_fails() {
            when(messageRepository.findAllUnresolved(THRESHOLD))
                .thenReturn(List.of(message("msg-1"), message("msg-2")));
            when(attachmentRepository.findAllActiveByMessageIdentifier("msg-1"))
                .thenReturn(List.of(attachment("att-1")));
            when(attachmentRepository.findAllActiveByMessageIdentifier("msg-2"))
                .thenReturn(List.of(attachment("att-2")));
            doThrow(new IllegalStateException("database error"))
                .when(attachmentRepository).markAsDeleted(eq(List.of("att-1")), any());

            service.execute(THRESHOLD);

            verify(fileStorageProvider).deleteByIdentifier("att-2");
            verify(attachmentRepository).markAsDeleted(List.of("att-2"), NOW);
        }
    }

    @Nested
    @SuppressWarnings("DataFlowIssue")
    @DisplayName("argument validation")
    class ArgumentValidation {
        @Test
        @DisplayName("rejects a null threshold")
        void should_reject_null_threshold() {
            assertThatThrownBy(() -> service.execute(null))
                .isInstanceOf(NullPointerException.class);

            verifyNoInteractions(messageRepository, attachmentRepository, fileStorageProvider);
        }
    }
}
