package eu.ecodex.connector.application.port.api.message.outbound;

import eu.ecodex.connector.domain.model.message.evidence.ConnectorEvidenceType;
import lombok.Builder;
import lombok.NonNull;

/**
 * This record represents a command for outbound evidence message processing. It encapsulates
 * details about the type of evidence, the backend message identifier, the reference to identifier,
 * and the backend name.
 *
 * @param evidenceType             the type of evidence
 * @param backendMessageIdentifier the identifier of the backend message
 * @param referenceToIdentifier    the identifier to which the evidence is referenced
 * @param backendName              the name of the backend
 */
@Builder
public record ConnectorOutboundEvidenceMessageCommand(
    @NonNull ConnectorEvidenceType evidenceType,
    String backendMessageIdentifier,
    String referenceToIdentifier,
    @NonNull String backendName
) {
}
