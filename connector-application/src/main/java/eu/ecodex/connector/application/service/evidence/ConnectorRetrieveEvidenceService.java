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

import eu.ecodex.connector.application.exception.ConnectorEvidenceNotFoundException;
import eu.ecodex.connector.application.port.api.evidence.ConnectorRetrieveEvidence;
import eu.ecodex.connector.application.port.spi.message.ConnectorMessageEvidenceRepository;
import eu.ecodex.connector.domain.model.message.evidence.ConnectorMessageEvidence;
import lombok.NonNull;
import org.springframework.stereotype.Service;

/**
 * Implementation of the {@link ConnectorRetrieveEvidence} service.
 */
@Service
public class ConnectorRetrieveEvidenceService implements ConnectorRetrieveEvidence {
    private final ConnectorMessageEvidenceRepository evidenceRepository;

    public ConnectorRetrieveEvidenceService(ConnectorMessageEvidenceRepository evidenceRepository) {
        this.evidenceRepository = evidenceRepository;
    }

    @Override
    public ConnectorMessageEvidence execute(@NonNull String uuid) {
        var evidence = this.evidenceRepository.findByUuid(uuid);

        if (evidence == null) {
            throw new ConnectorEvidenceNotFoundException("Evidence not found");
        }

        return evidence;
    }
}
