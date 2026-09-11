package eu.ecodex.connector.application.port.api.message.inbound;

import eu.ecodex.connector.domain.model.businessdomain.ConnectorBusinessDomainIdentifier;
import eu.ecodex.connector.domain.model.message.ConnectorMessageAS4Properties;
import eu.ecodex.connector.domain.model.message.evidence.ConnectorMessageEvidence;
import jakarta.annotation.Nonnull;
import java.util.List;
import lombok.Builder;
import lombok.NonNull;

/**
 * Represents a record for sending outbound business messages through the connector system.
 *
 * @param businessDomainIdentifier Represents the unique identifier of the business domain
 *                                 associated with this message.
 * @param gatewayName              Represents the name of the gateway system responsible for this
 *                                 message. This field is required and cannot be null.
 * @param as4Properties            Encapsulates the AS4-specific properties related to the message,
 *                                 including sender, recipient, and routing details. This field is
 *                                 mandatory.
 * @param transportedEvidences     A list of evidences that are transported with the message.
 */
@Builder
public record ConnectorInboundEvidenceMessageCommand(
    @NonNull ConnectorBusinessDomainIdentifier businessDomainIdentifier,
    @NonNull String gatewayName,
    @Nonnull ConnectorMessageAS4Properties as4Properties,
    @Nonnull List<ConnectorMessageEvidence> transportedEvidences
) {
    /**
     * Constructor for the ConnectorInboundEvidenceMessageCommand class.
     */
    public ConnectorInboundEvidenceMessageCommand {
        if (transportedEvidences.isEmpty()) {
            throw new IllegalStateException("transportedEvidences must not be empty");
        }
    }
}
