package eu.ecodex.connector.infrastructure.messaging.listener;


import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import eu.ecodex.connector.application.service.usecase.transport.ConnectorAckMessageTransportStep;
import eu.ecodex.connector.domain.model.message.transport.ConnectorMessageTransportStep;
import eu.ecodex.connector.domain.spi.message.ConnectorMessageTransportStepRepository;
import eu.ecodex.connector.infrastructure.messaging.BaseJmsMessageTest;
import eu.ecodex.connector.infrastructure.messaging.listener.inbound.ConnectorGatewayMessageAcknowledgementListener;
import jakarta.jms.JMSException;
import jakarta.jms.MapMessage;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

@SuppressWarnings("DataFlowIssue")
public class ConnectorGatewayMessageAcknowledgementListenerTest extends BaseJmsMessageTest {
    private static final String MESSAGE_ID = "msg-001";

    @Mock
    private ConnectorMessageTransportStepRepository transportStepRepository;
    @Mock
    private ConnectorAckMessageTransportStep acknowledgeMessageTransportStep;
    @Mock
    private MapMessage mapMessage;

    @InjectMocks
    private ConnectorGatewayMessageAcknowledgementListener listener;

    @Test
    void should_throw_null_pointer_exception_if_the_jms_message_is_null() {
        assertThatThrownBy(() -> listener.handle(null))
                .isInstanceOf(NullPointerException.class);

        verifyNoInteractions(transportStepRepository, acknowledgeMessageTransportStep);
    }

    @Test
    void should_throw_exception_if_the_connector_message_identifier_is_null() throws JMSException {
        when(mapMessage.getStringProperty("messageId")).thenReturn(null);

        assertThatThrownBy(() -> listener.handle(mapMessage))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Message identifier not found");

        verifyNoInteractions(transportStepRepository, acknowledgeMessageTransportStep);
    }

    @Test
    void should_handle_message_submission_to_gateway_reply_successfully() throws JMSException {
        when(mapMessage.getStringProperty("messageId")).thenReturn(MESSAGE_ID);
        when(transportStepRepository.findByMessageIdentifierOrRemoteSystemId(any()))
                .thenReturn(ConnectorMessageTransportStep.builder().build());
        doNothing().when(acknowledgeMessageTransportStep).execute(any(), any());

        listener.handle(mapMessage);

        verify(transportStepRepository).findByMessageIdentifierOrRemoteSystemId(MESSAGE_ID);
        verify(acknowledgeMessageTransportStep).execute(any(), any());
    }

    @Test
    void should_throw_exception_if_the_broker_is_unavailable() throws JMSException {
        when(mapMessage.getStringProperty("messageId"))
                .thenThrow(new JMSException("broker unavailable"));

        assertThatThrownBy(() -> listener.handle(mapMessage))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Failed to parse Domibus reply")
                .hasCauseInstanceOf(JMSException.class);

        verifyNoInteractions(transportStepRepository, acknowledgeMessageTransportStep);
    }
}
