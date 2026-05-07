/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.service.impl.message;

import eu.ecodex.connector.application.service.usecase.message.ConnectorEvidenceMessageCreator;
import eu.ecodex.connector.domain.model.message.ConnectorMessage;
import eu.ecodex.connector.domain.model.message.ConnectorMessageAS4Properties;
import eu.ecodex.connector.domain.model.message.evidence.ConnectorEvidenceType;
import eu.ecodex.connector.domain.model.message.evidence.ConnectorMessageEvidence;
import eu.ecodex.connector.domain.model.message.evidence.EvidenceAction;
import eu.ecodex.connector.domain.model.pmode.ConnectorAction;
import jakarta.annotation.Nonnull;
import lombok.NonNull;
import org.springframework.stereotype.Component;

/**
 * Implementation of the {@link ConnectorEvidenceMessageCreator} service.
 */
@Component
public class ConnectorEvidenceMessageCreatorService implements ConnectorEvidenceMessageCreator {
    /**
     * Retrieves the corresponding {@code ConnectorAction} for a given
     * {@code ConnectorEvidenceType}.
     *
     * @param evidenceType the type of evidence for which the corresponding action is to be fetched.
     *                     Must not be null.
     *
     * @return the {@code ConnectorAction} associated with the specified
     *         {@code ConnectorEvidenceType}. This action is mapped based on predefined rules for
     *         the evidence type.
     */
    public static ConnectorAction getEvidenceAction(@NonNull ConnectorEvidenceType evidenceType) {
        return switch (evidenceType) {
            case SUBMISSION_ACCEPTANCE -> EvidenceAction.submissionAcceptance.toConnectionAction();
            case SUBMISSION_REJECTION -> EvidenceAction.submissionRejection.toConnectionAction();
            case RELAY_REMMD_ACCEPTANCE -> EvidenceAction.relayREEMDAcceptance.toConnectionAction();
            case RELAY_REMMD_REJECTION -> EvidenceAction.relayREEMDRejection.toConnectionAction();
            case RELAY_REMMD_FAILURE -> EvidenceAction.relayREMMDFailure.toConnectionAction();
            case DELIVERY -> EvidenceAction.delivery.toConnectionAction();
            case NON_DELIVERY -> EvidenceAction.nonDelivery.toConnectionAction();
            case RETRIEVAL -> EvidenceAction.retrieval.toConnectionAction();
            case NON_RETRIEVAL -> EvidenceAction.nonRetrieval.toConnectionAction();
        };
    }

    @Override
    public ConnectorMessage create(
            @Nonnull ConnectorMessage businessMessage,
            @NonNull ConnectorMessageEvidence evidence) {
        // TODO check fromParty, toParty, service nullability

        var action = getEvidenceAction(evidence.type());

        var as4Properties = ConnectorMessageAS4Properties
                .builder()
                .conversationIdentifier(businessMessage.as4Properties().conversationIdentifier())
                .ebmsMessageIdentifier(businessMessage.as4Properties().ebmsMessageIdentifier())
                .finalRecipient(businessMessage.as4Properties().finalRecipient())
                .originalSender(businessMessage.as4Properties().originalSender())
                .fromParty(businessMessage.as4Properties().fromParty().toBuilder().build())
                .toParty(businessMessage.as4Properties().toParty().toBuilder().build())
                .referenceToIdentifier(businessMessage.as4Properties().ebmsMessageIdentifier())
                .service(businessMessage.as4Properties().service().toBuilder().build())
                .action(action)
                .build();

        // TODO check if caused by should be set to connector message definition
        return ConnectorMessage
                .builder()
                .uuid(businessMessage.uuid())
                .identifier(businessMessage.identifier())
                .backendMessageIdentifier(businessMessage.backendMessageIdentifier())
                .businessDomainIdentifier(businessMessage.businessDomainIdentifier())
                .referenceToBackendMessageIdentifier(businessMessage.backendMessageIdentifier())
                .direction(businessMessage.direction())
                .backendName(businessMessage.backendName())
                .gatewayName(businessMessage.gatewayName())
                .as4Properties(as4Properties)
                .build();
    }
}
