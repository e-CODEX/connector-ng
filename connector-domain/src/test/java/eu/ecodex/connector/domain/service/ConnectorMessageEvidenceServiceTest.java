/*
 * Copyright 2025 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.domain.service;

import static org.junit.jupiter.api.Assertions.assertThrows;

import eu.ecodex.connector.MessageTestFixtures;
import eu.ecodex.connector.domain.api.service.ConnectorEvidenceService;
import eu.ecodex.connector.domain.exception.ConnectorEvidenceException;
import eu.ecodex.connector.domain.model.message.ConnectorMessageDirection;
import eu.ecodex.connector.domain.spi.ConnectorMessageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for the {@code ConnectorEvidenceService} implementation.
 */
@SuppressWarnings("DataFlowIssue")
@ExtendWith(MockitoExtension.class)
class ConnectorMessageEvidenceServiceTest {
    @Mock
    private ConnectorMessageRepository messageRepository;
    private ConnectorEvidenceService evidenceService;

    @BeforeEach
    void setUp() {
        var messageService = new ConnectorMessageServiceImpl(
                messageRepository
        );
        this.evidenceService = new ConnectorEvidenceServiceImpl(messageService);
    }

    // isEvidenceTriggeringAllowed
    @Test
    void should_return_true_when_checking_is_evidence_trigger_and_message_is_evidence_trigger() {
        var businessMessage = MessageTestFixtures.createEvidenceTriggerMessage();
        this.evidenceService.isEvidenceTriggeringAllowed(businessMessage);
    }

    @Test
    void should_throw_exception_when_checking_is_evidence_trigger_and_message_is_null() {
        assertThrows(
                NullPointerException.class,
                () -> this.evidenceService.isEvidenceTriggeringAllowed(null)
        );
    }

    @Test
    void should_throw_exception_when_checking_is_evidence_trigger_and_message_is_not_evidence() {
        var businessMessage = MessageTestFixtures.createValidOutboundBusinessMessage();
        assertThrows(
                ConnectorEvidenceException.class,
                () -> this.evidenceService.isEvidenceTriggeringAllowed(businessMessage)
        );
    }

    @Test
    void should_throw_exception_when_checking_is_evidence_trigger_and_message_direction_is_backend() {
        var businessMessage = MessageTestFixtures.createEvidenceTriggerMessage()
                                         .toBuilder()
                                         .direction(ConnectorMessageDirection.GATEWAY_TO_BACKEND)
                                         .build();
        assertThrows(
                ConnectorEvidenceException.class,
                () -> this.evidenceService.isEvidenceTriggeringAllowed(businessMessage)
        );
    }
}
