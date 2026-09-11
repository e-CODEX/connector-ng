package eu.ecodex.connector.application.port.api.message.inbound;

import eu.ecodex.connector.domain.model.businessdomain.ConnectorBusinessDomainIdentifier;
import eu.ecodex.connector.domain.model.message.ConnectorMessageAS4Properties;
import eu.ecodex.connector.domain.model.message.attachment.ConnectorMessageAttachment;
import eu.ecodex.connector.domain.model.message.content.ConnectorMessageBusinessContent;
import eu.ecodex.connector.domain.model.message.evidence.ConnectorMessageEvidence;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
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
 * @param businessContent          Contains the main business content of the message, including XML
 *                                 structures and associated documents. This field is required.
 * @param attachments              A list of additional attachments associated with the message,
 *                                 such as supplementary documents or metadata. Defaults to an empty
 *                                 list if not provided.
 * @param transportedEvidences     A list of evidences that are transported with the message.
 */
@Builder
public record ConnectorInboundBusinessMessageCommand(
    @NonNull ConnectorBusinessDomainIdentifier businessDomainIdentifier,
    @NonNull String gatewayName,
    @Nonnull ConnectorMessageAS4Properties as4Properties,
    @NonNull ConnectorMessageBusinessContent businessContent,
    @Nullable List<ConnectorMessageAttachment> attachments,
    @Nonnull List<ConnectorMessageEvidence> transportedEvidences
) {
    /**
     * Constructs an instance of ConnectorInboundBusinessMessageCommand, initializing or validating
     * provided fields as necessary. Ensures that the list of transported evidences is not empty and
     * sets the attachments to an empty list if null.
     *
     * @param businessDomainIdentifier Represents the unique identifier of the business domain
     *                                 associated with this message. Must not be null.
     * @param gatewayName              Represents the name of the gateway system responsible for
     *                                 this message. Must not be null.
     * @param as4Properties            Encapsulates the AS4-specific properties related to the
     *                                 message, such as sender, recipient, and routing details. Must
     *                                 not be null.
     * @param businessContent          Contains the main business content of the message, which
     *                                 includes XML structures and associated documents. Must not be
     *                                 null.
     * @param attachments              A list of additional attachments associated with the message,
     *                                 such as supplementary documents or metadata. If null, it will
     *                                 default to an empty list.
     * @param transportedEvidences     A list of evidences that are transported with the message.
     *                                 Must not be empty; an empty list will result in an
     *                                 IllegalStateException.
     */
    public ConnectorInboundBusinessMessageCommand {
        attachments = attachments == null ? List.of() : attachments;

        if (transportedEvidences.isEmpty()) {
            throw new IllegalStateException("transportedEvidences must not be empty");
        }
    }
}
