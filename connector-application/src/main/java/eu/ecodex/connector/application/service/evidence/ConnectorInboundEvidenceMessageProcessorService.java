/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.service.evidence;

import eu.ecodex.connector.application.exception.ConnectorEvidenceNotRelevantException;
import eu.ecodex.connector.application.port.api.evidence.ConnectorInboundEvidenceMessageProcessor;
import eu.ecodex.connector.application.port.api.link.ConnectorLinkSubmitter;
import eu.ecodex.connector.application.port.api.message.ConnectorMessageEvidenceVerifier;
import eu.ecodex.connector.application.port.spi.message.ConnectorMessageEvidenceRepository;
import eu.ecodex.connector.application.port.spi.message.ConnectorMessageRepository;
import eu.ecodex.connector.domain.model.message.ConnectorMessage;
import eu.ecodex.connector.domain.model.message.ConnectorMessageDirection;
import eu.ecodex.connector.domain.model.message.evidence.ConnectorMessageEvidence;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * Implementation of the {@link ConnectorInboundEvidenceMessageProcessor} service.
 */
@Slf4j
@Service
@Transactional
public class ConnectorInboundEvidenceMessageProcessorService
    implements ConnectorInboundEvidenceMessageProcessor {
    private final ConnectorMessageRepository messageRepository;
    private final ConnectorMessageEvidenceRepository evidenceRepository;
    private final ConnectorMessageEvidenceVerifier evidenceVerifier;
    private final ConnectorLinkSubmitter linkSubmitter;

    /**
     * Creates the processor with message, evidence, and backend forwarding dependencies.
     *
     * @param messageRepository  persisted connector messages
     * @param evidenceRepository persisted message evidences
     * @param evidenceVerifier   validates evidence applicability
     * @param linkSubmitter      forwards messages to the backend
     */
    public ConnectorInboundEvidenceMessageProcessorService(
        ConnectorMessageRepository messageRepository,
        ConnectorMessageEvidenceRepository evidenceRepository,
        ConnectorMessageEvidenceVerifier evidenceVerifier,
        ConnectorLinkSubmitter linkSubmitter) {
        this.messageRepository = messageRepository;
        this.evidenceRepository = evidenceRepository;
        this.evidenceVerifier = evidenceVerifier;
        this.linkSubmitter = linkSubmitter;
    }

    @Override
    public void process(@NonNull ConnectorMessage confirmationMessage) {
        if (!confirmationMessage.isEvidenceMessage()) {
            throw new IllegalArgumentException(
                "Message [" + confirmationMessage.identifier()
                    + "] is not a gateway confirmation"
            );
        }

        var referencedBusinessMessage = findReferencedBusinessMessage(confirmationMessage);

        if (referencedBusinessMessage != null) {
            var appliedEvidences = applyEvidencesToReferencedBusinessMessage(
                confirmationMessage,
                referencedBusinessMessage
            );

            forwardToBackend(
                confirmationMessage.toBuilder().transportedEvidences(appliedEvidences).build(),
                referencedBusinessMessage
            );

            return;
        }

        log.warn(
            "Referenced business message not found for confirmation message [{}], forwarding "
                + "without lifecycle update",
            confirmationMessage.identifier()
        );

        linkSubmitter.submit(confirmationMessage);
    }

    private ConnectorMessage findReferencedBusinessMessage(ConnectorMessage confirmationMessage) {
        var referenceToMessageId = confirmationMessage.as4Properties().referenceToIdentifier();
        if (!StringUtils.hasText(referenceToMessageId)) {
            log.warn(
                "Confirmation message [{}] has no refToMessageId, skipping lifecycle update",
                confirmationMessage.identifier()
            );

            return null;
        }

        // the sorting by criteria because two messages can have the same ebms identifier
        return messageRepository.findByEbmsMessageIdentifierAndDirection(
            referenceToMessageId,
            ConnectorMessageDirection.revert(confirmationMessage.direction())
        );
    }

    private List<ConnectorMessageEvidence> applyEvidencesToReferencedBusinessMessage(
        ConnectorMessage confirmationMessage,
        ConnectorMessage referencedBusinessMessage) {
        var referencedBusinessMessageIdentifier = referencedBusinessMessage.identifier();
        log.info(
            "Applying transported evidences from confirmation message [{}] to referenced "
                + "business message [{}]",
            confirmationMessage.identifier(),
            referencedBusinessMessageIdentifier
        );

        var accumulatedEvidences = referencedBusinessMessage.evidences() != null
            ? new ArrayList<>(referencedBusinessMessage.evidences())
            : new ArrayList<ConnectorMessageEvidence>();


        var appliedEvidences = new LinkedList<ConnectorMessageEvidence>();

        // confirmationMessage.transportedEvidences() cannot be null because
        // of the !confirmationMessage.isEvidenceMessage() in the process method
        for (var incomingEvidence : confirmationMessage.transportedEvidences()) {
            var appliedEvidence = applyEvidence(
                referencedBusinessMessage,
                accumulatedEvidences,
                incomingEvidence
            );
            appliedEvidences.add(appliedEvidence);
            referencedBusinessMessage = messageRepository.findByIdentifier(
                referencedBusinessMessageIdentifier);
        }

        return appliedEvidences;
    }

    private ConnectorMessageEvidence applyEvidence(
        ConnectorMessage referencedMessage,
        List<ConnectorMessageEvidence> accumulatedEvidences,
        ConnectorMessageEvidence incomingEvidence) {
        var evidencesForVerification = new ArrayList<>(accumulatedEvidences);
        evidencesForVerification.add(incomingEvidence);

        var messageForVerification = referencedMessage
            .toBuilder()
            .transportedEvidences(evidencesForVerification)
            .build();

        try {
            evidenceVerifier.verify(incomingEvidence.type(), messageForVerification);
            var persistedEvidence = evidenceRepository.save(
                incomingEvidence,
                referencedMessage.identifier()
            );
            accumulatedEvidences.add(incomingEvidence);

            return persistedEvidence;
        } catch (ConnectorEvidenceNotRelevantException e) {
            log.info(
                "Evidence [{}] ignored for referenced message [{}]: {}",
                incomingEvidence.type(),
                referencedMessage.identifier(),
                e.getMessage()
            );
        }

        return incomingEvidence;
    }

    private void forwardToBackend(
        ConnectorMessage confirmationMessage,
        ConnectorMessage referencedMessage) {
        var backendMessageIdentifier = referencedMessage.backendMessageIdentifier();

        if (!StringUtils.hasText(backendMessageIdentifier)) {
            throw new IllegalStateException(
                "Referenced message [" + referencedMessage.identifier()
                    + "] has no backend message identifier"
            );
        }

        var messageForBackend = confirmationMessage
            .toBuilder()
            .backendName(referencedMessage.backendName())
            .referenceToBackendMessageIdentifier(backendMessageIdentifier)
            .as4Properties(
                confirmationMessage.as4Properties()
                                   .toBuilder()
                                   .referenceToIdentifier(backendMessageIdentifier)
                                   .build()
            )
            .build();

        log.info(
            "Forwarding confirmation message [{}] to backend [{}]",
            messageForBackend.identifier(),
            messageForBackend.backendName()
        );

        linkSubmitter.submit(messageForBackend);
    }
}
