/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.service.message.outbound;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import eu.ecodex.connector.BusinessMessageTestFixtures;
import eu.ecodex.connector.MessageAttachmentTestFixtures;
import eu.ecodex.connector.MessageContentTestFixtures;
import eu.ecodex.connector.application.exception.ConnectorMessageException;
import eu.ecodex.connector.application.port.api.message.ConnectorMessageAttachmentLinker;
import eu.ecodex.connector.application.port.spi.ConnectorMessageEventPublisher;
import eu.ecodex.connector.application.port.spi.message.ConnectorMessageRepository;
import eu.ecodex.connector.domain.model.message.ConnectorBusinessMessage;
import eu.ecodex.connector.domain.model.message.attachment.ConnectorAttachmentType;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("ConnectorOutboundBusinessMessageStagerService")
public class ConnectorOutboundBusinessMessageStagerServiceTest {
    @Mock
    private ConnectorMessageEventPublisher<ConnectorBusinessMessage> outboundMessagePipelinePublisher;
    @Mock
    private ConnectorMessageRepository messageRepository;
    @Mock
    private ConnectorMessageAttachmentLinker attachmentLinkerService;

    @InjectMocks
    private ConnectorOutboundBusinessMessageStagerService outboundMessageStagerService;

    private ConnectorBusinessMessage createMessage() {
        var message = BusinessMessageTestFixtures.createOutboundMessage();

        return message
            .toBuilder()
            .businessContent(MessageContentTestFixtures.createContent())
            .attachments(List.of(MessageAttachmentTestFixtures.createAttachment()))
            .build();
    }

    @Nested
    @DisplayName("when staging succeeds")
    class WhenStagingSucceeds {
        @Test
        void should_stage_the_message_with_its_attachments() {
            var message = createMessage();
            when(messageRepository.save(message)).thenReturn(message);
            doNothing().when(attachmentLinkerService).execute(any(), any(), any());

            outboundMessageStagerService.execute(message);

            verify(messageRepository).save(message);
            verify(attachmentLinkerService).execute(
                message.attachments().getFirst().identifier(),
                message.identifier(),
                ConnectorAttachmentType.ATTACHMENT
            );
            verify(attachmentLinkerService).execute(
                message.businessContent().xmlContent().identifier(),
                message.identifier(),
                ConnectorAttachmentType.BUSINESS_CONTENT
            );
            verify(attachmentLinkerService).execute(
                message.businessContent().businessDocument().attachment().identifier(),
                message.identifier(),
                ConnectorAttachmentType.BUSINESS_DOCUMENT
            );
            verify(outboundMessagePipelinePublisher).publish(message);
        }

        @Test
        void should_stage_the_message_without_attachments() {
            var message = createMessage()
                .toBuilder()
                .attachments(null)
                .build();
            when(messageRepository.save(message)).thenReturn(message);
            doNothing().when(attachmentLinkerService).execute(any(), any(), any());

            outboundMessageStagerService.execute(message);

            verify(messageRepository).save(message);
            verify(attachmentLinkerService).execute(
                message.businessContent().xmlContent().identifier(),
                message.identifier(),
                ConnectorAttachmentType.BUSINESS_CONTENT
            );
            verify(attachmentLinkerService).execute(
                message.businessContent().businessDocument().attachment().identifier(),
                message.identifier(),
                ConnectorAttachmentType.BUSINESS_DOCUMENT
            );
            verify(attachmentLinkerService, never()).execute(
                any(),
                any(),
                eq(ConnectorAttachmentType.ATTACHMENT)
            );
            verify(outboundMessagePipelinePublisher).publish(message);
        }
    }

    @SuppressWarnings("DataFlowIssue")
    @Nested
    @DisplayName("when the message cannot be staged")
    class WhenTheMessageCannotBeStaged {
        @Test
        void should_fail_if_the_message_is_null() {
            assertThrows(
                NullPointerException.class,
                () -> outboundMessageStagerService.execute(null)
            );

            verifyNoInteractions(
                outboundMessagePipelinePublisher,
                messageRepository,
                attachmentLinkerService
            );
        }

        @Test
        void should_fail_when_an_attachment_is_unknown() {
            var message = createMessage();
            when(messageRepository.save(message)).thenReturn(message);
            doThrow(IllegalStateException.class)
                .when(attachmentLinkerService).execute(any(), any(), any());

            assertThrows(
                IllegalStateException.class,
                () -> outboundMessageStagerService.execute(message)
            );

            verify(messageRepository).save(message);
            verify(attachmentLinkerService).execute(any(), any(), any());
            verifyNoInteractions(outboundMessagePipelinePublisher);
        }

        @Test
        void should_fail_when_the_business_document_is_null() {
            var message = createMessage()
                .toBuilder()
                .businessContent(
                    MessageContentTestFixtures.createContent()
                                              .toBuilder()
                                              .businessDocument(null)
                                              .build()
                )
                .attachments(null)
                .build();
            when(messageRepository.save(message)).thenReturn(message);

            assertThrows(
                ConnectorMessageException.class,
                () -> outboundMessageStagerService.execute(message)
            );

            verify(messageRepository).save(message);
            verifyNoInteractions(attachmentLinkerService, outboundMessagePipelinePublisher);
        }
    }
}

