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

import static org.assertj.core.api.Assertions.assertThatNullPointerException;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import eu.ecodex.connector.application.exception.message.ConnectorMessageTransportStepNotFoundException;
import eu.ecodex.connector.application.port.spi.message.ConnectorMessageTransportStepRepository;
import eu.ecodex.connector.domain.model.message.transport.ConnectorMessageTransportStep;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("ConnectorRetrieveTransportStepByTransportIdService")
public class ConnectorRetrieveTransportStepByTransportIdServiceTest {
    private static final String TRANSPORT_ID = "6e3320bb-6724-4387-822c-a2914dba559a";

    @Mock
    private ConnectorMessageTransportStepRepository transportStepRepository;

    @InjectMocks
    private ConnectorRetrieveTransportStepByTransportIdService retrieveService;

    @Nested
    @DisplayName("when the transport step exists")
    class WhenTheTransportStepExists {

        @Test
        void should_return_the_step_found_by_the_repository() {
            var expected = mock(ConnectorMessageTransportStep.class);
            when(transportStepRepository.findByIdentifier(TRANSPORT_ID)).thenReturn(expected);

            var result = retrieveService.execute(TRANSPORT_ID);

            assertThat(result).isSameAs(expected);
        }
    }

    @Nested
    @DisplayName("when the transport step does not exist")
    class WhenTheTransportStepDoesNotExist {
        @Test
        void should_fail_with_a_not_found_exception_mentioning_the_identifier() {
            when(transportStepRepository.findByIdentifier(TRANSPORT_ID)).thenReturn(null);

            assertThatExceptionOfType(ConnectorMessageTransportStepNotFoundException.class)
                .isThrownBy(() -> retrieveService.execute(TRANSPORT_ID))
                .withMessage("No transport step found with identifier %s".formatted(TRANSPORT_ID));
        }
    }

    @Nested
    @SuppressWarnings("DataFlowIssue")
    @DisplayName("when the identifier is invalid")
    class WhenTheIdentifierIsInvalid {
        @Test
        void should_fail_when_the_identifier_is_null() {
            assertThatNullPointerException()
                .isThrownBy(() -> retrieveService.execute(null));

            verifyNoInteractions(transportStepRepository);
        }
    }
}
