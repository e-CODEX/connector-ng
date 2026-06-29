/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.service.message.transport;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import eu.ecodex.connector.TransportStepFixtures;
import eu.ecodex.connector.application.service.impl.message.transport.ConnectorRetrieveTransportStepService;
import eu.ecodex.connector.domain.exception.ConnectorMessageTransportStepNotFoundException;
import eu.ecodex.connector.domain.model.message.transport.ConnectorMessageTransportStatus;
import eu.ecodex.connector.domain.spi.message.ConnectorMessageTransportStepRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@SuppressWarnings("DataFlowIssue")
@ExtendWith(MockitoExtension.class)
public class ConnectorRetrieveTransportStepServiceTest {
    private static final String MESSAGE_ID = "223caef9-cae9-4387-a38c-ad4879f94b4e@connector.ecodex.eu";

    @Mock
    private ConnectorMessageTransportStepRepository transportStepRepository;

    @InjectMocks
    private ConnectorRetrieveTransportStepService retrieveTransportStepService;

    @Test
    void should_throw_exception_if_message_identifier_is_null() {
        assertThrows(
                NullPointerException.class, () -> retrieveTransportStepService.execute(null)
        );
    }

    @Test
    void should_throw_exception_if_message_transport_step_is_not_found() {
        when(transportStepRepository.findByMessageIdentifierOrRemoteSystemId(any())).thenReturn(null);

        assertThrows(
                ConnectorMessageTransportStepNotFoundException.class,
                () -> retrieveTransportStepService.execute(MESSAGE_ID)
        );
    }

    @Test
    void should_retrieve_message_transport_step_successfully() {
        when(transportStepRepository.findByMessageIdentifierOrRemoteSystemId(MESSAGE_ID))
                .thenReturn(TransportStepFixtures.createTransportStep());

        var transportStep = retrieveTransportStepService.execute(MESSAGE_ID);

        assertThat(transportStep).isNotNull();
        assertThat(transportStep.transportedMessageIdentifier()).isEqualTo(MESSAGE_ID);
        assertThat(transportStep.status()).isEqualTo(ConnectorMessageTransportStatus.SUBMITTED);
        assertThat(transportStep.statuses()).isNotEmpty();
        assertThat(transportStep.numberOfAttempts()).isEqualTo(0);
    }
}
