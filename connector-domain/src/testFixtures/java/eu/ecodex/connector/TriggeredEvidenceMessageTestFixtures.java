package eu.ecodex.connector;

import eu.ecodex.connector.domain.model.message.ConnectorMessageDirection;
import eu.ecodex.connector.domain.model.message.ConnectorTriggeredEvidenceMessage;
import eu.ecodex.connector.domain.model.message.evidence.ConnectorEvidenceType;

@SuppressWarnings({"MissingJavadocType", "MissingJavadocMethod", "LineLength"})
public class TriggeredEvidenceMessageTestFixtures {
    public static ConnectorTriggeredEvidenceMessage createDeliveryTriggeredEvidenceMessage() {
        return ConnectorTriggeredEvidenceMessage
            .builder()
            .identifier("aa86b9d2-59f8-4d20-8051-35d67f0e8fd1@connector.ecodex.eu")
            .backendMessageIdentifier("125579fe-3279-4277-9a30-95231513e2bf")
            .referenceToBackendMessageIdentifier("125579fe-3279-4277-9a30-95231513e2bf")
            .backendName("default_backend")
            .direction(ConnectorMessageDirection.BACKEND_TO_GATEWAY)
            .evidenceType(ConnectorEvidenceType.DELIVERY)
            .referenceToIdentifier("a63c5d50-c951-49d3-b5ba-8e54ce69ebbc@connector.ecodex.eu")
            .build();
    }
}
