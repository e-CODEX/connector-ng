package eu.ecodex.connector.application.port.api.message.outbound;

import eu.ecodex.connector.domain.model.businessdomain.ConnectorBusinessDomainIdentifier;
import eu.ecodex.connector.domain.model.message.ConnectorMessageAS4Properties;
import eu.ecodex.connector.domain.model.message.ConnectorMessageDirection;
import eu.ecodex.connector.domain.model.message.attachment.ConnectorMessageAttachment;
import eu.ecodex.connector.domain.model.message.content.ConnectorMessageBusinessContent;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import java.util.List;
import lombok.Builder;
import lombok.NonNull;

/**
 * Represents a record for sending outbound business messages through the connector system.
 *
 * @param businessDomainIdentifier            Represents the unique identifier of the business
 *                                            domain associated with this message.
 * @param backendMessageIdentifier            The unique identifier of the message in the backend
 *                                            system. This value is optional and may not always be
 *                                            present.
 * @param referenceToBackendMessageIdentifier A backend-specific reference to the identifier of a
 *                                            related message. This field is marked for removal.
 * @param backendName                         Represents the name of the backend system responsible
 *                                            for this message. This field is required and cannot be
 *                                            null.
 * @param as4Properties                       Encapsulates the AS4-specific properties related to
 *                                            the message, including sender, recipient, and routing
 *                                            details. This field is mandatory.
 * @param direction                           The direction of the message flow (e.g., backend to
 *                                            gateway or gateway to backend).
 * @param businessContent                     Contains the main business content of the message,
 *                                            including XML structures and associated documents.
 *                                            This field is required.
 * @param attachments                         A list of additional attachments associated with the
 *                                            message, such as supplementary documents or metadata.
 *                                            Defaults to an empty list if not provided.
 */
@Builder
public record ConnectorOutboundBusinessMessageCommand(
    @NonNull ConnectorBusinessDomainIdentifier businessDomainIdentifier,
    @Nullable String backendMessageIdentifier,
    @Nullable String referenceToBackendMessageIdentifier, // TODO to be removed
    @NonNull String backendName,
    @Nonnull ConnectorMessageAS4Properties as4Properties,
    @Nonnull ConnectorMessageDirection direction,
    @NonNull ConnectorMessageBusinessContent businessContent,
    @Nullable List<ConnectorMessageAttachment> attachments
) {
    public ConnectorOutboundBusinessMessageCommand {
        attachments = attachments == null ? List.of() : attachments;
    }
}
