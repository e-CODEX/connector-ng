/*
 * Copyright 2025 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.domain.service;

import eu.ecodex.connector.domain.annotation.DomainService;
import eu.ecodex.connector.domain.api.service.ConnectorMessageService;
import eu.ecodex.connector.domain.exception.ConnectorMessageIdentifierException;
import eu.ecodex.connector.domain.exception.ConnectorMessageNotBusinessException;
import eu.ecodex.connector.domain.exception.ConnectorMessageNotFoundException;
import eu.ecodex.connector.domain.exception.ConnectorMessagePartyException;
import eu.ecodex.connector.domain.model.message.ConnectorMessage;
import eu.ecodex.connector.domain.model.message.ConnectorMessageAS4Properties;
import eu.ecodex.connector.domain.model.message.ConnectorMessageDirection;
import eu.ecodex.connector.domain.model.message.evidence.ConnectorEvidence;
import eu.ecodex.connector.domain.model.message.evidence.ConnectorEvidenceType;
import eu.ecodex.connector.domain.model.message.evidence.EvidenceAction;
import eu.ecodex.connector.domain.model.pmode.ConnectorAction;
import eu.ecodex.connector.domain.model.pmode.ConnectorPartyRoleType;
import eu.ecodex.connector.domain.spi.ConnectorMessageRepository;
import eu.ecodex.connector.domain.spi.property.ConnectorMessageProcessingConfigProvider;
import jakarta.annotation.Nonnull;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;

/**
 * e-Codex Connector message service implementation.
 */
@Slf4j
@DomainService
public class ConnectorMessageServiceImpl implements ConnectorMessageService {
    private final ConnectorMessageRepository messageRepository;
    private final ConnectorMessageProcessingConfigProvider messageProcessingConfigProvider;

    /**
     * Creates an instance of {@code ConnectorMessageServiceImpl}.
     *
     * @param messageRepository               the repository responsible for managing connector
     *                                        messages; must not be null.
     * @param messageProcessingConfigProvider the provider of configurations for message processing;
     *                                        must not be null.
     */
    public ConnectorMessageServiceImpl(
            ConnectorMessageRepository messageRepository,
            ConnectorMessageProcessingConfigProvider messageProcessingConfigProvider) {
        this.messageRepository = messageRepository;
        this.messageProcessingConfigProvider = messageProcessingConfigProvider;
    }

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
    public @NonNull ConnectorMessage register(@NonNull ConnectorMessage message) {
        var existingMessage = this.messageRepository.findByIdentifier(message.identifier());

        if (existingMessage != null) {
            log.warn("message uuid already exists");

            throw new ConnectorMessageIdentifierException("message uuid already exists");
        }

        return this.messageRepository.save(message);
    }

    @Override
    public ConnectorMessage createEvidenceMessage(
            @Nonnull ConnectorMessage businessMessage, @Nonnull ConnectorEvidence evidence) {
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
                .identifier(this.generateMessageIdentifier())
                .businessDomainIdentifier(businessMessage.businessDomainIdentifier())
                .referenceToBackendMessageIdentifier(businessMessage.backendMessageIdentifier())
                .direction(businessMessage.direction())
                .backendName(businessMessage.backendName())
                .gatewayName(businessMessage.gatewayName())
                .as4Properties(as4Properties)
                .evidences(Collections.singletonList(evidence))
                .transportedEvidences(Collections.singletonList(evidence))
                .build();
    }

    @Override
    public ConnectorMessage findByIdentifier(@NonNull String identifier) {
        log.debug("finding message with identifier: [{}]", identifier);

        var message = this.messageRepository.findByIdentifier(identifier);
        if (message == null) {
            throw new ConnectorMessageNotFoundException("message not found");
        }
        return message;
    }

    @Override
    public ConnectorMessage findByIdentifierAndDirection(
            @NonNull ConnectorMessage message, @NonNull ConnectorMessageDirection direction) {
        log.debug("finding message with identifier: [{}] and direction: [{}]", message, direction);

        var foundMessage = this.messageRepository.findByIdentifierAndDirection(message, direction);

        if (foundMessage == null) {
            throw new ConnectorMessageNotFoundException("message not found");
        }

        return foundMessage;
    }

    @Override
    public List<ConnectorMessage> findByConversationIdentifier(
            @NonNull String conversationIdentifier) {
        return this.messageRepository.findByConversationIdentifier(conversationIdentifier);
    }

    @Override
    public ConnectorMessage addEvidence(
            @NonNull ConnectorMessage message, @NonNull ConnectorEvidence evidence) {
        log.debug("adding evidence [{}] to message: [{}]", evidence, message);

        if (!this.isBusinessMessage(message)) {
            throw new ConnectorMessageNotBusinessException("message must be a business message");
        }

        var existingMessage = this.messageRepository.findByIdentifier(message.identifier());

        if (existingMessage == null) {
            throw new ConnectorMessageNotFoundException("message not found");
        }
        // TODO set evidence to global and business evidences
        return this.messageRepository.addEvidence(existingMessage, evidence);
    }

