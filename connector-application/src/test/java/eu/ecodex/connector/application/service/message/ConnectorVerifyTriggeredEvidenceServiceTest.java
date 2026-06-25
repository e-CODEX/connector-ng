/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.service.message;


import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import eu.ecodex.connector.application.service.impl.message.ConnectorVerifyTriggeredEvidenceService;
import eu.ecodex.connector.domain.exception.ConnectorEvidenceException;
import eu.ecodex.connector.domain.exception.ConnectorMessageNotFoundException;
import eu.ecodex.connector.domain.model.message.ConnectorMessage;
import eu.ecodex.connector.domain.model.message.ConnectorMessageAS4Properties;
import eu.ecodex.connector.domain.model.message.ConnectorMessageDirection;
import eu.ecodex.connector.domain.spi.message.ConnectorMessageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ConnectorVerifyTriggeredEvidenceServiceTest {
    private static final String REF_ID = "ref-message-001";
    private static final ConnectorMessageDirection TRIGGER_DIRECTION =
            ConnectorMessageDirection.BACKEND_TO_GATEWAY;

    // --- Test fixtures ---
    private static final ConnectorMessageDirection INVERTED_DIRECTION =
            ConnectorMessageDirection.GATEWAY_TO_BACKEND;

    @Mock
    private ConnectorMessageRepository messageRepository;

    @InjectMocks
    private ConnectorVerifyTriggeredEvidenceService service;

    private ConnectorMessage triggerMessage;
    private ConnectorMessage businessMessage;

    @BeforeEach
    void setUp() {
        triggerMessage = mock(ConnectorMessage.class);
        businessMessage = mock(ConnectorMessage.class);

        var as4Properties = mock(ConnectorMessageAS4Properties.class);
        lenient().when(triggerMessage.as4Properties()).thenReturn(as4Properties);
        lenient().when(as4Properties.referenceToIdentifier()).thenReturn(REF_ID);
        lenient().when(triggerMessage.direction()).thenReturn(TRIGGER_DIRECTION);
        lenient().when(businessMessage.direction()).thenReturn(INVERTED_DIRECTION);
    }

    @Test
    void verify_should_throw_exception_when_both_ref_ids_are_blank() {
        var as4Properties = mock(ConnectorMessageAS4Properties.class);
        when(triggerMessage.as4Properties()).thenReturn(as4Properties);
        when(as4Properties.referenceToIdentifier()).thenReturn("");
        when(triggerMessage.referenceToBackendMessageIdentifier()).thenReturn("  ");

        assertThatThrownBy(() -> service.verify(triggerMessage))
                .isInstanceOf(ConnectorEvidenceException.class)
                .hasMessageContaining("refToMessageId");
    }

    @Test
    void should_fallback_to_backend_message_id_when_as4_ref_id_is_blank() {
        var as4Properties = mock(ConnectorMessageAS4Properties.class);
        when(triggerMessage.as4Properties()).thenReturn(as4Properties);
        when(as4Properties.referenceToIdentifier()).thenReturn("");
        when(triggerMessage.referenceToBackendMessageIdentifier()).thenReturn(REF_ID);
        when(triggerMessage.direction()).thenReturn(TRIGGER_DIRECTION);
        when(messageRepository.findByEbmsMessageIdentifierAndDirection(REF_ID, INVERTED_DIRECTION))
                .thenReturn(businessMessage);

        assertThatNoException().isThrownBy(() -> service.verify(triggerMessage));
    }

    @Test
    void should_throw_exception_when_direction_is_null() {
        when(triggerMessage.direction()).thenReturn(null);

        assertThatThrownBy(() -> service.verify(triggerMessage))
                .isInstanceOf(ConnectorEvidenceException.class)
                .hasMessageContaining("direction");
    }

    @Test
    void should_find_by_ebms_id_first() {
        when(messageRepository.findByEbmsMessageIdentifierAndDirection(REF_ID, INVERTED_DIRECTION))
                .thenReturn(businessMessage);

        assertThatNoException().isThrownBy(() -> service.verify(triggerMessage));

        verify(messageRepository).findByEbmsMessageIdentifierAndDirection(
                REF_ID,
                INVERTED_DIRECTION
        );
        verify(messageRepository, never()).findByBackendMessageIdentifier(any());
        verify(messageRepository, never()).findByIdentifier(any());
    }

    @Test
    void should_find_by_backend_id_when_ebms_returns_null() {
        when(messageRepository.findByEbmsMessageIdentifierAndDirection(REF_ID, INVERTED_DIRECTION))
                .thenReturn(null);
        when(messageRepository.findByBackendMessageIdentifier(REF_ID))
                .thenReturn(businessMessage);

        assertThatNoException().isThrownBy(() -> service.verify(triggerMessage));

        verify(messageRepository).findByBackendMessageIdentifier(REF_ID);
        verify(messageRepository, never()).findByIdentifier(any());
    }

    @Test
    void should_find_by_connector_id_when_ebms_and_backend_return_null() {
        when(messageRepository.findByEbmsMessageIdentifierAndDirection(REF_ID, INVERTED_DIRECTION))
                .thenReturn(null);
        when(messageRepository.findByBackendMessageIdentifier(REF_ID))
                .thenReturn(null);
        when(messageRepository.findByIdentifier(REF_ID))
                .thenReturn(businessMessage);

        assertThatNoException().isThrownBy(() -> service.verify(triggerMessage));

        verify(messageRepository).findByIdentifier(REF_ID);
    }

    @Test
    void should_throw_exception_when_no_message_found_by_any_strategy() {
        when(messageRepository.findByEbmsMessageIdentifierAndDirection(any(), any()))
                .thenReturn(null);
        when(messageRepository.findByBackendMessageIdentifier(any())).thenReturn(null);
        when(messageRepository.findByIdentifier(any())).thenReturn(null);

        assertThatThrownBy(() -> service.verify(triggerMessage))
                .isInstanceOf(ConnectorMessageNotFoundException.class)
                .hasMessageContaining(REF_ID);
    }

    @Test
    void should_succeed_when_business_message_direction_is_gateway_to_backend() {
        when(messageRepository.findByEbmsMessageIdentifierAndDirection(REF_ID, INVERTED_DIRECTION))
                .thenReturn(businessMessage);
        when(businessMessage.direction()).thenReturn(ConnectorMessageDirection.GATEWAY_TO_BACKEND);

        assertThatNoException().isThrownBy(() -> service.verify(triggerMessage));
    }

    @Test
    void should_throw_exception_when_business_message_direction_is_null() {
        when(messageRepository.findByEbmsMessageIdentifierAndDirection(REF_ID, INVERTED_DIRECTION))
                .thenReturn(businessMessage);
        when(businessMessage.direction()).thenReturn(null);

        assertThatThrownBy(() -> service.verify(triggerMessage))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("direction cannot be null");
    }

    @Test
    void should_throw_exception_when_business_message_direction_is_not_gateway_to_backend() {
        when(messageRepository.findByEbmsMessageIdentifierAndDirection(REF_ID, INVERTED_DIRECTION))
                .thenReturn(businessMessage);
        when(businessMessage.direction()).thenReturn(ConnectorMessageDirection.BACKEND_TO_GATEWAY);

        assertThatThrownBy(() -> service.verify(triggerMessage))
                .isInstanceOf(ConnectorEvidenceException.class)
                .hasMessageContaining("GATEWAY_TO_BACKEND")
                .hasMessageContaining("BACKEND_TO_GATEWAY");
    }
}
