package eu.ecodex.connector.domain.model.message;

import eu.ecodex.connector.domain.model.ConnectorPublishable;
import eu.ecodex.connector.domain.model.message.evidence.ConnectorEvidenceType;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import java.io.Serializable;
import lombok.Builder;
import lombok.NonNull;

/**
 * Represents an evidence message triggered by a connector as part of its interactions with the
 * backend system or gateway.
 *
 * @param identifier                          A unique identifier for this evidence message.
 * @param backendMessageIdentifier            An optional identifier for the corresponding backend
 *                                            message.
 * @param referenceToBackendMessageIdentifier An optional reference to a related backend message by
 *                                            its identifier.
 * @param backendName                         The name of the associated backend system.
 * @param direction                           The {@link ConnectorMessageDirection} denoting the
 *                                            flow of the message.
 * @param evidenceType                        The {@link ConnectorEvidenceType} specifying the type
 *                                            of evidence being reported.
 * @param referenceToIdentifier               An optional reference to another identifier for
 *                                            correlation purposes.
 */
@Builder(toBuilder = true)
public record ConnectorTriggeredEvidenceMessage(
    @NonNull String identifier,
    @Nullable String backendMessageIdentifier,
    @Nullable String referenceToBackendMessageIdentifier,
    @NonNull String backendName,
    @NonNull ConnectorMessageDirection direction,
    @NonNull ConnectorEvidenceType evidenceType,
    @Nullable String referenceToIdentifier
) implements Serializable, ConnectorPublishable {
    @Override
    @Nonnull
    public String toString() {
        return String.format(
            "{identifier=%s, backendMessageIdentifier=%s, backendName=%s, "
                + "referenceToBackendMessageIdentifier=%s, direction=%s}",
            identifier, backendMessageIdentifier, backendName,
            referenceToBackendMessageIdentifier, direction
        );
    }
}
