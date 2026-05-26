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

import eu.ecodex.connector.application.service.usecase.link.ConnectorLinkSubmitter;
import eu.ecodex.connector.application.service.usecase.message.ConnectorGatewayConfirmationMessageProcessor;
import eu.ecodex.connector.application.service.usecase.message.ConnectorMessageEvidenceVerifier;
import eu.ecodex.connector.domain.exception.ConnectorEvidenceNotRelevantException;
import eu.ecodex.connector.domain.model.message.ConnectorMessage;
import eu.ecodex.connector.domain.model.message.evidence.ConnectorMessageEvidence;
import eu.ecodex.connector.domain.spi.message.ConnectorMessageEvidenceRepository;
import eu.ecodex.connector.domain.spi.message.ConnectorMessageRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * Implementation of the {@link ConnectorGatewayConfirmationMessageProcessor} service.
 */
@Slf4j
@Service
public class ConnectorGatewayConfirmationMessageProcessorService
        implements ConnectorGatewayConfirmationMessageProcessor {
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
    public ConnectorGatewayConfirmationMessageProcessorService(
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
        if (!isTransportedEvidenceConfirmation(confirmationMessage)) {
            throw new IllegalArgumentException(
                    "Message [" + confirmationMessage.identifier()
                            + "] is not a gateway confirmation"
            );
        }

        var referencedMessage = findReferencedMessage(confirmationMessage);
        if (referencedMessage != null) {
            applyEvidencesToReferencedMessage(confirmationMessage, referencedMessage);
            forwardToBackend(confirmationMessage, referencedMessage);
            return;
        }

        log.warn(
                "Referenced business message not found for confirmation message [{}], forwarding "
                + "without lifecycle update",
                confirmationMessage.identifier()
        );
        linkSubmitter.submit(confirmationMessage);
    }

    private ConnectorMessage findReferencedMessage(ConnectorMessage confirmationMessage) {
        var referenceToMessageId = confirmationMessage.as4Properties().referenceToIdentifier();
        if (!StringUtils.hasText(referenceToMessageId)) {
            log.warn(
                    "Confirmation message [{}] has no refToMessageId, skipping lifecycle update",
                    confirmationMessage.identifier()
            );
            return null;
        }

        return messageRepository.findByEbmsMessageIdentifier(referenceToMessageId);
    }

    private void applyEvidencesToReferencedMessage(
            ConnectorMessage confirmationMessage,
            ConnectorMessage referencedMessage) {
        log.info(
                "Applying transported evidences from confirmation message [{}] to referenced "
                + "business message [{}]",
                confirmationMessage.identifier(),
                referencedMessage.identifier()
        );

        var accumulatedEvidences = referencedMessage.evidences() != null
                ? new ArrayList<>(referencedMessage.evidences())
                : new ArrayList<ConnectorMessageEvidence>();

        for (var incomingEvidence : confirmationMessage.transportedEvidences()) {
            applyEvidence(referencedMessage, accumulatedEvidences, incomingEvidence);
            referencedMessage = messageRepository.findByIdentifier(referencedMessage.identifier());
        }
    }

    private void applyEvidence(
            ConnectorMessage referencedMessage,
            List<ConnectorMessageEvidence> accumulatedEvidences,
            ConnectorMessageEvidence incomingEvidence) {
        var evidencesForVerification = new ArrayList<>(accumulatedEvidences);
        evidencesForVerification.add(incomingEvidence);

        var messageForVerification = referencedMessage.toBuilder()
                .transportedEvidences(evidencesForVerification)
                .build();

        try {
            evidenceVerifier.verify(incomingEvidence.type(), messageForVerification);
            evidenceRepository.save(incomingEvidence, referencedMessage.identifier());
            accumulatedEvidences.add(incomingEvidence);
        } catch (ConnectorEvidenceNotRelevantException e) {
            log.info(
                    "Evidence [{}] ignored for referenced message [{}]: {}",
                    incomingEvidence.type(),
                    referencedMessage.identifier(),
                    e.getMessage()
            );
        }
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

        var messageForBackend = messageRepository.updateBackendContext(
                confirmationMessage.identifier(),
                referencedMessage.backendName(),
                backendMessageIdentifier
        );

        log.info(
                "Forwarding confirmation message [{}] to backend [{}]",
                messageForBackend.identifier(),
                messageForBackend.backendName()
        );
        linkSubmitter.submit(messageForBackend);
    }

    private static boolean isTransportedEvidenceConfirmation(ConnectorMessage message) {
        return message.businessContent() == null
               && message.transportedEvidences() != null
               && !message.transportedEvidences().isEmpty();
    }
}
