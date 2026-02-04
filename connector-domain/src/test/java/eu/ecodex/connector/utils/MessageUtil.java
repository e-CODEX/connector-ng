package eu.ecodex.connector.utils;

import eu.ecodex.connector.domain.model.message.ConnectorMessage;
import eu.ecodex.connector.domain.model.message.ConnectorMessageAS4Properties;
import eu.ecodex.connector.domain.model.message.ConnectorMessageDirection;
import eu.ecodex.connector.domain.model.message.content.ConnectorMessageContent;
import eu.ecodex.connector.domain.model.message.evidence.ConnectorEvidence;
import java.time.Instant;
import java.util.ArrayList;

@SuppressWarnings({"MissingJavadocType", "MissingJavadocMethod", "LineLength"})
public class MessageUtil {
    public static ConnectorMessage createValidOutboundBusinessMessage() {
        var builder = backendToGatewayMessage();
        return builder.build();
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
                                PartyUtil.createFromParty().toBuilder().identifierType("").build())
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
                        .toParty(PartyUtil.createToParty().toBuilder().identifierType("").build())
                        .build()
        );
        return builder.build();
    }

    public static ConnectorMessage createInvalidFromPartyOutboundBusinessMessage() {
        var builder = backendToGatewayMessage();
        builder.as4Properties(
                defaultASProperties()
                        .fromParty(PartyUtil.createToParty())
                        .build()
        );
        return builder.build();
    }

    public static ConnectorMessage createInvalidToPartyOutboundBusinessMessage() {
        var builder = backendToGatewayMessage();
        builder.as4Properties(
                defaultASProperties()
                        .toParty(PartyUtil.createFromParty())
                        .build()
        );
        return builder.build();
    }

    public static ConnectorMessage createSubmissionAcceptanceEvidenceMessage() {
        var evidence = EvidenceUtil.createSubmissionAcceptanceEvidence();
        var evidences = new ArrayList<ConnectorEvidence>();
        evidences.add(evidence);
        var builder = createValidOutboundBusinessMessage().toBuilder();
        builder.evidences(evidences);
        builder.transportedEvidences(evidences);
        builder.content(null);
        return builder.build();
    }

    public static ConnectorMessage createRelayRMMDAcceptanceEvidenceMessage() {
        var evidence = EvidenceUtil.createRelayREMMDAcceptanceEvidence();
        var evidences = new ArrayList<ConnectorEvidence>();
        evidences.add(evidence);
        var builder = createValidInboundBusinessMessage().toBuilder();
        builder.evidences(evidences);
        builder.transportedEvidences(evidences);
        builder.content(null);
        return builder.build();
    }

    public static ConnectorMessage createNonDeliveryEvidenceMessage() {
        var evidence = EvidenceUtil.createNonDeliveryEvidence();
        var evidences = new ArrayList<ConnectorEvidence>();
        evidences.add(evidence);
        var builder = createValidInboundBusinessMessage().toBuilder();
        builder.evidences(evidences);
        builder.transportedEvidences(evidences);
        builder.content(null);
        return builder.build();
    }

    public static ConnectorMessage createEvidenceTriggerMessage() {
        var evidence = EvidenceUtil.createEvidenceTrigger();
        var evidences = new ArrayList<ConnectorEvidence>();
        evidences.add(evidence);
        var builder = backendToGatewayMessage();
        builder.evidences(evidences);
        builder.transportedEvidences(evidences);
        builder.content(null);
        return builder.build();
    }

    public static ConnectorMessage createRejectedMessage() {
        var evidence = EvidenceUtil.createSubmissionRejectionEvidence();
        var evidences = new ArrayList<ConnectorEvidence>();
        evidences.add(evidence);
        var builder = backendToGatewayMessage();
        builder.evidences(evidences);
        builder.transportedEvidences(evidences);
        builder.content(null);
        builder.rejectedAt(Instant.now());
        return builder.build();
    }

    public static ConnectorMessage createConfirmedMessage() {
        var evidence = EvidenceUtil.createSubmissionAcceptanceEvidence();
        var evidences = new ArrayList<ConnectorEvidence>();
        evidences.add(evidence);
        var builder = backendToGatewayMessage();
        builder.evidences(evidences);
        builder.transportedEvidences(evidences);
        builder.content(null);
        builder.confirmedAt(Instant.now());
        return builder.build();
    }

    // is considered as an outgoing message
    private static ConnectorMessage.ConnectorMessageBuilder backendToGatewayMessage() {
        return ConnectorMessage
                .builder()
                .businessDomainIdentifier(
                        BusinessDomainUtil.createDefaultBusinessDomain()
                                          .identifier()
                )
                .uuid("223caef9-cae9-4387-a38c-ad4879f94b4e")
                .identifier("223caef9-cae9-4387-a38c-ad4879f94b4e@connector.ecodex.eu")
                .backendMessageIdentifier(
                        "85964ab5-b04b-4d45-97d1-962b565e22df@connector.ecodex.eu")
                .backendName("default_backend")
                .gatewayName("default_gateway")
                .as4Properties(defaultASProperties().build())
                .direction(ConnectorMessageDirection.BACKEND_TO_GATEWAY)
                .evidences(new ArrayList<>())
                .content(ConnectorMessageContent.builder().build())
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
                .fromParty(PartyUtil.createFromParty())
                .toParty(PartyUtil.createToParty())
                .service(ServiceUtil.createService())
                .action(ActionUtil.createAction());
    }
}
