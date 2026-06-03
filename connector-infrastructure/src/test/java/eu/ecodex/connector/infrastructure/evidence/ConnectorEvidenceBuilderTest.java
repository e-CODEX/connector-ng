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

import static org.assertj.core.api.AssertionsForClassTypes.assertThatNoException;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import eu.ecodex.connector.FileTestFixtures;
import eu.ecodex.connector.infrastructure.dss.ConnectorDssDocumentSigner;
import eu.ecodex.connector.infrastructure.evidence.builder.ConnectorEvidenceBuilder;
import eu.ecodex.connector.infrastructure.evidence.builder.ConnectorRemEvidenceBuilder;
import eu.ecodex.connector.infrastructure.evidence.exception.ConnectorEvidenceBuilderException;
import eu.ecodex.connector.infrastructure.evidence.model.ConnectorEvidenceMessageDetails;
import eu.ecodex.connector.infrastructure.evidence.spocseu.model.EDeliveryDetails;
import eu.ecodex.connector.infrastructure.property.common.KeystoreProperties;
import eu.ecodex.connector.infrastructure.property.common.KeystoreType;
import eu.ecodex.connector.infrastructure.property.common.PrivateKeyProperties;
import eu.ecodex.connector.infrastructure.property.evidence.ConnectorEvidencesProperties;
import eu.ecodex.connector.infrastructure.property.evidence.EvidencesSignatureProperties;
import eu.europa.esig.dss.enumerations.DigestAlgorithm;
import eu.europa.esig.dss.enumerations.EncryptionAlgorithm;
import eu.europa.esig.dss.model.DSSDocument;
import java.io.IOException;
import java.io.InputStream;
import org.etsi.uri._02640.v2.EventReasonType;
import org.etsi.uri._02640.v2.REMEvidenceType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ConnectorEvidenceBuilderTest {
    private static final byte[] PREVIOUS_EVIDENCE = FileTestFixtures.readAsBytes(
            "evidence/samples/SUBMISSION_ACCEPTANCE.xml");
    private static final byte[] SIGNED_BYTES = "<signed/>".getBytes();
    private static final String EBMS_ID = "ebms-001";
    private static final String NATIONAL_ID = "national-001";
    private static final String SENDER_ADDRESS = "http://sender.example.com";
    private static final String RECIPIENT_ADDRESS = "http://recipient.example.com";

    @Mock
    private ConnectorDssDocumentSigner documentSigner;
    @Mock
    private ConnectorEvidencesProperties evidencesProperties;
    @Mock
    private EvidencesSignatureProperties signatureProperties;

    @BeforeEach
    void stubSignatureProperties() {
        var keystoreProperties = new KeystoreProperties();
        keystoreProperties.setPath("classpath:keystores/connector-keystore.jks");
        keystoreProperties.setPassword("12345");
        keystoreProperties.setType(KeystoreType.JKS);

        var privateKeyProperties = new PrivateKeyProperties();
        privateKeyProperties.setAlias("connector_blue");
        privateKeyProperties.setPassword("12345");

        when(evidencesProperties.getSignature()).thenReturn(signatureProperties);
        when(signatureProperties.getKeystore()).thenReturn(keystoreProperties);
        when(signatureProperties.getPrivateKey()).thenReturn(privateKeyProperties);
    }

    // createSubmissionAcceptanceRejection

    @Test
    void should_create_and_sign_SubmissionAcceptanceRejection_acceptance_evidence_successfully()
            throws Exception {
        var builder = builderWithMockedSigning(null);
        var result = builder.createSubmissionAcceptanceRejection(
                true, null, issuerDetails(), messageDetails()
        );

        assertThat(result).isEqualTo(SIGNED_BYTES);
    }

    @Test
    void should_create_and_sign_SubmissionAcceptanceRejection_rejection_evidence_successfully()
            throws Exception {
        var builder = builderWithMockedSigning(null);

        var result = builder.createSubmissionAcceptanceRejection(
                false, null, issuerDetails(), messageDetails()
        );

        assertThat(result).isEqualTo(SIGNED_BYTES);
    }

    @Test
    void should_create_and_sign_SubmissionAcceptanceRejection_acceptance_evidence_with_event_reason_successfully() {
        var builder = builderWithMockedSigning(null);
        assertThatNoException()
                .isThrownBy(() ->
                                    builder.createSubmissionAcceptanceRejection(
                                            true,
                                            mock(EventReasonType.class),
                                            issuerDetails(),
                                            messageDetails()
                                    )
                );
    }

    // createRelayREMMDAcceptanceRejection

    @Test
    void should_create_and_sign_RelayREMMDAcceptanceRejection_acceptance_evidence_successfully()
            throws Exception {
        var builder = builderWithMockedSigning(previousEvidence());

        var result = builder.createRelayREMMDAcceptanceRejection(
                true,
                null,
                issuerDetails(),
                PREVIOUS_EVIDENCE
        );

        assertThat(result).isEqualTo(SIGNED_BYTES);
    }

    @Test
    void should_create_and_sign_RelayREMMDAcceptanceRejection_rejection_evidence_successfully()
            throws Exception {
        var builder = builderWithMockedSigning(previousEvidence());

        var result = builder.createRelayREMMDAcceptanceRejection(
                false,
                null,
                issuerDetails(),
                PREVIOUS_EVIDENCE
        );

        assertThat(result).isEqualTo(SIGNED_BYTES);
    }

    @Test
    void should_create_and_sign_RelayREMMDAcceptanceRejection_evidence_with_event_reason_successfully() {
        var builder = builderWithMockedSigning(previousEvidence());
        assertThatNoException()
                .isThrownBy(() ->
                                    builder.createRelayREMMDAcceptanceRejection(
                                            true,
                                            mock(EventReasonType.class),
                                            issuerDetails(),
                                            FileTestFixtures.readAsBytes(
                                                    "evidence/samples/SUBMISSION_ACCEPTANCE.xml")
                                    )
                );
    }

    // createRelayREMMDFailure

    @Test
    void should_create_and_sign_RelayREMMDFailure_evidence_successfully() throws Exception {
        var builder = builderWithMockedSigning(previousEvidence());

        var result = builder.createRelayREMMDFailure(
                null,
                issuerDetails(),
                PREVIOUS_EVIDENCE
        );

        assertThat(result).isEqualTo(SIGNED_BYTES);
    }

    @Test
    void should_create_and_sign_RelayREMMDFailure_evidence_with_event_reason_successfully() {
        var builder = builderWithMockedSigning(previousEvidence());

        assertThatNoException()
                .isThrownBy(() ->
                                    builder.createRelayREMMDFailure(
                                            mock(EventReasonType.class),
                                            issuerDetails(),
                                            FileTestFixtures.readAsBytes(
                                                    "evidence/samples/SUBMISSION_ACCEPTANCE.xml")
                                    )
                );
    }

    // createDeliveryNonDeliveryToRecipient

    @Test
    void should_create_and_sign_DeliveryNonDeliveryToRecipient_delivery_evidence_successfully()
            throws Exception {
        var builder = builderWithMockedSigning(previousEvidence());

        var result = builder.createDeliveryNonDeliveryToRecipient(
                true,
                null,
                issuerDetails(),
                PREVIOUS_EVIDENCE
        );

        assertThat(result).isEqualTo(SIGNED_BYTES);
    }

    @Test
    void should_create_and_sign_DeliveryNonDeliveryToRecipient_nonDelivery_evidence_successfully()
            throws Exception {
        var builder = builderWithMockedSigning(previousEvidence());

        var result = builder.createDeliveryNonDeliveryToRecipient(
                false,
                null,
                issuerDetails(),
                PREVIOUS_EVIDENCE
        );

        assertThat(result).isEqualTo(SIGNED_BYTES);
    }

    // createRetrievalNonRetrievalByRecipient

    @Test
    void should_create_and_sign_RetrievalNonRetrievalByRecipient_retrieval_evidence_successfully()
            throws Exception {
        var builder = builderWithMockedSigning(previousEvidence());

        var result = builder.createRetrievalNonRetrievalByRecipient(
                true,
                null,
                issuerDetails(),
                PREVIOUS_EVIDENCE
        );

        assertThat(result).isEqualTo(SIGNED_BYTES);
    }

    @Test
    void should_create_and_sign_RetrievalNonRetrievalByRecipient_nonRetrieval_evidence_successfully()
            throws Exception {
        var builder = builderWithMockedSigning(previousEvidence());

        var result = builder.createRetrievalNonRetrievalByRecipient(
                false,
                null,
                issuerDetails(),
                PREVIOUS_EVIDENCE
        );

        assertThat(result).isEqualTo(SIGNED_BYTES);
    }

    @Test
    void should_throw_exception_if_something_goes_wrong_during_signature() {
        when(signatureProperties.getEncryptionAlgorithm()).thenReturn(EncryptionAlgorithm.RSA);
        when(signatureProperties.getDigestAlgorithm()).thenReturn(DigestAlgorithm.SHA256);

        var brokenStream = new InputStream() {
            @Override
            public int read() throws IOException {
                throw new IOException("stream failure");
            }
        };

        var brokenDocument = mock(DSSDocument.class);
        when(brokenDocument.openStream()).thenReturn(brokenStream);
        when(documentSigner.signWithXAdES(any(), any(), any(), any()))
                .thenReturn(brokenDocument);

        var builder = new ConnectorRemEvidenceBuilder(documentSigner, evidencesProperties) {
            @Override
            protected REMEvidenceType parseSignedEvidence(byte[] bytes) {
                return previousEvidence();
            }
        };

        assertThatThrownBy(
                () -> builder.createRelayREMMDAcceptanceRejection(
                        true,
                        null,
                        issuerDetails(),
                        PREVIOUS_EVIDENCE
                ))
                .isInstanceOf(ConnectorEvidenceBuilderException.class);
    }

    private REMEvidenceType previousEvidence() {
        return mock(REMEvidenceType.class);
    }

    private ConnectorEvidenceBuilder builderWithMockedSigning(REMEvidenceType previousEvidence) {
        return new ConnectorRemEvidenceBuilder(documentSigner, evidencesProperties) {
            @Override
            protected byte[] sign(byte[] unsignedXml) {
                return SIGNED_BYTES;
            }

            @Override
            protected REMEvidenceType parseSignedEvidence(byte[] bytes) {
                return previousEvidence;
            }
        };
    }

    private ConnectorEvidenceMessageDetails messageDetails() {
        var details = mock(ConnectorEvidenceMessageDetails.class);
        when(details.getEbmsMessageId()).thenReturn(EBMS_ID);
        when(details.getNationalMessageId()).thenReturn(NATIONAL_ID);
        when(details.getSenderAddress()).thenReturn(SENDER_ADDRESS);
        when(details.getRecipientAddress()).thenReturn(RECIPIENT_ADDRESS);
        when(details.getHashValue()).thenReturn("hash-value".getBytes());
        when(details.getHashAlgorithm()).thenReturn("SHA-256");

        return details;
    }

    private EDeliveryDetails issuerDetails() {
        return mock(EDeliveryDetails.class);
    }
}
