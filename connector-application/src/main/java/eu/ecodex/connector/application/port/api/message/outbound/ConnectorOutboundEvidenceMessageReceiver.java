package eu.ecodex.connector.application.port.api.message.outbound;

import eu.ecodex.connector.domain.model.message.ConnectorTriggeredEvidenceMessage;
import jakarta.annotation.Nonnull;

/**
 * Defines the interface for receiving outbound evidence messages.
 */
public interface ConnectorOutboundEvidenceMessageReceiver {
    ConnectorTriggeredEvidenceMessage execute(
        @Nonnull ConnectorOutboundEvidenceMessageCommand command);
}
