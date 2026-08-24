/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.service.message;

import eu.ecodex.connector.application.exception.ConnectorEvidenceException;
import eu.ecodex.connector.application.exception.ConnectorMessageNotFoundException;
import eu.ecodex.connector.application.port.api.message.ConnectorTriggeredEvidenceMessageVerifier;
import eu.ecodex.connector.application.port.spi.message.ConnectorMessageRepository;
import eu.ecodex.connector.domain.model.message.ConnectorBusinessMessage;
import eu.ecodex.connector.domain.model.message.ConnectorMessageDirection;
import eu.ecodex.connector.domain.model.message.ConnectorTriggeredEvidenceMessage;
import jakarta.annotation.Nonnull;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * Default implementation of the {@link ConnectorTriggeredEvidenceMessageVerifier} interface.
 */
@Service
public class ConnectorTriggeredEvidenceMessageVerifierService
    implements ConnectorTriggeredEvidenceMessageVerifier {
    private static final ConnectorMessageDirection EXPECTED_BUSINESS_MSG_DIRECTION =
        ConnectorMessageDirection.GATEWAY_TO_BACKEND;

    private final ConnectorMessageRepository messageRepository;

    public ConnectorTriggeredEvidenceMessageVerifierService(
        ConnectorMessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @Override
    public void verify(@Nonnull ConnectorTriggeredEvidenceMessage triggeredEvidenceMessage) {
        var referenceToMessageId = resolveReferenceToMessageIdentifier(
            triggeredEvidenceMessage.referenceToBackendMessageIdentifier(),
            triggeredEvidenceMessage.referenceToIdentifier()
        );

        var direction = triggeredEvidenceMessage.direction();

        validateTriggerMessage(referenceToMessageId, direction);

        var businessMessage = findReferencedBusinessMessage(referenceToMessageId, direction);
        checkRelatedBusinessMessageDirection(businessMessage);
    }

    private String resolveReferenceToMessageIdentifier(
        String referenceToBackendMessageIdentifier,
        String referenceToIdentifier) {
        return StringUtils.hasText(referenceToIdentifier)
               ? referenceToIdentifier
               : referenceToBackendMessageIdentifier;
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
    private ConnectorBusinessMessage findReferencedBusinessMessage(
        String referenceToMessageId,
        ConnectorMessageDirection triggerDirection) {
        var relatedBusinessMessage = messageRepository.findReferencedBusinessMessage(
            referenceToMessageId, triggerDirection);

        if (relatedBusinessMessage == null) {
            throw new ConnectorMessageNotFoundException(
                "Referenced business message not found for evidence trigger message with ref ["
                    + referenceToMessageId + "]");
        }

        return relatedBusinessMessage;
    }

    private void checkRelatedBusinessMessageDirection(ConnectorBusinessMessage businessMessage) {
        var direction = businessMessage.direction();

        if (direction != EXPECTED_BUSINESS_MSG_DIRECTION) {
            throw new ConnectorEvidenceException(
                "Evidence trigger related business message direction must be ["
                    + EXPECTED_BUSINESS_MSG_DIRECTION
                    + "] but was [" + direction + "]");
        }
    }
}
