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

import static eu.ecodex.connector.domain.model.security.KeystoreType.fromFileName;

import eu.ecodex.connector.application.port.api.pmode.ConnectorListProcessingMode;
import eu.ecodex.connector.application.port.api.pmode.ConnectorRegisterProcessingMode;
import eu.ecodex.connector.application.port.api.pmode.ConnectorRetrieveProcessingMode;
import eu.ecodex.connector.application.port.api.pmode.ConnectorUpdateProcessingModeTruststore;
import eu.ecodex.connector.domain.model.businessdomain.ConnectorBusinessDomainIdentifier;
import eu.ecodex.connector.domain.model.pmode.ConnectorProcessingMode;
import eu.ecodex.connector.domain.model.security.ConnectorTruststore;
import eu.ecodex.connector.infrastructure.inbound.web.rest.dto.pmode.ConnectorProcessingModeDetailDto;
import eu.ecodex.connector.infrastructure.inbound.web.rest.dto.pmode.ConnectorProcessingModeDto;
import eu.ecodex.connector.infrastructure.inbound.web.rest.dto.pmode.ConnectorProcessingModeTruststoreDto;
import eu.ecodex.connector.infrastructure.inbound.web.rest.exception.ConnectorBadRequestException;
import eu.ecodex.connector.infrastructure.inbound.web.rest.request.pmode.ConnectorProcessingModeCreationRequest;
import eu.ecodex.connector.infrastructure.inbound.web.rest.request.pmode.ConnectorProcessingModeTruststoreRequest;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
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
    private final ConnectorUpdateProcessingModeTruststore updateProcessingModeTruststoreService;

    /**
     * Constructs an instance of {@code ConnectorProcessingModeAdminController}.
     *
     * @param registerProcessingModeService         the service responsible for registering
     *                                              processing modes
     * @param listProcessingModeService             the service used to list processing modes
     * @param retrieveProcessingModeService         the service for retrieving specific processing
     *                                              modes
     * @param updateProcessingModeTruststoreService the service for updating the truststore of
     *                                              processing modes
     */
    public ConnectorProcessingModeAdminController(
        ConnectorRegisterProcessingMode registerProcessingModeService,
        ConnectorListProcessingMode listProcessingModeService,
        ConnectorRetrieveProcessingMode retrieveProcessingModeService,
        ConnectorUpdateProcessingModeTruststore updateProcessingModeTruststoreService) {
        this.registerProcessingModeService = registerProcessingModeService;
        this.listProcessingModeService = listProcessingModeService;
        this.retrieveProcessingModeService = retrieveProcessingModeService;
        this.updateProcessingModeTruststoreService = updateProcessingModeTruststoreService;
    }

    @Override
    public ConnectorProcessingModeDto registerPmode(ConnectorProcessingModeCreationRequest request)
        throws IOException {
        var businessDomainIdentifier = ConnectorBusinessDomainIdentifier
            .builder()
            .messageLaneIdentifier(request.businessDomainIdentifier())
            .build();

        var processingMode = processCreationRequest(request);

        var created = this.registerProcessingModeService.execute(
            businessDomainIdentifier,
            processingMode
        );

        return ConnectorProcessingModeDto.from(created);
    }

    @Override
    public List<ConnectorProcessingModeDto> listPmodes() {
        var processingModes = listProcessingModeService.execute();

        return processingModes.stream().map(ConnectorProcessingModeDto::from).toList();
    }

    @Override
    public ConnectorProcessingModeDetailDto retrievePmode(String uuid) {
        var processingMode = retrieveProcessingModeService.execute(uuid);
        return ConnectorProcessingModeDetailDto.from(processingMode);
    }

    @Override
    public ResponseEntity<byte[]> downloadPmode(String uuid) {
        var processingMode = retrieveProcessingModeService.execute(uuid);
        var content = processingMode.content().getBytes(StandardCharsets.UTF_8);
        var filename = Paths.get(processingMode.filename()).getFileName().toString();

        return ResponseEntity.ok()
                             .contentType(MediaType.APPLICATION_XML)
                             .contentLength(content.length)
                             .header(
                                 HttpHeaders.CONTENT_DISPOSITION,
                                 "attachment; filename=%s".formatted(filename)
                             )
                             .body(content);
    }

    @Override
    public ConnectorProcessingModeTruststoreDto updateTruststore(
        String uuid,
        ConnectorProcessingModeTruststoreRequest request) {
        var file = request.truststoreFile();
        var filename = StringUtils.getFilename(getFilename(file.getOriginalFilename()));
        var truststore = ConnectorTruststore.builder()
                                            .filename(filename)
                                            .password(request.password())
                                            .content(
                                                readTruststoreContent(request.truststoreFile())
                                            )
                                            .type(fromFileName(filename).orElse(null))
                                            .build();

        var updated = updateProcessingModeTruststoreService.execute(uuid, truststore);

        return ConnectorProcessingModeTruststoreDto.from(updated);
    }

    private ConnectorProcessingMode processCreationRequest(
        ConnectorProcessingModeCreationRequest request) throws IOException {
        var processingModeXmlFile = request.processingModeFile();

        var xmlFileContentType = processingModeXmlFile.getContentType();

        if (!Objects.equals(xmlFileContentType, MediaType.APPLICATION_XML_VALUE)
            && !Objects.equals(xmlFileContentType, MediaType.TEXT_XML_VALUE)) {
            throw new ConnectorBadRequestException("Pmode file must be an XML file");
        }

        var truststoreRequest = request.truststore();
        var truststoreFile = truststoreRequest.truststoreFile();
        var truststoreFilename = getFilename(truststoreFile.getOriginalFilename());

        var truststore = ConnectorTruststore.builder()
                                            .filename(truststoreFilename)
                                            .password(truststoreRequest.password())
                                            .content(readTruststoreContent(truststoreFile))
                                            .type(fromFileName(truststoreFilename).orElse(null))
                                            .build();

        return ConnectorProcessingMode.builder()
                                      .description(request.description())
                                      .content(new String(processingModeXmlFile.getBytes()))
                                      .filename(getFilename(
                                          processingModeXmlFile.getOriginalFilename()
                                      ))
                                      .truststore(truststore)
                                      .build();
    }

    private byte[] readTruststoreContent(MultipartFile file) {
        try {
            return file.getBytes();
        } catch (IOException e) {
            throw new UncheckedIOException("Could not read the uploaded truststore", e);
        }
    }

    private String getFilename(String filename) {
        return StringUtils.getFilename(StringUtils.cleanPath(Objects.requireNonNull(filename, "")));
    }
}