    @Override
    public void checkPartiesInfo(@NonNull ConnectorMessage message) {
        log.debug("checking message [{}] parties info", message);

        final var as4Properties = message.as4Properties();
        final var fromParty = as4Properties.fromParty();
        final var toParty = as4Properties.toParty();

        if (fromParty == null || toParty == null) {
            throw new ConnectorMessagePartyException("message must have 'from' and 'to' parties");
        }

        var direction = message.direction();
        switch (direction) {
            case BACKEND_TO_GATEWAY -> {
                if (fromParty.roleType() != ConnectorPartyRoleType.INITIATOR) {
                    throw new ConnectorMessagePartyException(
                            "message 'fromParty' roleType must be INITIATOR but was "
                            + fromParty.roleType()
                    );
                }

                if (toParty.roleType() != ConnectorPartyRoleType.RESPONDER) {
                    throw new ConnectorMessagePartyException(
                            "message 'toParty' roleType must be RESPONDER but was "
                            + toParty.roleType()
                    );
                }
            }
            case GATEWAY_TO_BACKEND ->
                    throw new UnsupportedOperationException("not implemented yet");
            default -> throw new AssertionError("unreachable: unexpected direction: " + direction);
        }
    }

    @Override
    public boolean isBusinessMessage(@NonNull ConnectorMessage message) {
        return !isEvidenceMessage(message);
    }

    @Override
    public boolean isEvidenceMessage(@NonNull ConnectorMessage message) {
        return message.content() == null
               && message.evidences() != null
               && !message.evidences().isEmpty();
    }

    @Override
    public boolean isEvidenceTriggerMessage(@NonNull ConnectorMessage message) {
        return message.content() == null
               && message.evidences() != null
               && message.evidences().size() == 1
               && ArrayUtils.isEmpty(message.evidences().getFirst().content());
    }

    @Override
    public ConnectorMessage setAsRejected(@NonNull ConnectorMessage message) {
        log.debug("setting message [{}] as rejected", message);

        var foundMessage = this.findByIdentifier(message.identifier());

        return this.messageRepository.setAsRejected(foundMessage);
    }

    @Override
    public ConnectorMessage setAsConfirmed(@NonNull ConnectorMessage message) {
        log.debug("setting message [{}] as confirmed", message);

        var foundMessage = this.findByIdentifier(message.identifier());

        return this.messageRepository.setAsConfirmed(foundMessage);
    }

    @Override
    public boolean isRejected(@NonNull ConnectorMessage message) {
        var foundMessage = this.findByIdentifier(message.identifier());

        return foundMessage.rejectedAt() != null;
    }

    @Override
    public ConnectorMessage switchDirection(@Nonnull ConnectorMessage message) {
        log.debug("switching message direction: [{}]", message);

        final var as4Properties = message.as4Properties();
        final var direction = message.direction();
        final var fromParty = as4Properties.fromParty();
        final var toParty = as4Properties.toParty();

        var switchedAS4PropertiesBuilder = message.as4Properties().toBuilder();
        // switching party, but keep Role and RoleType
        final var switchedFromParty = toParty.toBuilder()
                                             .roleType(ConnectorPartyRoleType.INITIATOR)
                                             .role(fromParty.role())
                                             .build();

        final var switchedToParty = fromParty.toBuilder()
                                             .roleType(ConnectorPartyRoleType.RESPONDER)
                                             .role(toParty.role())
                                             .build();

        switchedAS4PropertiesBuilder.fromParty(switchedFromParty);
        switchedAS4PropertiesBuilder.toParty(switchedToParty);
        switchedAS4PropertiesBuilder.originalSender(as4Properties.finalRecipient());
        switchedAS4PropertiesBuilder.finalRecipient(as4Properties.originalSender());

        var switchedMessageBuilder = message.toBuilder();
        switchedMessageBuilder.direction(
                ConnectorMessageDirection.from(direction.getTarget(), direction.getSource())
        );
        switchedMessageBuilder.as4Properties(switchedAS4PropertiesBuilder.build());

        final var switchedMessage = switchedMessageBuilder.build();

        log.info(
                "message [{}] direction has been successfully switched to [{}]", message,
                switchedMessage
        );

        return switchedMessage;
    }

    @Override
    public ConnectorMessage assignEbmsIdentifier(@NonNull ConnectorMessage message) {
        log.debug("assigning EBMS identifier to message: [{}]", message);

        var processingProperties = this.messageProcessingConfigProvider.getProcessingProperties();
        var ebmsIdentifier = String.format(
                "%s@%s", UUID.randomUUID(), processingProperties.ebmsIdSuffix()
        );
        var as4Properties = message.as4Properties()
                                   .toBuilder()
                                   .ebmsMessageIdentifier(ebmsIdentifier)
                                   .build();

        return message.toBuilder()
                      .as4Properties(as4Properties)
                      .build();
    }

    private String generateMessageIdentifier() {
        var processingProperties = this.messageProcessingConfigProvider.getProcessingProperties();
        return String.format(
                "%s@%s", UUID.randomUUID(), processingProperties.identifierSuffix()
        );
    }
}
