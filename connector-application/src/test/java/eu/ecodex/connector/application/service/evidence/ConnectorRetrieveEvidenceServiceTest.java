/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.service.evidence;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import eu.ecodex.connector.EvidenceTestFixtures;
import eu.ecodex.connector.application.service.impl.evidence.ConnectorRetrieveEvidenceService;
import eu.ecodex.connector.domain.exception.ConnectorEvidenceNotFoundException;
import eu.ecodex.connector.domain.spi.message.ConnectorMessageEvidenceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@SuppressWarnings("DataFlowIssue")
@ExtendWith(MockitoExtension.class)
public class ConnectorRetrieveEvidenceServiceTest {
    @Mock
    private ConnectorMessageEvidenceRepository evidenceRepository;

    @InjectMocks
    private ConnectorRetrieveEvidenceService evidenceService;

    @Test
    void should_throw_exception_if_evidence_uuid_is_null() {
        assertThrows(
                NullPointerException.class,
                () -> evidenceService.execute(null)
        );
    }

    @Test
    void should_throw_exception_if_evidence_does_not_exist() {
        when(evidenceRepository.findByUuid(any())).thenReturn(null);
        assertThrows(
                ConnectorEvidenceNotFoundException.class,
                () -> evidenceService.execute("non-existing-uuid")
        );
    }

    @Test
    void should_retrieve_evidence_successfully() {
        when(evidenceRepository.findByUuid(any()))
                .thenReturn(EvidenceTestFixtures.createSubmissionAcceptanceEvidence());

        var evidence = evidenceService.execute("12345678-1234-1234-1234-123456789012");

        assertThat(evidence).isNotNull();
        assertThat(evidence.uuid()).isEqualTo("12345678-1234-1234-1234-123456789012");
    }
}
