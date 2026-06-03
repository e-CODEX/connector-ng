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

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import eu.ecodex.connector.EvidenceContextConfiguration;
import eu.ecodex.connector.MessageTestFixtures;
import eu.ecodex.connector.domain.api.ConnectorEvidenceToolkit;
import eu.ecodex.connector.domain.model.ConnectorMessageRejectionReason;
import eu.ecodex.connector.domain.model.message.ConnectorMessage;
import eu.ecodex.connector.domain.model.message.attachment.ConnectorMessageAttachment;
import eu.ecodex.connector.domain.model.message.evidence.ConnectorEvidenceType;
import eu.ecodex.connector.domain.model.message.evidence.ConnectorMessageEvidence;
import eu.ecodex.connector.domain.spi.ConnectorFileStorageProvider;
import eu.ecodex.connector.domain.spi.message.ConnectorMessageAttachmentRepository;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles({"evidence", "dss"})
@SpringBootTest(classes = EvidenceContextConfiguration.class)
@Import(ConnectorEvidenceToolkitImplTest.MockAttachmentBeans.class)
class ConnectorEvidenceToolkitImplTest {
    private final Map<String, byte[]> storedContent = new ConcurrentHashMap<>();
    @Autowired
    private ConnectorEvidenceToolkit evidenceToolkit;
    @Autowired
    private ConnectorMessageAttachmentRepository attachmentRepository;
    @Autowired
    private ConnectorFileStorageProvider fileStorageProvider;

    @BeforeEach
    void wireAttachmentStorage() {
        Mockito.reset(attachmentRepository, fileStorageProvider);
        storedContent.clear();
        when(attachmentRepository.save(any(ConnectorMessageAttachment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(fileStorageProvider.save(any(ConnectorMessageAttachment.class), any(byte[].class)))
                .thenAnswer(invocation -> {
                    var attachment = invocation.<ConnectorMessageAttachment>getArgument(0);
                    var bytes = invocation.getArgument(1, byte[].class);
                    storedContent.put(attachment.identifier(), bytes);
                    return attachment.identifier();
                });
        when(fileStorageProvider.findByIdentifier(any()))
                .thenAnswer(invocation -> storedContent.get(invocation.getArgument(0)));
    }

    private byte[] evidenceBytes(ConnectorMessageEvidence evidence) {
        return fileStorageProvider.findByIdentifier(evidence.attachment().identifier());
    }

    @Test
    void submission_acceptance_contains_enveloped_signature_bytes() {
        var base = MessageTestFixtures.createValidOutboundBusinessMessage();
        var as4 = base.as4Properties().toBuilder()
                      .ebmsMessageIdentifier("urn:test:ebms:001")
                      .build();
        ConnectorMessage message = base.toBuilder().as4Properties(as4).build();

        var evidence = evidenceToolkit.create(
                message,
                ConnectorEvidenceType.SUBMISSION_ACCEPTANCE,
                null
        );

        byte[] bytes = evidenceBytes(evidence);
        assertThat(bytes).isNotNull();
        assertThat(bytes.length).isPositive();
        String asUtf8 = new String(bytes, StandardCharsets.UTF_8);
        assertThat(asUtf8).contains("ds:Signature");
    }

    @Test
    void submission_rejection_contains_enveloped_signature_bytes() {
        var base = MessageTestFixtures.createValidOutboundBusinessMessage();
        var as4 = base.as4Properties().toBuilder()
                      .ebmsMessageIdentifier("urn:test:ebms:002")
                      .build();
        ConnectorMessage message = base.toBuilder().as4Properties(as4).build();

        var evidence = evidenceToolkit.create(
                message,
                ConnectorEvidenceType.SUBMISSION_REJECTION,
                ConnectorMessageRejectionReason.BACKEND_REJECTION
        );

        byte[] bytes = evidenceBytes(evidence);
        assertThat(bytes).isNotNull();
        assertThat(bytes.length).isPositive();
        assertThat(new String(bytes, StandardCharsets.UTF_8)).contains("ds:Signature");
    }

    @Test
    void each_step_in_submission_relay_delivery_retrieval_chain_produces_signed_evidence() {
        var base = MessageTestFixtures.createValidOutboundBusinessMessage();
        var as4 = base.as4Properties().toBuilder()
                      .ebmsMessageIdentifier("urn:test:ebms:chain")
                      .build();
        var message = base.toBuilder().as4Properties(as4).build();

        var chain = new ArrayList<ConnectorMessageEvidence>();
        for (var step : List.of(
                ConnectorEvidenceType.SUBMISSION_ACCEPTANCE,
                ConnectorEvidenceType.RELAY_REMMD_ACCEPTANCE,
                ConnectorEvidenceType.DELIVERY,
                ConnectorEvidenceType.RETRIEVAL
        )) {
            var withPrior = message.toBuilder().evidences(new ArrayList<>(chain)).build();
            var next = evidenceToolkit.create(withPrior, step, null);
            assertThat(next.type()).isEqualTo(step);
            byte[] bytes = evidenceBytes(next);
            assertThat(bytes).isNotEmpty();
            assertThat(new String(bytes, StandardCharsets.UTF_8)).contains("ds:Signature");
            chain.add(next);
        }
    }

    @Test
    void non_delivery_after_relay_chain_contains_enveloped_signature_bytes() {
        var base = MessageTestFixtures.createValidOutboundBusinessMessage();
        var as4 = base.as4Properties().toBuilder()
                      .ebmsMessageIdentifier("urn:test:ebms:reject-chain")
                      .build();
        var message = base.toBuilder().as4Properties(as4).build();

        var chain = new ArrayList<ConnectorMessageEvidence>();
        for (var step : List.of(
                ConnectorEvidenceType.SUBMISSION_ACCEPTANCE,
                ConnectorEvidenceType.RELAY_REMMD_ACCEPTANCE
        )) {
            var withPrior = message.toBuilder().evidences(new ArrayList<>(chain)).build();
            chain.add(evidenceToolkit.create(withPrior, step, null));
        }

        var withPrior = message.toBuilder().evidences(new ArrayList<>(chain)).build();
        var evidence = evidenceToolkit.create(
                withPrior,
                ConnectorEvidenceType.NON_DELIVERY,
                ConnectorMessageRejectionReason.UNREACHABLE
        );

        assertThat(evidence.type()).isEqualTo(ConnectorEvidenceType.NON_DELIVERY);
        assertThat(new String(evidenceBytes(evidence), StandardCharsets.UTF_8)).contains(
                "ds:Signature");
    }

    @TestConfiguration
    static class MockAttachmentBeans {
        @Bean
        @Primary
        ConnectorMessageAttachmentRepository attachmentRepository() {
            return Mockito.mock(ConnectorMessageAttachmentRepository.class);
        }

        @Bean
        @Primary
        ConnectorFileStorageProvider fileStorageProvider() {
            return Mockito.mock(ConnectorFileStorageProvider.class);
        }
    }
}
