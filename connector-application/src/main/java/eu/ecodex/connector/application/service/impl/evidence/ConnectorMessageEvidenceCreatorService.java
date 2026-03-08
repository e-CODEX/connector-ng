/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.service.impl.evidence;

import eu.ecodex.connector.application.service.usecase.evidence.ConnectorMessageEvidenceCreator;
import eu.ecodex.connector.domain.api.ConnectorEvidenceToolkit;
import eu.ecodex.connector.domain.model.ConnectorMessageRejectionReason;
import eu.ecodex.connector.domain.model.message.ConnectorMessage;
import eu.ecodex.connector.domain.model.message.evidence.ConnectorEvidenceType;
import eu.ecodex.connector.domain.model.message.evidence.ConnectorMessageEvidence;
import eu.ecodex.connector.domain.spi.ConnectorMessageEvidenceRepository;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Implementation of the {@link ConnectorMessageEvidenceCreator} service.
 */
@Slf4j
@Component
public class ConnectorMessageEvidenceCreatorService implements ConnectorMessageEvidenceCreator {
    private final ConnectorEvidenceToolkit evidenceToolkit;
    private final ConnectorMessageEvidenceRepository evidenceRepository;

    public ConnectorMessageEvidenceCreatorService(
            ConnectorEvidenceToolkit evidenceToolkit,
            ConnectorMessageEvidenceRepository evidenceRepository) {
        this.evidenceToolkit = evidenceToolkit;
        this.evidenceRepository = evidenceRepository;
    }

    @Override
    public ConnectorMessageEvidence createSuccess(
            @NonNull ConnectorEvidenceType evidenceType,
            @NonNull ConnectorMessage message) {
        log.debug(
                "creating success evidence for message [{}] with type [{}]", message, evidenceType);
        return this.create(evidenceType, message, null);
    }

    @Override
    public ConnectorMessageEvidence createFailure(
            @NonNull ConnectorEvidenceType evidenceType,
            @NonNull ConnectorMessage message,
            ConnectorMessageRejectionReason reason) {
        log.debug(
                "creating failure evidence for message [{}] with type [{}]", message, evidenceType);
        return this.create(evidenceType, message, reason);
    }

    private ConnectorMessageEvidence create(
            ConnectorEvidenceType evidenceType,
            ConnectorMessage message,
            ConnectorMessageRejectionReason rejectionReason) {
        var evidence = this.evidenceToolkit.create(message, evidenceType, rejectionReason);

        return this.evidenceRepository.save(evidence, message.identifier());
    }
}
