package eu.ecodex.connector;

import eu.ecodex.connector.domain.model.message.ConnectorMessage;
import eu.ecodex.connector.domain.model.message.ConnectorMessageAS4Properties;
import eu.ecodex.connector.domain.model.message.ConnectorMessageDirection;
import eu.ecodex.connector.domain.model.message.evidence.ConnectorMessageEvidence;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"MissingJavadocType", "MissingJavadocMethod", "LineLength"})
public class MessageTestFixtures {
    public static ConnectorMessage createOutboundBusinessMessage() {
        var builder = backendToGatewayMessage();
        return builder.build();
    }

    public static ConnectorMessage createEvidenceMessage() {
        var builder = backendToGatewayMessage();
        return builder
                .businessContent(null)
                .evidences(null)
                .transportedEvidences(
                        List.of(EvidenceTestFixtures.createDeliveryEvidence())
                )
                .build();
    }

    public static ConnectorMessage createOutboundStagingBusinessMessage() {
        return backendToGatewayMessage()
                .identifier(null)
                .uuid(null)
                .backendName(null)
                .gatewayName(null)
                .direction(null)
                .evidences(null)
                .createdAt(null)
                .updatedAt(null)
                .as4Properties(
                        defaultASProperties()
                                .toParty(PartyTestFixtures.createToStagingParty())
                                .fromParty(PartyTestFixtures.createStagingFromParty())
                                .build()
                )
                .build();
    }

    public static ConnectorMessage createInboundBusinessMessage() {
        return backendToGatewayMessage()
                .direction(ConnectorMessageDirection.GATEWAY_TO_BACKEND)
                .transportedEvidences(
                        List.of(EvidenceTestFixtures.createRelayREMMDAcceptanceEvidence())
                ).build();
    }

    public static ConnectorMessage createInboundBusinessMessageWithoutBackendName() {
        return createInboundBusinessMessage().toBuilder()
                                             .backendName(null)
                                             .build();
    }

    public static ConnectorMessage createInboundBusinessMessageWithoutBackendNameAndConversationIdentifier() {
        return createInboundBusinessMessageWithoutBackendName()
                .toBuilder()
                .backendName(null)
                .as4Properties(defaultASProperties().conversationIdentifier(null).build())
                .build();
    }

    public static ConnectorMessage createValidOutboundBusinessMessageWithoutGatewayName() {
        return backendToGatewayMessage().gatewayName(null).build();
    }

    public static ConnectorMessage createNullFromPartyOutboundBusinessMessage() {
        return backendToGatewayMessage()
                .as4Properties(defaultASProperties().fromParty(null).build())
                .build();
    }

    public static ConnectorMessage createEmptyFromPartyOutboundBusinessMessage() {
        return backendToGatewayMessage()
                .as4Properties(
                        defaultASProperties()
                                .fromParty(
                                        PartyTestFixtures.createFromParty()
                                                         .toBuilder()
                                                         .identifierType("")
                                                         .build())
                                .build()
                )
                .build();
    }

    public static ConnectorMessage createNullToPartyOutboundBusinessMessage() {
        return backendToGatewayMessage()
                .as4Properties(defaultASProperties().toParty(null).build())
                .build();
    }

    public static ConnectorMessage createEmptyToPartyOutboundBusinessMessage() {
        return backendToGatewayMessage()
                .as4Properties(
                        defaultASProperties()
                                .toParty(PartyTestFixtures.createToParty()
                                                          .toBuilder()
                                                          .identifierType("")
                                                          .build())
                                .build()
                )
                .build();
    }

    public static ConnectorMessage createInvalidFromPartyOutboundBusinessMessage() {
        return backendToGatewayMessage()
                .as4Properties(
                        defaultASProperties()
                                .fromParty(PartyTestFixtures.createToParty())
                                .build()
                )
                .build();
    }

    public static ConnectorMessage createInvalidToPartyOutboundBusinessMessage() {
        return backendToGatewayMessage()
                .as4Properties(
                        defaultASProperties()
                                .toParty(PartyTestFixtures.createFromParty())
                                .build()
                ).build();
    }

    public static ConnectorMessage createSubmissionAcceptanceEvidenceMessage() {
        var evidence = EvidenceTestFixtures.createSubmissionAcceptanceEvidence();
        var evidences = new ArrayList<ConnectorMessageEvidence>();
        evidences.add(evidence);

        return createOutboundBusinessMessage().toBuilder()
                                              .evidences(evidences)
                                              .transportedEvidences(evidences)
                                              .businessContent(null)
                                              .build();
    }

    public static ConnectorMessage createRelayRMMDAcceptanceEvidenceMessage() {
        var evidence = EvidenceTestFixtures.createRelayREMMDAcceptanceEvidence();
        var evidences = new ArrayList<ConnectorMessageEvidence>();
        evidences.add(evidence);

        return createInboundBusinessMessage().toBuilder()
                                             .evidences(evidences)
                                             .transportedEvidences(evidences)
                                             .businessContent(null)
                                             .build();
    }

