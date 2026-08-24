package eu.ecodex.connector;

import eu.ecodex.connector.domain.model.message.ConnectorEvidenceMessage;
import eu.ecodex.connector.domain.model.message.ConnectorMessageDirection;
import eu.ecodex.connector.domain.model.message.evidence.ConnectorMessageEvidence;
import java.util.ArrayList;

@SuppressWarnings({"MissingJavadocType", "MissingJavadocMethod", "LineLength"})
public class EvidenceMessageTestFixtures {
    public static ConnectorEvidenceMessage createSubmissionAcceptanceEvidenceMessage() {
        var evidence = EvidenceTestFixtures.createSubmissionAcceptanceEvidence();
        var evidences = new ArrayList<ConnectorMessageEvidence>();
        evidences.add(evidence);

        return backendToGatewayMessage()
            .transportedEvidences(evidences)
            .build();
    }

    public static ConnectorEvidenceMessage createRelayRMMDAcceptanceEvidenceMessage() {
        var evidence = EvidenceTestFixtures.createRelayREMMDAcceptanceEvidence();
        var evidences = new ArrayList<ConnectorMessageEvidence>();
        evidences.add(evidence);

        return backendToGatewayMessage()
            .transportedEvidences(evidences)
            .build();
    }

    public static ConnectorEvidenceMessage createRelayREMMDRejectionEvidenceEvidenceMessage() {
        var evidence = EvidenceTestFixtures.createRelayREMMDRejectionEvidence();
        var evidences = new ArrayList<ConnectorMessageEvidence>();
        evidences.add(evidence);

        return backendToGatewayMessage()
            .transportedEvidences(evidences)
            .build();
    }

    public static ConnectorEvidenceMessage createNonDeliveryEvidenceMessage() {
        var evidence = EvidenceTestFixtures.createNonDeliveryEvidence();
        var evidences = new ArrayList<ConnectorMessageEvidence>();
        evidences.add(evidence);

        return backendToGatewayMessage()
            .transportedEvidences(evidences)
            .build();
    }

    public static ConnectorEvidenceMessage createDeliveryEvidenceMessage() {
        var evidence = EvidenceTestFixtures.createDeliveryEvidence();
        var evidences = new ArrayList<ConnectorMessageEvidence>();
        evidences.add(evidence);

        return backendToGatewayMessage()
            .transportedEvidences(evidences)
            .build();
    }

    public static ConnectorEvidenceMessage createEvidenceTriggerMessage() {
        var evidence = EvidenceTestFixtures.createEvidenceTrigger();
        var transported = new ArrayList<ConnectorMessageEvidence>();
        transported.add(evidence);

        return backendToGatewayMessage()
            .transportedEvidences(transported)
            .build();
    }

    public static ConnectorEvidenceMessage createRejectedMessage() {
        var evidence = EvidenceTestFixtures.createSubmissionRejectionEvidence();
        var evidences = new ArrayList<ConnectorMessageEvidence>();
        evidences.add(evidence);

        return backendToGatewayMessage()
            .transportedEvidences(evidences)
            .build();
    }

    public static ConnectorEvidenceMessage createConfirmedMessage() {
        var evidence = EvidenceTestFixtures.createSubmissionAcceptanceEvidence();
        var evidences = new ArrayList<ConnectorMessageEvidence>();
        evidences.add(evidence);

        return backendToGatewayMessage()
            .transportedEvidences(evidences)
            .build();
    }

    private static ConnectorEvidenceMessage.ConnectorEvidenceMessageBuilder backendToGatewayMessage() {
        return ConnectorEvidenceMessage
            .builder()
            .businessDomainIdentifier(
                BusinessDomainTestFixtures.createDefaultBusinessDomain()
                                          .identifier()
            )
            .identifier("223caef9-cae9-4387-a38c-ad4879f94b4e@connector.ecodex.eu")
            .backendMessageIdentifier(
                "85964ab5-b04b-4d45-97d1-962b565e22df@connector.ecodex.eu")
            .backendName("default_backend")
            .gatewayName("default_gateway")
            .as4Properties(AS4PropertiesTestFixtures.defaultAS4Properties().build())
            .direction(ConnectorMessageDirection.BACKEND_TO_GATEWAY)
            .transportedEvidences(new ArrayList<>());
    }
}
