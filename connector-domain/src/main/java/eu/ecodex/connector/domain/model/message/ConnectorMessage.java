package eu.ecodex.connector.domain.model.message;

import eu.ecodex.connector.domain.model.ConnectorPublishable;
import eu.ecodex.connector.domain.model.businessdomain.ConnectorBusinessDomainIdentifier;
import eu.ecodex.connector.domain.model.message.evidence.ConnectorMessageEvidence;
import java.util.List;

/**
 * Represents a message exchanged within the connector system.
 *
 * <p>This sealed interface defines the common properties and behaviors for all connector messages,
 * including business messages and evidence messages. It extends the {@link ConnectorPublishable}
 * interface, which marks messages that can be published to the message broker.
 *
 * <p>The implementing classes, {@link ConnectorBusinessMessage} and
 * {@link ConnectorEvidenceMessage}, provide contextual implementations that add specific fields and
 * methods necessary for handling different message types.
 *
 * <p>The messages are characterised by various identifiers for tracking and routing purposes,
 * along with metadata detailing the involved systems and their roles within the transaction.
 */
public sealed interface ConnectorMessage extends ConnectorPublishable
    permits ConnectorBusinessMessage, ConnectorEvidenceMessage {
    ConnectorBusinessDomainIdentifier businessDomainIdentifier();

    String identifier();

    String backendMessageIdentifier();

    String referenceToBackendMessageIdentifier();

    String backendName();

    String gatewayName();

    ConnectorMessageAS4Properties as4Properties();

    ConnectorMessageDirection direction();

    List<ConnectorMessageEvidence> transportedEvidences();
}
