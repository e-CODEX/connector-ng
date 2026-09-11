/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.service.message.inbound;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
import eu.ecodex.connector.EvidenceTestFixtures;
import eu.ecodex.connector.MessageAttachmentTestFixtures;
import eu.ecodex.connector.MessageContentTestFixtures;
import eu.ecodex.connector.application.port.api.message.ConnectorMessageAttachmentLinker;
import eu.ecodex.connector.application.port.spi.ConnectorMessageEventPublisher;
import eu.ecodex.connector.application.port.spi.message.ConnectorMessageEvidenceRepository;
import eu.ecodex.connector.application.port.spi.message.ConnectorMessageRepository;
import eu.ecodex.connector.domain.model.message.ConnectorBusinessMessage;
import eu.ecodex.connector.domain.model.message.attachment.ConnectorAttachmentType;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@SuppressWarnings("DataFlowIssue")
@ExtendWith(MockitoExtension.class)
@DisplayName("ConnectorInboundBusinessMessageStagerService")
public class ConnectorInboundBusinessMessageStagerServiceTest {
    @Mock
    private ConnectorMessageEventPublisher<ConnectorBusinessMessage> inboundMessagePipelinePublisher;
    @Mock
    private ConnectorMessageRepository messageRepository;
    @Mock
    private ConnectorMessageEvidenceRepository evidenceRepository;
    @Mock
    private ConnectorMessageAttachmentLinker attachmentLinkerService;

    @InjectMocks
    private ConnectorInboundBusinessMessageStagerService inboundMessageStagerService;

    private ConnectorBusinessMessage createMessage() {
        return BusinessMessageTestFixtures.createInboundMessage()
                                          .toBuilder()
                                          .businessContent(MessageContentTestFixtures.createContent())
                                          .attachments(List.of(
                                              MessageAttachmentTestFixtures.createAttachment(),
                                              MessageAttachmentTestFixtures.createBusinessDocumentAttachment()
                                          ))
                                          .transportedEvidences(List.of(
                                              EvidenceTestFixtures.createSubmissionAcceptanceEvidence(),
                                              EvidenceTestFixtures.createRelayREMMDAcceptanceEvidence()
                                          ))
                                          .build();
    }

    @Nested
    @DisplayName("when staging succeeds")
    class WhenStagingSucceeds {
        @Test
        void should_stage_the_message_with_its_attachments_and_evidences() {
            var message = createMessage();
            when(messageRepository.save(message)).thenReturn(message);
            doNothing().when(attachmentLinkerService).execute(any(), any(), any());
            when(evidenceRepository.save(any(), any())).thenAnswer(i -> i.getArgument(0));

            inboundMessageStagerService.execute(message);

            verify(messageRepository).save(message);
            for (var attachment : message.attachments()) {
                verify(attachmentLinkerService).execute(
                    attachment.identifier(),
                    message.identifier(),
                    attachment.type()
                );
            }
            verify(attachmentLinkerService).execute(
                message.businessContent().xmlContent().identifier(),
                message.identifier(),
                ConnectorAttachmentType.BUSINESS_CONTENT
            );
            for (var evidence : message.transportedEvidences()) {
                verify(evidenceRepository).save(evidence, message.identifier());
            }

            var messageCaptor = ArgumentCaptor.forClass(ConnectorBusinessMessage.class);
            verify(inboundMessagePipelinePublisher).publish(messageCaptor.capture());
            var publishedMessage = messageCaptor.getValue();
            assertThat(publishedMessage).isNotNull();
            assertThat(publishedMessage.identifier()).isEqualTo(message.identifier());
            assertThat(publishedMessage.transportedEvidences())
                .isEqualTo(message.transportedEvidences());
        }

        @Test
        void should_stage_the_message_without_attachments() {
            var message = createMessage()
                .toBuilder()
                .attachments(null)
                .build();
            when(messageRepository.save(message)).thenReturn(message);
            doNothing().when(attachmentLinkerService).execute(any(), any(), any());
            when(evidenceRepository.save(any(), any())).thenAnswer(i -> i.getArgument(0));

            inboundMessageStagerService.execute(message);

            verify(messageRepository).save(message);
            verify(attachmentLinkerService).execute(
                message.businessContent().xmlContent().identifier(),
                message.identifier(),
                ConnectorAttachmentType.BUSINESS_CONTENT
            );
            verify(attachmentLinkerService, never()).execute(
                any(),
                any(),
                eq(ConnectorAttachmentType.ATTACHMENT)
            );
            for (var evidence : message.transportedEvidences()) {
                verify(evidenceRepository).save(evidence, message.identifier());
            }
            verify(inboundMessagePipelinePublisher).publish(message);
        }

