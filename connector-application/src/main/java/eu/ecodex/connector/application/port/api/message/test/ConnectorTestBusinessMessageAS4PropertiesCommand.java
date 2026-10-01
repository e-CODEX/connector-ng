package eu.ecodex.connector.application.port.api.message.test;

import eu.ecodex.connector.domain.model.businessdomain.ConnectorBusinessDomainIdentifier;
import eu.ecodex.connector.domain.model.pmode.ConnectorParty;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import lombok.Builder;

/**
 * Represents a record for sending outbound business messages through the connector system.
 *
 * @param businessDomainIdentifier Represents the unique identifier of the business domain
 *                                 associated with this message.
 * @param ebmsIdentifier           Represents Ebms message identifier
 * @param conversationIdentifier   Represents the unique identifier of the conversation to which
 *                                 this message belongs.
 * @param originalSender           Represents the original sender of the business message; must not
 *                                 be {@code null}.
 * @param finalRecipient           Represents the final recipient of the business message; must not
 *                                 be {@code null}.
 * @param fromParty                The {@link ConnectorParty} representing the sending party in the
 *                                 AS4 exchange; must not be {@code null}
 * @param toParty                  The {@link ConnectorParty} representing the receiving party in
 *                                 the AS4 exchange; must not be {@code null}
 */
@Builder
public record ConnectorTestBusinessMessageAS4PropertiesCommand(
    @Nonnull ConnectorBusinessDomainIdentifier businessDomainIdentifier,
    @Nullable String ebmsIdentifier,
    @Nonnull String conversationIdentifier,
    @Nonnull String originalSender,
    @Nonnull String finalRecipient,
    @Nonnull ConnectorParty fromParty,
    @Nonnull ConnectorParty toParty
) {
}
