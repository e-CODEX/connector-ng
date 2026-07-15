/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.inbound.web.rest.controller.evidence;

import eu.ecodex.connector.application.service.usecase.evidence.ConnectorRetrieveEvidence;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

/**
 * Defines the REST controller for managing connector messages evidence.
 */
@RestController
public class ConnectorEvidenceController implements ConnectorEvidenceApi {
    private final ConnectorRetrieveEvidence retrieveEvidenceService;

    public ConnectorEvidenceController(ConnectorRetrieveEvidence retrieveEvidenceService) {
        this.retrieveEvidenceService = retrieveEvidenceService;
    }

    @Override
    public ResponseEntity<byte[]> download(String uuid) {
        var evidence = retrieveEvidenceService.execute(uuid);

        return ResponseEntity.ok()
                             .contentType(MediaType.APPLICATION_XML)
                             .header(
                                     HttpHeaders.CONTENT_DISPOSITION,
                                     "attachment; filename=%s.xml".formatted(evidence.type())
                             )
                             .body(evidence.content());
    }
}
