/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.presentation.rest.controller.admin;

import eu.ecodex.connector.application.presentation.rest.api.admin.ConnectorProcessingModeAdminApi;
import eu.ecodex.connector.application.presentation.rest.dto.ConnectorProcessingModeDto;
import eu.ecodex.connector.application.presentation.rest.exception.ConnectorBadRequestException;
import eu.ecodex.connector.application.presentation.rest.request.pmode.ConnectorProcessingModeCreationRequest;
import eu.ecodex.connector.domain.api.service.ConnectorProcessingModeService;
import eu.ecodex.connector.domain.model.businessdomain.ConnectorBusinessDomainIdentifier;
import eu.ecodex.connector.domain.model.keystore.ConnectorKeystore;
import eu.ecodex.connector.domain.model.pmode.ConnectorProcessingMode;
import jakarta.validation.Valid;
import java.io.IOException;
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
    private final ConnectorProcessingModeService processingModeService;

    public ConnectorProcessingModeAdminController(
            ConnectorProcessingModeService processingModeService) {
        this.processingModeService = processingModeService;
    }

    @Override
    public ConnectorProcessingModeDto create(
            @RequestParam("processingModeXmlFile") MultipartFile processingModeXmlFile,
            @RequestParam("truststoreFile") MultipartFile truststoreFile,
            @Valid @RequestPart("metadata") ConnectorProcessingModeCreationRequest metadata)
            throws IOException {
        var businessDomainIdentifier = ConnectorBusinessDomainIdentifier
                .builder()
                .messageLaneIdentifier(metadata.businessDomainIdentifier())
                .build();

        var processingMode = processCreationRequest(
                metadata, processingModeXmlFile, truststoreFile
        );

        var created = this.processingModeService.register(
                businessDomainIdentifier, processingMode
        );

        return toDto(created);
    }

    private ConnectorProcessingMode processCreationRequest(
            ConnectorProcessingModeCreationRequest metadata,
            MultipartFile processingModeXmlFile,
            MultipartFile truststoreFile) throws IOException {
        var xmlFileContentType = processingModeXmlFile.getContentType();

        if (!Objects.equals(xmlFileContentType, MediaType.APPLICATION_XML_VALUE)
            && !Objects.equals(xmlFileContentType, MediaType.TEXT_XML_VALUE)) {
            throw new ConnectorBadRequestException("pmode file must be XML");
        }

        var truststore = ConnectorKeystore
                .builder()
                .description(metadata.truststore().description())
                .content(truststoreFile.getBytes())
                .password(metadata.truststore().password())
                .type(metadata.truststore().type())
                .filename(StringUtils.cleanPath(
                        Objects.requireNonNull(truststoreFile.getOriginalFilename()))
                )
                .build();

        return ConnectorProcessingMode
                .builder()
                .description(metadata.description())
                .content(new String(processingModeXmlFile.getBytes()))
                .filename(StringUtils.cleanPath(
                        Objects.requireNonNull(processingModeXmlFile.getOriginalFilename()))
                )
                .truststore(truststore)
                .build();
    }

    private ConnectorProcessingModeDto toDto(ConnectorProcessingMode processingMode) {
        return ConnectorProcessingModeDto
                .builder()
                .uuid(processingMode.uuid())
                .description(processingMode.description())
                .content(processingMode.content())
                .filename(processingMode.filename())
                .businessDomainIdentifier(
                        Objects.requireNonNull(processingMode.businessDomain())
                               .identifier().messageLaneIdentifier()
                )
                .createdAt(processingMode.createdAt())
                .updatedAt(processingMode.updatedAt())
                .build();
    }
}