        @Test
        void should_stage_the_message_with_empty_attachments() {
            var message = createMessage()
                .toBuilder()
                .attachments(List.of())
                .build();
            when(messageRepository.save(message)).thenReturn(message);
            doNothing().when(attachmentLinkerService).execute(any(), any(), any());
            when(evidenceRepository.save(any(), any())).thenAnswer(i -> i.getArgument(0));

            inboundMessageStagerService.execute(message);

            verify(messageRepository).save(message);
            verify(attachmentLinkerService).execute(
                message.businessContent().xmlContent().identifier(),
                message.identifier(),
                ConnectorAttachmentType.BUSINESS_CONTENT
            );
            verify(attachmentLinkerService, never()).execute(
                any(),
                any(),
                eq(ConnectorAttachmentType.ATTACHMENT)
            );
            for (var evidence : message.transportedEvidences()) {
                verify(evidenceRepository).save(evidence, message.identifier());
            }
            verify(inboundMessagePipelinePublisher).publish(message);
        }
    }

    @Nested
    @DisplayName("when the message cannot be staged")
    class WhenTheMessageCannotBeStaged {
        @Test
        void should_fail_if_the_message_is_null() {
            assertThrows(
                NullPointerException.class,
                () -> inboundMessageStagerService.execute(null)
            );

            verifyNoInteractions(
                inboundMessagePipelinePublisher,
                messageRepository,
                evidenceRepository,
                attachmentLinkerService
            );
        }

        @Test
        void should_fail_when_transported_evidences_is_empty() {
            var message = createMessage()
                .toBuilder()
                .transportedEvidences(List.of())
                .build();

            assertThatThrownBy(() -> inboundMessageStagerService.execute(message))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Transported evidences must not be empty");

            verifyNoInteractions(
                inboundMessagePipelinePublisher,
                messageRepository,
                evidenceRepository,
                attachmentLinkerService
            );
        }

        @Test
        void should_fail_when_message_cannot_be_saved() {
            var message = createMessage();
            doThrow(RuntimeException.class).when(messageRepository).save(any());

            assertThrows(
                RuntimeException.class,
                () -> inboundMessageStagerService.execute(message)
            );

            verify(messageRepository).save(message);
            verifyNoInteractions(
                attachmentLinkerService,
                evidenceRepository,
                inboundMessagePipelinePublisher
            );
        }

        @Test
        void should_fail_when_evidence_content_is_null() {
            var message = createMessage()
                .toBuilder()
                .transportedEvidences(List.of(EvidenceTestFixtures.createEvidenceTrigger()))
                .build();
            when(messageRepository.save(message)).thenReturn(message);
            doNothing().when(attachmentLinkerService).execute(any(), any(), any());

            assertThatThrownBy(() -> inboundMessageStagerService.execute(message))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Evidence content is null for evidence");

            verify(messageRepository).save(message);
            for (var attachment : message.attachments()) {
                verify(attachmentLinkerService).execute(
                    attachment.identifier(),
                    message.identifier(),
                    attachment.type()
                );
            }
            verify(attachmentLinkerService).execute(
                message.businessContent().xmlContent().identifier(),
                message.identifier(),
                ConnectorAttachmentType.BUSINESS_CONTENT
            );
            verifyNoInteractions(
                inboundMessagePipelinePublisher,
                evidenceRepository
            );
        }

        @Test
        void should_fail_when_an_attachment_cannot_be_linked() {
            var message = createMessage();
            when(messageRepository.save(message)).thenReturn(message);
            doThrow(IllegalStateException.class)
                .when(attachmentLinkerService).execute(any(), any(), any());

            assertThrows(
                IllegalStateException.class,
                () -> inboundMessageStagerService.execute(message)
            );

            verify(messageRepository).save(message);
            verify(attachmentLinkerService).execute(any(), any(), any());
            verifyNoInteractions(
                inboundMessagePipelinePublisher,
                evidenceRepository
            );
        }

        @Test
        void should_fail_when_evidence_cannot_be_saved() {
            var message = createMessage();
            when(messageRepository.save(message)).thenReturn(message);
            doNothing().when(attachmentLinkerService).execute(any(), any(), any());
            doThrow(IllegalStateException.class)
                .when(evidenceRepository).save(any(), any());

            assertThrows(
                IllegalStateException.class,
                () -> inboundMessageStagerService.execute(message)
            );

            verify(messageRepository).save(message);
            for (var attachment : message.attachments()) {
                verify(attachmentLinkerService).execute(
                    attachment.identifier(),
                    message.identifier(),
                    attachment.type()
                );
            }
            verify(attachmentLinkerService).execute(
                message.businessContent().xmlContent().identifier(),
                message.identifier(),
                ConnectorAttachmentType.BUSINESS_CONTENT
            );
            verify(evidenceRepository).save(
                message.transportedEvidences().getFirst(),
                message.identifier()
            );
            verifyNoInteractions(inboundMessagePipelinePublisher);
        }
    }
}
