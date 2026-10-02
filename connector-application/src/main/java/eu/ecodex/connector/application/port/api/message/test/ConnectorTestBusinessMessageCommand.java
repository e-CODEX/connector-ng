package eu.ecodex.connector.application.port.api.message.test;

import eu.ecodex.connector.domain.model.businessdomain.ConnectorBusinessDomainIdentifier;
import eu.ecodex.connector.domain.model.message.attachment.ConnectorMessageAttachment;
import eu.ecodex.connector.domain.model.message.content.ConnectorMessageBusinessContent;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import java.util.List;
import lombok.Builder;

/**
 * Represents a record for sending outbound business messages through the connector system.
 *
 * @param businessDomainIdentifier Represents the unique identifier of the business domain
 *                                 associated with this message.
 * @param backendMessageIdentifier Represents the unique identifier of the message in the backend
 *                                 system.
 * @param as4PropertiesCommand     Represents the test message AS4 properties.
 * @param businessContent          Contains the main business content of the message, including XML
 *                                 structures and associated documents. This field is required.
 * @param attachments              A list of additional attachments associated with the message,
 *                                 such as supplementary documents or metadata. Defaults to an empty
 *                                 list if not provided.
 */
@Builder
public record ConnectorTestBusinessMessageCommand(
    @Nonnull ConnectorBusinessDomainIdentifier businessDomainIdentifier,
    @Nonnull String backendMessageIdentifier,
    @Nonnull ConnectorTestBusinessMessageAS4PropertiesCommand as4PropertiesCommand,
    @Nonnull ConnectorMessageBusinessContent businessContent,
    @Nullable List<ConnectorMessageAttachment> attachments
) {
    public ConnectorTestBusinessMessageCommand {
        attachments = attachments == null ? List.of() : attachments;
    }
}
