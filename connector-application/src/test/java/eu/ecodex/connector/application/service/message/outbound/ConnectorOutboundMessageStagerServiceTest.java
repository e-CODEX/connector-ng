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
import static org.mockito.Mockito.when;

import eu.ecodex.connector.MessageAttachmentTestFixtures;
import eu.ecodex.connector.MessageContentTestFixtures;
import eu.ecodex.connector.MessageTestFixtures;
import eu.ecodex.connector.application.service.impl.message.outbound.ConnectorOutboundMessageStagerService;
import eu.ecodex.connector.domain.model.message.ConnectorMessage;
import eu.ecodex.connector.domain.spi.ConnectorMessageAttachmentRepository;
import eu.ecodex.connector.domain.spi.ConnectorMessageBusinessContentRepository;
import eu.ecodex.connector.domain.spi.ConnectorMessageRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ConnectorOutboundMessageStagerServiceTest {
    @Mock
    private ConnectorMessageRepository messageRepository;
    @Mock
    private ConnectorMessageAttachmentRepository attachmentRepository;
    @Mock
    private ConnectorMessageBusinessContentRepository businessContentRepository;
    @InjectMocks
    private ConnectorOutboundMessageStagerService connectorStageOutboundMessageService;

    @Test
    void should_stage_message_successfully_with_attachments() {
        when(messageRepository.save(any())).thenReturn(createMessage());
        when(attachmentRepository.findByIdentifier(any()))
                .thenReturn(MessageAttachmentTestFixtures.createAttachment());
        doNothing().when(attachmentRepository).attachToMessage(any(), any());
        when(businessContentRepository.save(any(), any()))
                .thenReturn(MessageContentTestFixtures.createContent());

        connectorStageOutboundMessageService.stage(createMessage());
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

        connectorStageOutboundMessageService.stage(createMessage());
    }

    @Test
    void should_fail_to_stage_message_with_unknown_attachment() {
        when(messageRepository.save(any())).thenReturn(createMessage());
        when(attachmentRepository.findByIdentifier(any()))
                .thenReturn(null);

        assertThrows(
                IllegalStateException.class,
                () -> connectorStageOutboundMessageService.stage(createMessage())
        );
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
