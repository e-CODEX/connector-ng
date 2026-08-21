/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.service.message.outbound.pipeline.step;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import eu.ecodex.connector.BusinessMessageTestFixtures;
import eu.ecodex.connector.EvidenceMessageTestFixtures;
import eu.ecodex.connector.EvidenceTestFixtures;
import eu.ecodex.connector.application.port.api.message.ConnectorEvidenceMessageCreator;
import eu.ecodex.connector.domain.model.message.ConnectorMessageDirection;
import java.util.Collections;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for the {@code ConnectorOutboundMessageConfirmationStep}.
 */
@SuppressWarnings("DataFlowIssue")

@ExtendWith(MockitoExtension.class)
@DisplayName("ConnectorOutboundMessageConfirmationStep")
public class ConnectorOutboundMessageConfirmationStepTest {
    @Mock
    private ConnectorEvidenceMessageCreator messageCreator;

    @InjectMocks
    private ConnectorOutboundMessageConfirmationStep confirmationStep;

    @Nested
    @DisplayName("when confirmation succeeds")
    class WhenConfirmationSucceeds {
        @Test
        void should_create_the_confirmation_and_switch_the_direction() {
            var outboundMessage = BusinessMessageTestFixtures
                .createOutboundMessage()
                .toBuilder()
                .evidences(Collections.singletonList(
                    EvidenceTestFixtures.createSubmissionAcceptanceEvidence()))
                .transportedEvidences(Collections.singletonList(
                    EvidenceTestFixtures.createSubmissionAcceptanceEvidence()))
                .build();

            when(messageCreator.create(any(), any()))
                .thenReturn(EvidenceMessageTestFixtures.createSubmissionAcceptanceEvidenceMessage());

            var outputMessage = confirmationStep.execute(outboundMessage);

            assertThat(outputMessage).isNotNull();
            assertThat(outputMessage.direction()).isNotEqualTo(outboundMessage.direction());
            assertThat(outputMessage.direction())
                .isEqualTo(ConnectorMessageDirection.GATEWAY_TO_BACKEND);
        }
    }

    @Nested
    @DisplayName("when execution fails")
    class WhenExecutionFails {
        @Test
        void should_fail_when_the_transported_evidences_are_null() {
            var outboundMessage = BusinessMessageTestFixtures.createOutboundMessage()
                                                             .toBuilder()
                                                             .transportedEvidences(null)
                                                             .build();

            assertThrows(
                IllegalStateException.class,
                () -> confirmationStep.execute(outboundMessage)
            );
        }

        @Test
        void should_fail_when_the_transported_evidences_are_empty() {
            var outboundMessage = BusinessMessageTestFixtures.createOutboundMessage()
                                                             .toBuilder()
                                                             .evidences(Collections.emptyList())
                                                             .transportedEvidences(Collections.emptyList())
                                                             .build();

            assertThrows(
                IllegalStateException.class,
                () -> confirmationStep.execute(outboundMessage)
            );
        }

        @Test
        void should_fail_when_the_message_is_null() {
            assertThrows(
                NullPointerException.class,
                () -> confirmationStep.execute(null)
            );
        }
    }
}
