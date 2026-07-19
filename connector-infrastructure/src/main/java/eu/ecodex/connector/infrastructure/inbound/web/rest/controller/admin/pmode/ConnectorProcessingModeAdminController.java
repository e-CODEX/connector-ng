/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.inbound.web.rest.controller.admin.pmode;

import eu.ecodex.connector.application.port.api.pmode.ConnectorListProcessingMode;
import eu.ecodex.connector.application.port.api.pmode.ConnectorRegisterProcessingMode;
import eu.ecodex.connector.application.port.api.pmode.ConnectorRetrieveProcessingMode;
import eu.ecodex.connector.domain.model.businessdomain.ConnectorBusinessDomainIdentifier;
import eu.ecodex.connector.domain.model.pmode.ConnectorProcessingMode;
import eu.ecodex.connector.infrastructure.inbound.web.rest.dto.pmode.ConnectorProcessingModeDetailDto;
import eu.ecodex.connector.infrastructure.inbound.web.rest.dto.pmode.ConnectorProcessingModeDto;
import eu.ecodex.connector.infrastructure.inbound.web.rest.exception.ConnectorBadRequestException;
import eu.ecodex.connector.infrastructure.inbound.web.rest.request.pmode.ConnectorProcessingModeCreationRequest;
import jakarta.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * Defines the REST controller for managing processing modes within the connector system.
 */
@RestController
public class ConnectorProcessingModeAdminController implements ConnectorProcessingModeAdminApi {
    private final ConnectorRegisterProcessingMode registerProcessingModeService;
    private final ConnectorListProcessingMode listProcessingModeService;
    private final ConnectorRetrieveProcessingMode retrieveProcessingModeService;

    /**
     * Constructs an instance of {@code ConnectorProcessingModeAdminController}.
     *
     * @param registerProcessingModeService the service responsible for registering processing
     *                                      modes
     * @param listProcessingModeService     the service used to list processing modes
     * @param retrieveProcessingModeService the service for retrieving specific processing modes
     */
    public ConnectorProcessingModeAdminController(
        ConnectorRegisterProcessingMode registerProcessingModeService,
        ConnectorListProcessingMode listProcessingModeService,
        ConnectorRetrieveProcessingMode retrieveProcessingModeService) {
        this.registerProcessingModeService = registerProcessingModeService;
        this.listProcessingModeService = listProcessingModeService;
        this.retrieveProcessingModeService = retrieveProcessingModeService;
    }

    @Override
    public ConnectorProcessingModeDto create(
        @RequestParam("processingModeXmlFile") MultipartFile processingModeXmlFile,
        @Valid @RequestPart("metadata") ConnectorProcessingModeCreationRequest metadata)
        throws IOException {
        var businessDomainIdentifier = ConnectorBusinessDomainIdentifier
            .builder()
            .messageLaneIdentifier(metadata.businessDomainIdentifier())
            .build();

        var processingMode = processCreationRequest(
            metadata, processingModeXmlFile
        );

        var created = this.registerProcessingModeService.execute(
            businessDomainIdentifier, processingMode
        );

        return ConnectorProcessingModeDto.from(created);
    }

    @Override
    public List<ConnectorProcessingModeDto> listPmodes() {
        var processingModes = listProcessingModeService.execute();

        return processingModes.stream().map(ConnectorProcessingModeDto::from).toList();
    }

    @Override
    public ConnectorProcessingModeDetailDto getPmode(String uuid) {
        var processingMode = retrieveProcessingModeService.execute(uuid);
        return ConnectorProcessingModeDetailDto.from(processingMode);
    }

    private ConnectorProcessingMode processCreationRequest(
        ConnectorProcessingModeCreationRequest metadata,
        MultipartFile processingModeXmlFile) throws IOException {
        var xmlFileContentType = processingModeXmlFile.getContentType();

        if (!Objects.equals(xmlFileContentType, MediaType.APPLICATION_XML_VALUE)
            && !Objects.equals(xmlFileContentType, MediaType.TEXT_XML_VALUE)) {
            throw new ConnectorBadRequestException("pmode file must be XML");
        }

        return ConnectorProcessingMode
            .builder()
            .description(metadata.description())
            .content(new String(processingModeXmlFile.getBytes()))
            .filename(StringUtils.cleanPath(
                Objects.requireNonNull(processingModeXmlFile.getOriginalFilename()))
            )
            .build();
    }
}
