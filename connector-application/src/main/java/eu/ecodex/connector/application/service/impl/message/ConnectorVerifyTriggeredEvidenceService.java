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

import eu.ecodex.connector.application.service.usecase.message.ConnectorVerifyTriggeredEvidence;
import eu.ecodex.connector.domain.exception.ConnectorEvidenceException;
import eu.ecodex.connector.domain.exception.ConnectorMessageNotFoundException;
import eu.ecodex.connector.domain.model.message.ConnectorMessage;
import eu.ecodex.connector.domain.model.message.ConnectorMessageDirection;
import eu.ecodex.connector.domain.spi.message.ConnectorMessageRepository;
import lombok.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * Default implementation of the {@link ConnectorVerifyTriggeredEvidence} interface.
 */
@Service
public class ConnectorVerifyTriggeredEvidenceService implements ConnectorVerifyTriggeredEvidence {
    private static final ConnectorMessageDirection EXPECTED_BUSINESS_MSG_DIRECTION =
            ConnectorMessageDirection.GATEWAY_TO_BACKEND;

    private final ConnectorMessageRepository messageRepository;

    public ConnectorVerifyTriggeredEvidenceService(
            ConnectorMessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @Override
    public void verify(@NonNull ConnectorMessage triggerMessage) {
        var referenceToMessageId = resolveReferenceToMessageId(triggerMessage);
        var triggerDirection = triggerMessage.direction();

        validateTriggerMessage(referenceToMessageId, triggerDirection);

        var businessMessage = findReferencedBusinessMessage(referenceToMessageId, triggerDirection);
        checkRelatedBusinessMessageDirection(businessMessage);
    }

    private String resolveReferenceToMessageId(ConnectorMessage triggerMessage) {
        var identifier = triggerMessage.as4Properties().referenceToIdentifier();

        return StringUtils.hasText(identifier)
                ? identifier
                : triggerMessage.referenceToBackendMessageIdentifier();
    }

    private void validateTriggerMessage(
            String referenceToMessageId,
            ConnectorMessageDirection direction) {
        if (!StringUtils.hasText(referenceToMessageId)) {
            throw new ConnectorEvidenceException(
                    "Evidence trigger must set refToMessageId to the referenced business message");
        }

        if (direction == null) {
            throw new ConnectorEvidenceException(
                    "Evidence trigger must set direction to the referenced business message");
        }
    }

    /**
     * Looks up the business message in priority order.
     *
     * <ol>
     *   <li>EBMS message identifier and inverted direction</li>
     *   <li>Backend message identifier</li>
     *   <li>Connector-internal identifier</li>
     * </ol>
     */
    private ConnectorMessage findReferencedBusinessMessage(
            String referenceToMessageId,
            ConnectorMessageDirection triggerDirection) {
        var invertedDirection = ConnectorMessageDirection.revert(triggerDirection);

        var relatedBusinessMessage = messageRepository.findByEbmsMessageIdentifierAndDirection(
                referenceToMessageId, invertedDirection);

        if (relatedBusinessMessage == null) {
            relatedBusinessMessage = messageRepository.findByBackendMessageIdentifier(
                    referenceToMessageId);
        }

        if (relatedBusinessMessage == null) {
            relatedBusinessMessage = messageRepository.findByIdentifier(referenceToMessageId);
        }

        if (relatedBusinessMessage == null) {
            throw new ConnectorMessageNotFoundException(
                    "Referenced business message not found for evidence trigger message with ref ["
                            + referenceToMessageId + "]");
        }

        return relatedBusinessMessage;
    }

    private void checkRelatedBusinessMessageDirection(ConnectorMessage businessMessage) {
        var direction = businessMessage.direction();

        if (direction == null) {
            throw new IllegalStateException(
                    "The business message direction cannot be null for a connector message");
        }
        
        if (direction != EXPECTED_BUSINESS_MSG_DIRECTION) {
            throw new ConnectorEvidenceException(
                    "Evidence trigger related business message direction must be ["
                            + EXPECTED_BUSINESS_MSG_DIRECTION
                            + "] but was [" + direction + "]");
        }
    }
}
