package eu.ecodex.connector;

import eu.ecodex.connector.domain.model.message.ConnectorMessage;
import eu.ecodex.connector.domain.model.message.ConnectorMessageAS4Properties;
import eu.ecodex.connector.domain.model.message.ConnectorMessageDirection;
import eu.ecodex.connector.domain.model.message.content.ConnectorMessageBusinessContent;
import eu.ecodex.connector.domain.model.message.evidence.ConnectorEvidence;
import java.time.Instant;
import java.util.ArrayList;

@SuppressWarnings({"MissingJavadocType", "MissingJavadocMethod", "LineLength"})
public class MessageTestFixtures {
    public static ConnectorMessage createValidOutboundBusinessMessage() {
        var builder = backendToGatewayMessage();
        return builder.build();
    }

    public static ConnectorMessage createValidOutboundStagingBusinessMessage() {
        var builder = backendToGatewayMessage();
        return builder
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

    public static ConnectorMessage createValidInboundBusinessMessage() {
        var builder = backendToGatewayMessage();
        builder.direction(ConnectorMessageDirection.GATEWAY_TO_BACKEND);
        return builder.build();
    }

    public static ConnectorMessage createValidInboundBusinessMessageWithoutBackendName() {
        var builder = createValidInboundBusinessMessage().toBuilder();
        builder.backendName(null);
        return builder.build();
    }

    public static ConnectorMessage createValidInboundBusinessMessageWithoutBackendNameAndConversationIdentifier() {
        var builder = createValidInboundBusinessMessageWithoutBackendName().toBuilder();
        builder.backendName(null);
        builder.as4Properties(defaultASProperties().conversationIdentifier(null).build());
        return builder.build();
    }

    public static ConnectorMessage createValidOutboundBusinessMessageWithoutGatewayName() {
        var builder = backendToGatewayMessage();
        builder.gatewayName(null);
        return builder.build();
    }

    public static ConnectorMessage createNullFromPartyOutboundBusinessMessage() {
        var builder = backendToGatewayMessage();
        builder.as4Properties(
                defaultASProperties()
                        .fromParty(null)
                        .build()
        );
        return builder.build();
    }

    public static ConnectorMessage createEmptyFromPartyOutboundBusinessMessage() {
        var builder = backendToGatewayMessage();
        builder.as4Properties(
                defaultASProperties()
                        .fromParty(
                                PartyTestFixtures.createFromParty().toBuilder().identifierType("").build())
                        .build()
        );
        return builder.build();
    }

    public static ConnectorMessage createNullToPartyOutboundBusinessMessage() {
        var builder = backendToGatewayMessage();
        builder.as4Properties(
                defaultASProperties()
                        .toParty(null)
                        .build()
        );
        return builder.build();
    }

    public static ConnectorMessage createEmptyToPartyOutboundBusinessMessage() {
        var builder = backendToGatewayMessage();
        builder.as4Properties(
                defaultASProperties()
                        .toParty(PartyTestFixtures.createToParty().toBuilder().identifierType("").build())
                        .build()
        );
        return builder.build();
    }

    public static ConnectorMessage createInvalidFromPartyOutboundBusinessMessage() {
        var builder = backendToGatewayMessage();
        builder.as4Properties(
                defaultASProperties()
                        .fromParty(PartyTestFixtures.createToParty())
                        .build()
        );
        return builder.build();
    }

    public static ConnectorMessage createInvalidToPartyOutboundBusinessMessage() {
        var builder = backendToGatewayMessage();
        builder.as4Properties(
                defaultASProperties()
                        .toParty(PartyTestFixtures.createFromParty())
                        .build()
        );
        return builder.build();
    }

    public static ConnectorMessage createSubmissionAcceptanceEvidenceMessage() {
        var evidence = EvidenceTestFixtures.createSubmissionAcceptanceEvidence();
        var evidences = new ArrayList<ConnectorEvidence>();
        evidences.add(evidence);
        var builder = createValidOutboundBusinessMessage().toBuilder();
        builder.evidences(evidences);
        builder.transportedEvidences(evidences);
        builder.businessContent(null);
        return builder.build();
    }

    public static ConnectorMessage createRelayRMMDAcceptanceEvidenceMessage() {
        var evidence = EvidenceTestFixtures.createRelayREMMDAcceptanceEvidence();
        var evidences = new ArrayList<ConnectorEvidence>();
        evidences.add(evidence);
        var builder = createValidInboundBusinessMessage().toBuilder();
        builder.evidences(evidences);
        builder.transportedEvidences(evidences);
        builder.businessContent(null);
        return builder.build();
    }

    public static ConnectorMessage createNonDeliveryEvidenceMessage() {
        var evidence = EvidenceTestFixtures.createNonDeliveryEvidence();
        var evidences = new ArrayList<ConnectorEvidence>();
        evidences.add(evidence);
        var builder = createValidInboundBusinessMessage().toBuilder();
        builder.evidences(evidences);
        builder.transportedEvidences(evidences);
        builder.businessContent(null);
        return builder.build();
    }

    public static ConnectorMessage createEvidenceTriggerMessage() {
        var evidence = EvidenceTestFixtures.createEvidenceTrigger();
        var evidences = new ArrayList<ConnectorEvidence>();
        evidences.add(evidence);
        var builder = backendToGatewayMessage();
        builder.evidences(evidences);
        builder.transportedEvidences(evidences);
        builder.businessContent(null);
        return builder.build();
    }

    public static ConnectorMessage createRejectedMessage() {
        var evidence = EvidenceTestFixtures.createSubmissionRejectionEvidence();
        var evidences = new ArrayList<ConnectorEvidence>();
        evidences.add(evidence);
        var builder = backendToGatewayMessage();
        builder.evidences(evidences);
        builder.transportedEvidences(evidences);
        builder.businessContent(null);
        builder.rejectedAt(Instant.now());
        return builder.build();
    }

    public static ConnectorMessage createConfirmedMessage() {
        var evidence = EvidenceTestFixtures.createSubmissionAcceptanceEvidence();
        var evidences = new ArrayList<ConnectorEvidence>();
        evidences.add(evidence);
        var builder = backendToGatewayMessage();
        builder.evidences(evidences);
        builder.transportedEvidences(evidences);
        builder.businessContent(null);
        builder.confirmedAt(Instant.now());
        return builder.build();
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
                .businessContent(ConnectorMessageBusinessContent.builder().build())
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
