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
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import eu.ecodex.connector.MessageAttachmentTestFixtures;
import eu.ecodex.connector.MessageContentTestFixtures;
import eu.ecodex.connector.MessageTestFixtures;
import eu.ecodex.connector.application.service.impl.message.outbound.ConnectorOutboundMessageStagerService;
import eu.ecodex.connector.application.service.usecase.message.outbound.ConnectorOutboundMessageStager;
import eu.ecodex.connector.domain.api.ConnectorEventPublisher;
import eu.ecodex.connector.domain.exception.ConnectorMessageException;
import eu.ecodex.connector.domain.model.message.ConnectorMessage;
import eu.ecodex.connector.domain.spi.message.ConnectorMessageAttachmentRepository;
import eu.ecodex.connector.domain.spi.message.ConnectorMessageBusinessContentRepository;
import eu.ecodex.connector.domain.spi.message.ConnectorMessageRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ConnectorOutboundMessageStagerServiceTest {
    @Mock
    private ConnectorEventPublisher outboundMessagePipelinePublisher;
    @Mock
    private ConnectorMessageRepository messageRepository;
    @Mock
    private ConnectorMessageAttachmentRepository attachmentRepository;
    @Mock
    private ConnectorMessageBusinessContentRepository businessContentRepository;

    private ConnectorOutboundMessageStager outboundMessageStagerService;

    @BeforeEach
    void setUp() {
        outboundMessageStagerService = new ConnectorOutboundMessageStagerService(
                outboundMessagePipelinePublisher,
                messageRepository,
                attachmentRepository,
                businessContentRepository
        );
    }

    @Test
    void should_stage_message_successfully_with_attachments() {
        when(messageRepository.save(any())).thenReturn(createMessage());
        when(attachmentRepository.findByIdentifier(any()))
                .thenReturn(MessageAttachmentTestFixtures.createAttachment());
        doNothing().when(attachmentRepository).attachToMessage(any(), any());
        when(businessContentRepository.save(any(), any()))
                .thenReturn(MessageContentTestFixtures.createContent());

        outboundMessageStagerService.stage(createMessage());

        verify(messageRepository).save(any());
        verify(attachmentRepository, times(3)).findByIdentifier(any());
        verify(attachmentRepository, times(3)).attachToMessage(any(), any());
        verify(attachmentRepository, times(3)).updateType(any(), any());

        verify(outboundMessagePipelinePublisher).publish(any());
    }

    @Test
    void should_stage_message_successfully_without_attachments() {
        when(messageRepository.save(any())).thenReturn(
                createMessage()
                        .toBuilder()
                        .attachments(null)
                        .build()
        );

        when(attachmentRepository.findByIdentifier(any()))
                .thenReturn(MessageAttachmentTestFixtures.createAttachment());
        doNothing().when(attachmentRepository).attachToMessage(any(), any());
        when(businessContentRepository.save(any(), any()))
                .thenReturn(MessageContentTestFixtures.createContent());

        outboundMessageStagerService.stage(createMessage());

        verify(outboundMessagePipelinePublisher).publish(any());
    }

    @Test
    void should_throw_exception_if_the_message_is_an_evidence_message() {
        when(messageRepository.save(any())).thenReturn(
                createMessage()
                        .toBuilder()
                        .attachments(null)
                        .build()
        );

        assertThrows(
                ConnectorMessageException.class,
                () -> outboundMessageStagerService.stage(
                        MessageTestFixtures.createValidEvidenceMessage()
                )
        );


        verifyNoInteractions(
                attachmentRepository,
                businessContentRepository,
                outboundMessagePipelinePublisher
        );
    }

    @Test
    void should_fail_to_stage_message_with_unknown_attachment() {
        when(messageRepository.save(any())).thenReturn(createMessage());
        when(attachmentRepository.findByIdentifier(any()))
                .thenReturn(null);

        assertThrows(
                IllegalStateException.class,
                () -> outboundMessageStagerService.stage(createMessage())
        );

        verify(messageRepository).save(any());
        verify(attachmentRepository).findByIdentifier(any());
        verify(attachmentRepository, never()).attachToMessage(any(), any());
        verify(attachmentRepository, never()).updateType(any(), any());
    }

    private ConnectorMessage createMessage() {
        var message = MessageTestFixtures.createValidOutboundBusinessMessage();

        return message
                .toBuilder()
                .businessContent(
                        MessageContentTestFixtures.createContent()
                )
                .attachments(List.of(MessageAttachmentTestFixtures.createAttachment()))
                .build();
    }
}
