/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.helper;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import eu.ecodex.connector.BusinessDomainTestFixtures;
import eu.ecodex.connector.application.port.spi.ConnectorFileStorageProvider;
import eu.ecodex.connector.application.port.spi.message.ConnectorMessageAttachmentRepository;
import eu.ecodex.connector.domain.model.message.ConnectorMessage;
import eu.ecodex.connector.domain.model.message.ConnectorMessageAS4Properties;
import eu.ecodex.connector.domain.model.message.ConnectorMessageDirection;
import eu.ecodex.connector.domain.model.message.attachment.ConnectorAttachmentType;
import eu.ecodex.connector.domain.model.message.attachment.ConnectorMessageAttachment;
import eu.ecodex.connector.domain.model.message.content.ConnectorMessageBusinessContent;
import eu.ecodex.connector.domain.model.message.evidence.ConnectorEvidenceType;
import eu.ecodex.connector.domain.model.message.evidence.ConnectorMessageEvidence;
import eu.ecodex.connector.domain.model.pmode.ConnectorAction;
import eu.ecodex.connector.domain.model.pmode.ConnectorParty;
import eu.ecodex.connector.domain.model.pmode.ConnectorPartyRoleType;
import eu.ecodex.connector.domain.model.pmode.ConnectorService;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class LegacyMessageHelperTest {
    private static final String MESSAGE_ID = "msg-001";
    private static final String BACKEND_NAME = "backend-alice";

    @Mock
    private ConnectorMessageAttachmentRepository attachmentRepository;
    @Mock
    private ConnectorFileStorageProvider fileStorageProvider;

    @InjectMocks
    private LegacyMessageHelper legacyMessageHelper;

    @Test
    void should_throw_an_exception_if_as4_properties_service_is_missing() {
        var brokenAS4 = as4Properties().toBuilder().service(null).build();
        var inbound = inboundMessage().toBuilder().as4Properties(brokenAS4).build();

        assertThatThrownBy(() -> legacyMessageHelper.convertMessage(inbound))
            .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void should_throw_and_swallow_exception_if_as4_properties_action_is_missing() {
        var brokenAS4 = as4Properties().toBuilder().action(null).build();
        var inbound = inboundMessage().toBuilder().as4Properties(brokenAS4).build();

        assertThatThrownBy(() -> legacyMessageHelper.convertMessage(inbound))
            .isInstanceOf(IllegalStateException.class);
    }


    @Test
    void should_throw_and_swallow_exception_if_as4_properties_from_party_is_missing() {
        var brokenAS4 = as4Properties().toBuilder().fromParty(null).build();
        var inbound = inboundMessage().toBuilder().as4Properties(brokenAS4).build();

        assertThatThrownBy(() -> legacyMessageHelper.convertMessage(inbound))
            .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void should_throw_exception_and_swallow_if_as4_properties_to_party_is_missing() {
        var brokenAS4 = as4Properties().toBuilder().toParty(null).build();
        var inbound = inboundMessage().toBuilder().as4Properties(brokenAS4).build();

        assertThatThrownBy(() -> legacyMessageHelper.convertMessage(inbound))
            .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void should_throw_and_swallow_exception_if_evidence_has_no_attachment() {
        var evidence = ConnectorMessageEvidence.builder()
                                               .type(ConnectorEvidenceType.values()[0])
                                               .content(null)
                                               .build();
        var inbound = inboundMessage().toBuilder().transportedEvidences(List.of(evidence)).build();

        when(fileStorageProvider.findByIdentifier("xml-content-id"))
            .thenReturn("<xml/>".getBytes());
        when(attachmentRepository.findByMessageIdentifierAndTypes(eq(MESSAGE_ID), any()))
            .thenReturn(List.of());

        assertThatThrownBy(() -> legacyMessageHelper.convertMessage(inbound))
            .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void should_handle_message_without_business_content_successfully() {
        var inbound = ConnectorMessage.builder()
                                      .identifier(MESSAGE_ID)
                                      .backendName(BACKEND_NAME)
                                      .backendMessageIdentifier(null)
                                      .direction(ConnectorMessageDirection.GATEWAY_TO_BACKEND)
                                      .as4Properties(as4Properties())
                                      .businessContent(null)
                                      .attachments(List.of())
                                      .evidences(List.of())
                                      .build();

        when(attachmentRepository.findByMessageIdentifierAndTypes(eq(MESSAGE_ID), any()))
            .thenReturn(List.of());

        legacyMessageHelper.convertMessage(inbound);

        verify(fileStorageProvider, never()).findByIdentifier(any());
    }

    @Test
    void should_submit_message_with_no_business_content_successfully() {
        var inbound = ConnectorMessage.builder()
                                      .identifier(MESSAGE_ID)
                                      .backendName(BACKEND_NAME)
                                      .backendMessageIdentifier(null)
                                      .direction(ConnectorMessageDirection.GATEWAY_TO_BACKEND)
                                      .as4Properties(as4Properties())
                                      .businessContent(null)
                                      .attachments(List.of())
                                      .evidences(List.of())
                                      .build();

        when(attachmentRepository.findByMessageIdentifierAndTypes(eq(MESSAGE_ID), any()))
            .thenReturn(List.of());

        legacyMessageHelper.convertMessage(inbound);

        verify(fileStorageProvider, never()).findByIdentifier(any());
    }

    @Test
    void should_fetch_attachments_for_message_when_successful() {
        stubHappyPath();

        legacyMessageHelper.convertMessage(inboundMessage());

        verify(attachmentRepository).findByMessageIdentifierAndTypes(
            eq(MESSAGE_ID),
            argThat(types -> types.containsAll(List.of(
                ConnectorAttachmentType.ATTACHMENT,
                ConnectorAttachmentType.PDF_TOKEN,
                ConnectorAttachmentType.XML_TOKEN
            )))
        );
    }

    @Test
    void should_submit_evidence_message_successfully() {
        stubHappyPath();

        var evidence = ConnectorMessageEvidence.builder()
                                               .type(ConnectorEvidenceType.values()[0])
                                               .content(new byte[1])
                                               .build();
        var inbound = inboundMessage().toBuilder().transportedEvidences(List.of(evidence)).build();

        var message = legacyMessageHelper.convertMessage(inbound);

        assertThat(message).isNotNull();
        assertThat(message.getMessageConfirmations().size()).isEqualTo(1);
        assertThat(message.getMessageConfirmations().getFirst().getConfirmationType()).isNotNull();
    }

    private void stubHappyPath() {
        when(fileStorageProvider.findByIdentifier("xml-content-id"))
            .thenReturn("<xml/>".getBytes());
        when(attachmentRepository.findByMessageIdentifierAndTypes(eq(MESSAGE_ID), any()))
            .thenReturn(List.of());
    }

    private ConnectorMessageAS4Properties as4Properties() {
        return ConnectorMessageAS4Properties
            .builder()
            .service(ConnectorService.builder()
                                     .name("EPO")
                                     .type("urn:e-codex:services:")
                                     .build())
            .action(ConnectorAction.builder()
                                   .name("Form_A")
                                   .build())
            .fromParty(ConnectorParty.builder()
                                     .identifier("BL")
                                     .identifierType(
                                         "urn:oasis:names:tc:ebcore:partyid-type:ecodex")
                                     .role("GW")
                                     .roleType(ConnectorPartyRoleType.INITIATOR)
                                     .build())
            .toParty(ConnectorParty.builder()
                                   .identifier("RE")
                                   .identifierType(
                                       "urn:oasis:names:tc:ebcore:partyid-type:ecodex")
                                   .role("GW")
                                   .roleType(ConnectorPartyRoleType.RESPONDER)
                                   .build())
            .conversationIdentifier("3d5ec775-6602-4bb0-a23c-0311ef8dabc8")
            .ebmsMessageIdentifier("50ef4a19-916f-4e38-bc86-4f85921e6f0a@domibus.eu")
            .referenceToIdentifier(null)
            .originalSender("bob")
            .finalRecipient("alice")
            .build();
    }

    private ConnectorMessageBusinessContent businessContent() {
        return ConnectorMessageBusinessContent
            .builder()
            .uuid(UUID.randomUUID().toString())
            .xmlContent(ConnectorMessageAttachment.builder()
                                                  .identifier("xml-content-id")
                                                  .build())
            .build();
    }

    private ConnectorMessage inboundMessage() {
        return ConnectorMessage.builder()
                               .businessDomainIdentifier(
                                   BusinessDomainTestFixtures.createDefaultBusinessDomain()
                                                             .identifier())
                               .identifier(MESSAGE_ID)
                               .backendName(BACKEND_NAME)
                               .backendMessageIdentifier(null)
                               .direction(ConnectorMessageDirection.GATEWAY_TO_BACKEND)
                               .as4Properties(as4Properties())
                               .businessContent(businessContent())
                               .attachments(List.of())
                               .evidences(List.of())
                               .build();
    }
}
