package eu.ecodex.connector.application.service.message.outbound;

import eu.ecodex.connector.application.port.api.message.ConnectorTriggeredEvidenceMessageVerifier;
import eu.ecodex.connector.application.port.api.message.outbound.ConnectorOutboundEvidenceMessageCommand;
import eu.ecodex.connector.application.port.api.message.outbound.ConnectorOutboundEvidenceMessageReceiver;
import eu.ecodex.connector.application.port.spi.ConnectorMessageEventPublisher;
import eu.ecodex.connector.application.service.message.ConnectorMessageIdGeneratorService;
import eu.ecodex.connector.domain.model.message.ConnectorMessageDirection;
import eu.ecodex.connector.domain.model.message.ConnectorTriggeredEvidenceMessage;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * Service for receiving outbound evidence messages.
 */
@Service
public class ConnectorOutboundEvidenceMessageReceiverService
    implements ConnectorOutboundEvidenceMessageReceiver {
    private final ConnectorMessageIdGeneratorService messageIdGeneratorService;
    private final ConnectorTriggeredEvidenceMessageVerifier triggeredEvidenceMessageVerifier;
    private final ConnectorMessageEventPublisher<ConnectorTriggeredEvidenceMessage>
        evidenceTriggerPublisher;

    /**
     * Constructor for the ConnectorOutboundEvidenceMessageReceiverService.
     *
     * @param messageIdGeneratorService        the service for generating message IDs
     * @param triggeredEvidenceMessageVerifier the service for verifying triggered evidence
     *                                         messages
     * @param evidenceTriggerPublisher         the publisher for evidence trigger messages
     */
    public ConnectorOutboundEvidenceMessageReceiverService(
        ConnectorMessageIdGeneratorService messageIdGeneratorService,
        ConnectorTriggeredEvidenceMessageVerifier triggeredEvidenceMessageVerifier,
        @Qualifier("connectorJmsOutboundEvidenceTriggerPublisher")
        ConnectorMessageEventPublisher<ConnectorTriggeredEvidenceMessage> evidenceTriggerPublisher
    ) {
        this.messageIdGeneratorService = messageIdGeneratorService;
        this.triggeredEvidenceMessageVerifier = triggeredEvidenceMessageVerifier;
        this.evidenceTriggerPublisher = evidenceTriggerPublisher;
    }

    @Override
    public ConnectorTriggeredEvidenceMessage execute(
        @NonNull ConnectorOutboundEvidenceMessageCommand command) {
        var triggeredEvidenceMessage = ConnectorTriggeredEvidenceMessage
            .builder()
            .identifier(messageIdGeneratorService.generateIdentifier())
            .backendMessageIdentifier(command.backendMessageIdentifier())
            .referenceToBackendMessageIdentifier(command.backendMessageIdentifier())
            .backendName(command.backendName())
            .direction(ConnectorMessageDirection.BACKEND_TO_GATEWAY)
            .evidenceType(command.evidenceType())
            .referenceToIdentifier(command.referenceToIdentifier())
            .build();

        triggeredEvidenceMessageVerifier.verify(triggeredEvidenceMessage);

        evidenceTriggerPublisher.publish(triggeredEvidenceMessage);

        return triggeredEvidenceMessage;
    }
}