    public static ConnectorMessage createNonDeliveryEvidenceMessage() {
        var evidence = EvidenceTestFixtures.createNonDeliveryEvidence();
        var evidences = new ArrayList<ConnectorMessageEvidence>();
        evidences.add(evidence);

        return createInboundBusinessMessage().toBuilder()
                                             .evidences(evidences)
                                             .transportedEvidences(evidences)
                                             .businessContent(null)
                                             .build();
    }

    public static ConnectorMessage createDeliveryEvidenceMessage() {
        var evidence = EvidenceTestFixtures.createDeliveryEvidence();
        var evidences = new ArrayList<ConnectorMessageEvidence>();
        evidences.add(evidence);

        return backendToGatewayMessage()
                .evidences(evidences)
                .transportedEvidences(evidences)
                .businessContent(null)
                .build();
    }

    public static ConnectorMessage createEvidenceTriggerMessage() {
        var evidence = EvidenceTestFixtures.createEvidenceTrigger();
        var transported = new ArrayList<ConnectorMessageEvidence>();
        transported.add(evidence);

        return evidencesMessage()
                .transportedEvidences(transported)
                .businessContent(null)
                .build();
    }

    public static ConnectorMessage createRejectedMessage() {
        var evidence = EvidenceTestFixtures.createSubmissionRejectionEvidence();
        var evidences = new ArrayList<ConnectorMessageEvidence>();
        evidences.add(evidence);

        return backendToGatewayMessage()
                .evidences(evidences)
                .transportedEvidences(evidences)
                .businessContent(null)
                .rejectedAt(Instant.now())
                .build();
    }

    public static ConnectorMessage createConfirmedMessage() {
        var evidence = EvidenceTestFixtures.createSubmissionAcceptanceEvidence();
        var evidences = new ArrayList<ConnectorMessageEvidence>();
        evidences.add(evidence);

        return backendToGatewayMessage()
                .evidences(evidences)
                .transportedEvidences(evidences)
                .businessContent(null)
                .confirmedAt(Instant.now())
                .build();
    }

    private static ConnectorMessage.ConnectorMessageBuilder evidencesMessage() {
        var builder = backendToGatewayMessage();
        return builder
                .identifier(null)
                .uuid(null)
                .backendName(null)
                .gatewayName(null)
                .direction(ConnectorMessageDirection.BACKEND_TO_GATEWAY)
                .evidences(null)
                .createdAt(null)
                .updatedAt(null)
                .as4Properties(
                        ConnectorMessageAS4Properties
                                .builder()
                                .conversationIdentifier(null)
                                .referenceToIdentifier(
                                        "223caef9-cae9-4387-a38c-ad4879f94b4e@connector.ecodex.eu")
                                .ebmsMessageIdentifier(null)
                                .originalSender(null)
                                .finalRecipient(null)
                                .fromParty(null)
                                .toParty(null)
                                .service(null)
                                .action(null)
                                .build()
                );
    }

    // is considered as an outgoing message
    private static ConnectorMessage.ConnectorMessageBuilder backendToGatewayMessage() {
        return ConnectorMessage
                .builder()
                .businessDomainIdentifier(
                        BusinessDomainTestFixtures.createDefaultBusinessDomain()
                                                  .identifier()
                )
                .businessContent(MessageContentTestFixtures.createContent())
                .uuid("223caef9-cae9-4387-a38c-ad4879f94b4e")
                .identifier("223caef9-cae9-4387-a38c-ad4879f94b4e@connector.ecodex.eu")
                .backendMessageIdentifier(
                        "85964ab5-b04b-4d45-97d1-962b565e22df@connector.ecodex.eu")
                .backendName("default_backend")
                .gatewayName("default_gateway")
                .as4Properties(defaultASProperties().build())
                .direction(ConnectorMessageDirection.BACKEND_TO_GATEWAY)
                .evidences(new ArrayList<>())
                .transportedEvidences(new ArrayList<>())
                .createdAt(Instant.now())
                .updatedAt(Instant.now());
    }

    private static ConnectorMessageAS4Properties.ConnectorMessageAS4PropertiesBuilder defaultASProperties() {
        return ConnectorMessageAS4Properties
                .builder()
                .conversationIdentifier("e6a173ec-de21-46dc-8a19-63a6cb74915d")
                .ebmsMessageIdentifier(null)
                .originalSender("alice")
                .finalRecipient("bob")
                .fromParty(PartyTestFixtures.createFromParty())
                .toParty(PartyTestFixtures.createToParty())
                .service(ServiceTestFixtures.createService())
                .action(ActionTestFixtures.createAction());
    }
}
