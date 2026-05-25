/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.evidence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import eu.ecodex.connector.EvidenceTestFixtures;
import eu.ecodex.connector.MessageAttachmentTestFixtures;
import eu.ecodex.connector.MessageContentTestFixtures;
import eu.ecodex.connector.MessageTestFixtures;
import eu.ecodex.connector.domain.exception.ConnectorEvidenceException;
import eu.ecodex.connector.domain.model.ConnectorMessageRejectionReason;
import eu.ecodex.connector.domain.model.message.ConnectorMessage;
import eu.ecodex.connector.domain.model.message.attachment.ConnectorMessageAttachment;
import eu.ecodex.connector.domain.model.message.content.ConnectorMessageBusinessContent;
import eu.ecodex.connector.domain.model.message.evidence.ConnectorEvidenceType;
import eu.ecodex.connector.domain.model.message.evidence.ConnectorMessageEvidence;
import eu.ecodex.connector.domain.spi.ConnectorFileStorageProvider;
import eu.ecodex.connector.domain.spi.ConnectorMessageAttachmentRepository;
import eu.ecodex.connector.evidences.EvidenceBuilder;
import eu.ecodex.connector.evidences.exception.ECodexEvidenceBuilderException;
import eu.ecodex.connector.evidences.types.ECodexMessageDetails;
import eu.ecodex.connector.infrastructure.property.evidence.ConnectorEvidencesProperties;
import eu.ecodex.connector.infrastructure.util.HashValueBuilder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.etsi.uri._02640.v2.EventReasonType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ConnectorEvidenceToolkitImplUnitTest {

    @Mock
    private EvidenceBuilder evidenceBuilder;

    @Mock
    private HashValueBuilder hashValueBuilder;

    @Mock
    private ConnectorMessageAttachmentRepository attachmentRepository;

    @Mock
    private ConnectorFileStorageProvider fileStorageProvider;

    private ConnectorEvidencesProperties evidencesProperties;
    private ConnectorEvidenceToolkitImpl toolkit;

    private static final byte[] STUB_EVIDENCE_BYTES = {0x01, 0x02};
    private static final byte[] PREV = {0x0e};
    private static final String HASH_HEX = "abab";
    private static final String PRIOR_EVIDENCE_ATTACHMENT_ID = "prior-attachment-for-test";
    private static final String BUSINESS_XML_ATTACHMENT_ID =
            "104ebc70-abd5-45da-8c74-940d687501b3_messageContent";
    private static final String FIXTURE_SUBMISSION_ACCEPTANCE_ATTACHMENT_ID =
            "c3e18064-e0da-4170-9733-1e7e2768e0bb_SUBMISSION_ACCEPTANCE";

    @BeforeEach
    void setUp() throws ECodexEvidenceBuilderException {
        evidencesProperties = new ConnectorEvidencesProperties();
        evidencesProperties.getIssuer().getAs4Party().setName("issuer-gw");
        evidencesProperties.getIssuer().getAs4Party().setEndpointAddress("https://gw.example/rem");
        evidencesProperties.getIssuer().getPostalAddress().setStreet("Main 1");
        evidencesProperties.getIssuer().getPostalAddress().setLocality("Town");
        evidencesProperties.getIssuer().getPostalAddress().setZipCode("1000");
        evidencesProperties.getIssuer().getPostalAddress().setCountry("EU");

        toolkit = new ConnectorEvidenceToolkitImpl(
                evidenceBuilder,
                hashValueBuilder,
                evidencesProperties,
                attachmentRepository,
                fileStorageProvider
        );

        when(attachmentRepository.save(any(ConnectorMessageAttachment.class)))
                .thenAnswer(inv -> inv.getArgument(0));
        when(fileStorageProvider.findByIdentifier(PRIOR_EVIDENCE_ATTACHMENT_ID)).thenReturn(PREV);
        when(fileStorageProvider.findByIdentifier(FIXTURE_SUBMISSION_ACCEPTANCE_ATTACHMENT_ID)).thenReturn(
                PREV);
        when(fileStorageProvider.findByIdentifier(BUSINESS_XML_ATTACHMENT_ID))
                .thenReturn("<?xml version=\"1.0\"?><root/>".getBytes(StandardCharsets.UTF_8));

        when(hashValueBuilder.getAlgorithm()).thenReturn("SHA-256");
        defaultEvidenceBuilderStubs();
    }

    private void defaultEvidenceBuilderStubs() throws ECodexEvidenceBuilderException {
        when(evidenceBuilder.createSubmissionAcceptanceRejection(
                eq(true), nullable(EventReasonType.class), any(), any()
        )).thenReturn(STUB_EVIDENCE_BYTES);
        when(evidenceBuilder.createSubmissionAcceptanceRejection(
                eq(false), any(EventReasonType.class), any(), any()
        )).thenReturn(STUB_EVIDENCE_BYTES);
        when(evidenceBuilder.createRelayREMMDAcceptanceRejection(
                any(boolean.class), nullable(EventReasonType.class), any(), any()
        )).thenReturn(STUB_EVIDENCE_BYTES);
        when(evidenceBuilder.createRelayREMMDFailure(
                any(EventReasonType.class), any(), any()
        )).thenReturn(STUB_EVIDENCE_BYTES);
        when(evidenceBuilder.createDeliveryNonDeliveryToRecipient(
                any(boolean.class), nullable(EventReasonType.class), any(), any()
        )).thenReturn(STUB_EVIDENCE_BYTES);
        when(evidenceBuilder.createRetrievalNonRetrievalByRecipient(
                any(boolean.class), nullable(EventReasonType.class), any(), any()
        )).thenReturn(STUB_EVIDENCE_BYTES);
    }

    private ConnectorMessage submissionReadyMessage() {
        var base = MessageTestFixtures.createValidOutboundBusinessMessage();
        var as4 = base.as4Properties().toBuilder()
                      .ebmsMessageIdentifier("urn:test:ebms")
                      .originalSender("sender@domain")
                      .finalRecipient("recipient@domain")
                      .build();
        return base.toBuilder().as4Properties(as4).build();
    }

    @Test
    void submission_acceptance_calls_builder_with_acceptance_and_builds_result() throws Exception {
        var message = submissionReadyMessage().toBuilder()
                              .businessContent(MessageContentTestFixtures.createContent())
                              .build();
        when(hashValueBuilder.buildHashValueAsString(any(byte[].class))).thenReturn(HASH_HEX);

        var evidence = toolkit.create(message, ConnectorEvidenceType.SUBMISSION_ACCEPTANCE, null);

        assertThat(evidence.type()).isEqualTo(ConnectorEvidenceType.SUBMISSION_ACCEPTANCE);
        assertThat(evidence.attachment()).isNotNull();
        verify(fileStorageProvider).save(eq(evidence.attachment()), eq(STUB_EVIDENCE_BYTES));

        var detailsCaptor = ArgumentCaptor.forClass(ECodexMessageDetails.class);
        verify(evidenceBuilder).createSubmissionAcceptanceRejection(
                eq(true), nullable(EventReasonType.class), any(), detailsCaptor.capture()
        );
        var details = detailsCaptor.getValue();
        assertThat(details.getNationalMessageId()).isEqualTo(message.backendMessageIdentifier());
        assertThat(details.getEbmsMessageId()).isEqualTo("urn:test:ebms");
        assertThat(details.getSenderAddress()).isEqualTo("sender@domain");
        assertThat(details.getRecipientAddress()).isEqualTo("recipient@domain");
        assertThat(details.getHashAlgorithm()).isEqualTo("SHA-256");
        assertThat(details.getHashValue()).containsExactly((byte) 0xab, (byte) 0xab);
    }

    @Test
    void submission_acceptance_without_xml_payload_omits_hash_value() throws Exception {
        var message = submissionReadyMessage();

        var evidence = toolkit.create(message, ConnectorEvidenceType.SUBMISSION_ACCEPTANCE, null);

        assertThat(evidence.attachment()).isNotNull();
        verify(fileStorageProvider).save(eq(evidence.attachment()), eq(STUB_EVIDENCE_BYTES));
        var detailsCaptor = ArgumentCaptor.forClass(ECodexMessageDetails.class);
        verify(evidenceBuilder).createSubmissionAcceptanceRejection(
                eq(true), nullable(EventReasonType.class), any(), detailsCaptor.capture()
        );
        assertThat(detailsCaptor.getValue().getHashValue()).isNull();
    }

    @Test
    void submission_rejection_maps_reason_to_event_and_calls_rejection_builder() throws Exception {
        var message = submissionReadyMessage();
        var detailsCaptor = ArgumentCaptor.forClass(ECodexMessageDetails.class);
        var reasonCaptor = ArgumentCaptor.forClass(EventReasonType.class);

        toolkit.create(message, ConnectorEvidenceType.SUBMISSION_REJECTION,
                ConnectorMessageRejectionReason.BACKEND_REJECTION);

        verify(evidenceBuilder).createSubmissionAcceptanceRejection(
                eq(false), reasonCaptor.capture(), any(), detailsCaptor.capture()
        );
        assertThat(reasonCaptor.getValue().getCode())
                .isEqualTo(ConnectorMessageRejectionReason.BACKEND_REJECTION.getErrorCode());
        assertThat(reasonCaptor.getValue().getDetails())
                .isEqualTo(ConnectorMessageRejectionReason.BACKEND_REJECTION.getReason());
    }

    @Test
    void submission_rejection_uses_enum_name_when_reason_text_is_blank() throws Exception {
        var message = submissionReadyMessage();
        var reasonCaptor = ArgumentCaptor.forClass(EventReasonType.class);

        toolkit.create(message, ConnectorEvidenceType.SUBMISSION_REJECTION,
                ConnectorMessageRejectionReason.UNSPECIFIC_PROCESSING_ERROR);

        verify(evidenceBuilder).createSubmissionAcceptanceRejection(
                eq(false), reasonCaptor.capture(), any(), any()
        );
        assertThat(reasonCaptor.getValue().getDetails())
                .isEqualTo(ConnectorMessageRejectionReason.UNSPECIFIC_PROCESSING_ERROR.name());
    }

    @ParameterizedTest
    @MethodSource("rejectionEvidenceTypes")
    void rejection_evidence_requires_non_null_reason(ConnectorEvidenceType type) throws Exception {
        var message = messageWithPrior(ConnectorEvidenceType.SUBMISSION_ACCEPTANCE);

        assertThatThrownBy(() -> toolkit.create(message, type, null))
                .isInstanceOf(ConnectorEvidenceException.class)
                .hasMessageContaining("rejectionReason may not be null");
    }

    static Stream<Arguments> rejectionEvidenceTypes() {
        return Stream.of(
                Arguments.of(ConnectorEvidenceType.SUBMISSION_REJECTION),
                Arguments.of(ConnectorEvidenceType.RELAY_REMMD_REJECTION),
                Arguments.of(ConnectorEvidenceType.RELAY_REMMD_FAILURE),
                Arguments.of(ConnectorEvidenceType.NON_DELIVERY),
                Arguments.of(ConnectorEvidenceType.NON_RETRIEVAL)
        );
    }

    @Test
    void submission_evidence_requires_non_blank_national_message_id() throws Exception {
        var message = submissionReadyMessage().toBuilder().backendMessageIdentifier("  ").build();

        assertThatThrownBy(
                () -> toolkit.create(message, ConnectorEvidenceType.SUBMISSION_ACCEPTANCE, null))
                .isInstanceOf(ConnectorEvidenceException.class)
                .hasMessageContaining("nationalMessageId");
    }

    @Test
    void submission_evidence_requires_non_blank_final_recipient() throws Exception {
        var as4 = submissionReadyMessage().as4Properties().toBuilder().finalRecipient("").build();
        var message = submissionReadyMessage().toBuilder().as4Properties(as4).build();

        assertThatThrownBy(
                () -> toolkit.create(message, ConnectorEvidenceType.SUBMISSION_ACCEPTANCE, null))
                .isInstanceOf(ConnectorEvidenceException.class)
                .hasMessageContaining("finalRecipient");
    }

    @Test
    void submission_evidence_requires_non_blank_original_sender() throws Exception {
        var as4 = submissionReadyMessage().as4Properties().toBuilder().originalSender(null).build();
        var message = submissionReadyMessage().toBuilder().as4Properties(as4).build();

        assertThatThrownBy(
                () -> toolkit.create(message, ConnectorEvidenceType.SUBMISSION_ACCEPTANCE, null))
                .isInstanceOf(ConnectorEvidenceException.class)
                .hasMessageContaining("originalSender");
    }

    @Test
    void hash_builder_failure_is_wrapped() throws Exception {
        var xmlContent = "<?xml version=\"1.0\"?><root/>";
        var xmlAttachment = MessageAttachmentTestFixtures.createBusinessContentAttachment().toBuilder()
                                                          .identifier("hash-fail-xml-id")
                                                          .build();
        var businessContent = ConnectorMessageBusinessContent.builder().xmlContent(xmlAttachment).build();
        var messageWithXml = submissionReadyMessage().toBuilder().businessContent(businessContent).build();

        when(fileStorageProvider.findByIdentifier("hash-fail-xml-id"))
                .thenReturn(xmlContent.getBytes(StandardCharsets.UTF_8));
        when(hashValueBuilder.buildHashValueAsString(xmlContent.getBytes(StandardCharsets.UTF_8)))
                .thenThrow(new IllegalStateException("hash boom"));

        assertThatThrownBy(() -> toolkit.create(messageWithXml, ConnectorEvidenceType.SUBMISSION_ACCEPTANCE, null))
                .isInstanceOf(ConnectorEvidenceException.class)
                .hasMessageContaining("could not build payload hash");
    }

    @Test
    void evidence_builder_exception_is_wrapped() throws Exception {
        var message = submissionReadyMessage();
        when(evidenceBuilder.createSubmissionAcceptanceRejection(
                eq(true), nullable(EventReasonType.class), any(), any()
        )).thenThrow(new ECodexEvidenceBuilderException("x"));

        assertThatThrownBy(() -> toolkit.create(message, ConnectorEvidenceType.SUBMISSION_ACCEPTANCE, null))
                .isInstanceOf(ConnectorEvidenceException.class)
                .hasMessageContaining("evidence could not be created")
                .hasCauseInstanceOf(ECodexEvidenceBuilderException.class);
    }

    @ParameterizedTest
    @MethodSource("missingPredecessorCases")
    void missing_predecessor_evidence_throws(ConnectorEvidenceType toCreate, ConnectorEvidenceType present) throws Exception {
        var message = messageWithPrior(present);

        assertThatThrownBy(() -> toolkit.create(
                message,
                toCreate,
                rejectionReasonIfNeeded(toCreate)
        ))
                .isInstanceOf(ConnectorEvidenceException.class)
                .hasMessageContaining("no prior evidence of type");
    }

    static Stream<Arguments> missingPredecessorCases() {
        return Stream.of(
                Arguments.of(ConnectorEvidenceType.RELAY_REMMD_ACCEPTANCE,
                        ConnectorEvidenceType.DELIVERY),
                Arguments.of(ConnectorEvidenceType.DELIVERY,
                        ConnectorEvidenceType.SUBMISSION_ACCEPTANCE),
                Arguments.of(ConnectorEvidenceType.RETRIEVAL,
                        ConnectorEvidenceType.RELAY_REMMD_ACCEPTANCE),
                Arguments.of(ConnectorEvidenceType.RELAY_REMMD_FAILURE,
                        ConnectorEvidenceType.DELIVERY)
        );
    }

    private static ConnectorMessageRejectionReason rejectionReasonIfNeeded(ConnectorEvidenceType type) {
        return switch (type) {
            case SUBMISSION_REJECTION, RELAY_REMMD_REJECTION, RELAY_REMMD_FAILURE,
                    NON_DELIVERY, NON_RETRIEVAL -> ConnectorMessageRejectionReason.OTHER;
            default -> null;
        };
    }

    @Test
    void null_evidences_list_throws_when_prior_required() throws Exception {
        var base = submissionReadyMessage();
        var message = base.toBuilder().evidences(null).build();

        assertThatThrownBy(
                () -> toolkit.create(message, ConnectorEvidenceType.RELAY_REMMD_ACCEPTANCE, null))
                .isInstanceOf(ConnectorEvidenceException.class)
                .hasMessageContaining("no prior evidence");
    }

    @Test
    void relay_remmd_acceptance_uses_submission_acceptance_predecessor() throws Exception {
        var message = messageWithPrior(ConnectorEvidenceType.SUBMISSION_ACCEPTANCE);

        toolkit.create(message, ConnectorEvidenceType.RELAY_REMMD_ACCEPTANCE, null);

        verify(evidenceBuilder).createRelayREMMDAcceptanceRejection(
                eq(true), nullable(EventReasonType.class), any(), eq(PREV)
        );
    }

    @Test
    void delivery_uses_relay_acceptance_predecessor() throws Exception {
        var message = messageWithPrior(ConnectorEvidenceType.RELAY_REMMD_ACCEPTANCE);

        toolkit.create(message, ConnectorEvidenceType.DELIVERY, null);

        verify(evidenceBuilder).createDeliveryNonDeliveryToRecipient(
                eq(true), nullable(EventReasonType.class), any(), eq(PREV)
        );
    }

    @Test
    void retrieval_uses_delivery_predecessor() throws Exception {
        var message = messageWithPrior(ConnectorEvidenceType.DELIVERY);

        toolkit.create(message, ConnectorEvidenceType.RETRIEVAL, null);

        verify(evidenceBuilder).createRetrievalNonRetrievalByRecipient(
                eq(true), nullable(EventReasonType.class), any(), eq(PREV)
        );
    }

    @Test
    void non_delivery_passes_mapped_rejection_to_builder() throws Exception {
        var message = messageWithPrior(ConnectorEvidenceType.RELAY_REMMD_ACCEPTANCE);
        var reasonCaptor = ArgumentCaptor.forClass(EventReasonType.class);

        toolkit.create(message, ConnectorEvidenceType.NON_DELIVERY,
                ConnectorMessageRejectionReason.UNREACHABLE);

        verify(evidenceBuilder).createDeliveryNonDeliveryToRecipient(
                eq(false), reasonCaptor.capture(), any(), eq(PREV)
        );
        assertThat(reasonCaptor.getValue().getCode()).isEqualTo("E404");
    }

    @Test
    void non_retrieval_passes_mapped_rejection_to_builder() throws Exception {
        var message = messageWithPrior(ConnectorEvidenceType.DELIVERY);
        var reasonCaptor = ArgumentCaptor.forClass(EventReasonType.class);

        toolkit.create(message, ConnectorEvidenceType.NON_RETRIEVAL,
                ConnectorMessageRejectionReason.DELIVERY_EVIDENCE_TIMEOUT);

        verify(evidenceBuilder).createRetrievalNonRetrievalByRecipient(
                eq(false), reasonCaptor.capture(), any(), eq(PREV)
        );
        assertThat(reasonCaptor.getValue().getCode()).isEqualTo("E300");
    }

    @Test
    void relay_remmd_rejection_maps_reason_and_uses_submission_predecessor() throws Exception {
        var message = messageWithPrior(ConnectorEvidenceType.SUBMISSION_ACCEPTANCE);
        var reasonCaptor = ArgumentCaptor.forClass(EventReasonType.class);

        toolkit.create(message, ConnectorEvidenceType.RELAY_REMMD_REJECTION,
                ConnectorMessageRejectionReason.GW_REJECTION);

        verify(evidenceBuilder).createRelayREMMDAcceptanceRejection(
                eq(false), reasonCaptor.capture(), any(), eq(PREV)
        );
        assertThat(reasonCaptor.getValue().getCode()).isEqualTo("E201");
    }

    @Test
    void relay_remmd_failure_calls_failure_builder() throws Exception {
        var message = messageWithPrior(ConnectorEvidenceType.SUBMISSION_ACCEPTANCE);
        var reasonCaptor = ArgumentCaptor.forClass(EventReasonType.class);

        toolkit.create(message, ConnectorEvidenceType.RELAY_REMMD_FAILURE,
                ConnectorMessageRejectionReason.RELAY_REMMD_TIMEOUT);

        verify(evidenceBuilder).createRelayREMMDFailure(reasonCaptor.capture(), any(), eq(PREV));
        assertThat(reasonCaptor.getValue().getCode()).isEqualTo("E301");
    }

    @Test
    void skips_evidence_entries_with_null_content_when_resolving_predecessor() throws Exception {
        var emptyEntry = ConnectorMessageEvidence.builder()
                                        .type(ConnectorEvidenceType.SUBMISSION_ACCEPTANCE)
                                        .attachment(null)
                                        .build();
        var good = EvidenceTestFixtures.createSubmissionAcceptanceEvidence();
        var evidences = new ArrayList<ConnectorMessageEvidence>();
        evidences.add(emptyEntry);
        evidences.add(good);
        var message = submissionReadyMessage().toBuilder().evidences(evidences).build();

        toolkit.create(message, ConnectorEvidenceType.RELAY_REMMD_ACCEPTANCE, null);

        verify(evidenceBuilder).createRelayREMMDAcceptanceRejection(
                eq(true), nullable(EventReasonType.class), any(), eq(PREV)
        );
    }

    private ConnectorMessage messageWithPrior(ConnectorEvidenceType priorType) {
        var attachment = MessageAttachmentTestFixtures.createEvidenceAttachment().toBuilder()
                                       .identifier(PRIOR_EVIDENCE_ATTACHMENT_ID)
                                       .build();
        var evidence = ConnectorMessageEvidence.builder()
                                  .type(priorType)
                                  .attachment(attachment)
                                  .build();
        return submissionReadyMessage().toBuilder()
                       .evidences(List.of(evidence))
                       .build();
    }
}
