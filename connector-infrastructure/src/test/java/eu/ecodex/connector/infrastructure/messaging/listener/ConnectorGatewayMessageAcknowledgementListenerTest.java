package eu.ecodex.connector.infrastructure.messaging.listener;


import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import eu.ecodex.connector.domain.spi.message.ConnectorMessageRepository;
import eu.ecodex.connector.infrastructure.messaging.BaseJmsMessageTest;
import jakarta.jms.JMSException;
import jakarta.jms.MapMessage;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

@SuppressWarnings("DataFlowIssue")
public class ConnectorGatewayMessageAcknowledgementListenerTest extends BaseJmsMessageTest {
    @Mock
    private ConnectorMessageRepository messageRepository;
    @Mock
    private MapMessage mapMessage;
    @InjectMocks
    private ConnectorGatewayMessageAcknowledgementListener listener;

    @Test
    void should_handle_message_submission_to_gateway_reply_successfully() throws JMSException {
        when(mapMessage.getStringProperty("messageId")).thenReturn("msg-001");

        listener.handle(mapMessage);

        verify(messageRepository).setDeliveredToGatewayAt("msg-001");
        verifyNoMoreInteractions(messageRepository);
    }

    @Test
    void should_throw_null_pointer_exception_if_the_jms_message_is_null() {
        assertThatThrownBy(() -> listener.handle(null))
                .isInstanceOf(NullPointerException.class);

        verifyNoInteractions(messageRepository);
    }

    @Test
    void should_throw_exception_if_the_connector_message_identifier_is_null() throws JMSException {
        when(mapMessage.getStringProperty("messageId")).thenReturn(null);

        assertThatThrownBy(() -> listener.handle(mapMessage))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Message identifier not found");

        verifyNoInteractions(messageRepository);
    }

    @Test
    void should_throw_exception_if_the_broker_is_unavailable() throws JMSException {
        when(mapMessage.getStringProperty("messageId"))
                .thenThrow(new JMSException("broker unavailable"));

        assertThatThrownBy(() -> listener.handle(mapMessage))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Failed to parse Domibus reply")
                .hasCauseInstanceOf(JMSException.class);

        verifyNoInteractions(messageRepository);
    }
}
