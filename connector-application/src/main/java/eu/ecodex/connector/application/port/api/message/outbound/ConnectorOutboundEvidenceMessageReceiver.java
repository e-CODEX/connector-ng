package eu.ecodex.connector.application.port.api.message.outbound;

import eu.ecodex.connector.application.port.api.message.ConnectorTriggeredEvidenceMessageVerifier;
import eu.ecodex.connector.domain.model.message.ConnectorTriggeredEvidenceMessage;
import jakarta.annotation.Nonnull;

/**
 * Defines the contract for receiving outbound evidence trigger messages submitted by a backend.
 *
 * <p>Implementations are responsible for turning an outbound evidence message command into a
 * {@link ConnectorTriggeredEvidenceMessage}, which can then be verified and processed (see
 * {@link ConnectorTriggeredEvidenceMessageVerifier} and
 * {@link ConnectorOutboundEvidenceMessageProcessor}).
 */
public interface ConnectorOutboundEvidenceMessageReceiver {

    /**
     * Processes the given command to create and return a
     * {@link ConnectorTriggeredEvidenceMessage}.
     *
     * @param command the outbound evidence message command describing the evidence requested by the
     *                backend (must not be null)
     *
     * @return the {@link ConnectorTriggeredEvidenceMessage} created from the command
     */
    ConnectorTriggeredEvidenceMessage execute(
        @Nonnull ConnectorOutboundEvidenceMessageCommand command);
}
