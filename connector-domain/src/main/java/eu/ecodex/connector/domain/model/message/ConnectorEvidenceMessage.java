package eu.ecodex.connector.domain.model.message;

import eu.ecodex.connector.domain.model.businessdomain.ConnectorBusinessDomainIdentifier;
import eu.ecodex.connector.domain.model.message.evidence.ConnectorMessageEvidence;
import eu.ecodex.connector.domain.model.pmode.ConnectorPartyRoleType;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.Builder;

/**
 * Represents an evidence message within the connector system.
 *
 * <p>This class encapsulates the metadata and evidence-related content sent or received through
 * the connector. It is specifically tailored for processing evidence messages in various
 * communication flows between backend systems and gateways.
 *
 * <p>The class insists on having a non-empty list of transported evidences as part of its
 * initialization, enforcing the validity of constructed instances. Additionally, it provides
 * utility methods to interrogate the nature of the message and to modify its directionality within
 * the communication flow.
 *
 * <p>The {@link ConnectorEvidenceMessage} class implements {@link ConnectorMessage}, making it a
 * compliant part of the connector's message-publishing mechanism.
 *
 * @param businessDomainIdentifier            The business domain this message belongs to.
 * @param identifier                          The unique identifier for the evidence message.
 * @param backendMessageIdentifier            An identifier specific to the backend system for this
 *                                            message, if available.
 * @param referenceToBackendMessageIdentifier The identifier of any backend-referenced message, if
 *                                            applicable.
 * @param backendName                         The name of the backend system associated with this
 *                                            message, if available.
 * @param gatewayName                         The name of the gateway associated with this message,
 *                                            if available.
 * @param as4Properties                       The AS4-specific properties of the evidence message,
 *                                            if available.
 * @param direction                           The directional flow of the evidence message (e.g.,
 *                                            backend-to-gateway or gateway-to-backend).
 * @param transportedEvidences                A non-empty list of evidences transported within this
 *                                            message. It must contain at least one item.
 */
@Builder(toBuilder = true)
public record ConnectorEvidenceMessage(
    ConnectorBusinessDomainIdentifier businessDomainIdentifier,
    @Nonnull String identifier,
    @Nullable String backendMessageIdentifier,
    @Nullable String referenceToBackendMessageIdentifier,
    @Nullable String backendName,
    @Nullable String gatewayName,
    @Nonnull ConnectorMessageAS4Properties as4Properties,
    @Nonnull ConnectorMessageDirection direction,
    @Nonnull @NotEmpty List<ConnectorMessageEvidence> transportedEvidences
) implements ConnectorMessage {
    /**
     * Constructs a new instance of {@link ConnectorEvidenceMessage}.
     *
     * @throws IllegalStateException if the list of transported evidences is empty
     */
    public ConnectorEvidenceMessage {
        if (transportedEvidences.isEmpty()) {
            throw new IllegalStateException("No transported evidences");
        }
    }

    public boolean isEvidenceTriggerMessage() {
        return transportedEvidences.size() == 1
            && transportedEvidences.getFirst().content() == null;
    }

    /**
     * Switches the direction of the current message by altering its AS4 properties and swapping the
     * roles and parties involved. The method reassigns the sender and receiver roles, switches the
     * original sender and final recipient, and updates the direction of the message.
     *
     * <p>The resulting message maintains its general structure and compliance with the expected
     * properties, but with reversed sender-to-receiver directions.
     *
     * @return A new {@code ConnectorMessage} instance with updated direction, roles, and party
     *     information, reflecting the switched communication flow.
     */
    @Nonnull
    public ConnectorEvidenceMessage switchDirection() {
        final var as4Properties = this.as4Properties();
        final var direction = this.direction();

        final var fromParty = as4Properties.fromParty();

        final var toParty = as4Properties.toParty();

        var switchedAS4PropertiesBuilder = this.as4Properties().toBuilder();
        // switching party, but keep Role and RoleType
        final var switchedFromParty = toParty.toBuilder()
                                             .roleType(ConnectorPartyRoleType.INITIATOR)
                                             .role(fromParty.role())
                                             .build();

        final var switchedToParty = fromParty.toBuilder()
                                             .roleType(ConnectorPartyRoleType.RESPONDER)
                                             .role(toParty.role())
                                             .build();

        switchedAS4PropertiesBuilder.fromParty(switchedFromParty);
        switchedAS4PropertiesBuilder.toParty(switchedToParty);
        switchedAS4PropertiesBuilder.originalSender(as4Properties.finalRecipient());
        switchedAS4PropertiesBuilder.finalRecipient(as4Properties.originalSender());

        var switchedMessageBuilder = this.toBuilder();
        switchedMessageBuilder.direction(
            ConnectorMessageDirection.from(direction.getTarget(), direction.getSource())
        );
        switchedMessageBuilder.as4Properties(switchedAS4PropertiesBuilder.build());

        return switchedMessageBuilder.build();
    }

    @Override
    @Nonnull
    public String toString() {
        return String.format(
            "{identifier=%s, backendMessageIdentifier=%s, backendName=%s, gatewayName=%s, "
                + "referenceToBackendMessageIdentifier=%s, direction=%s, as4Properties=%s}",
            identifier, backendMessageIdentifier, backendName, gatewayName,
            referenceToBackendMessageIdentifier, direction, as4Properties
        );
    }
}
