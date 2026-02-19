/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.inbound.web.rest.controller.attachement;

import eu.ecodex.connector.application.service.impl.attachement.FileUploadCommand;
import eu.ecodex.connector.application.service.usecase.attachment.ConnectorFileStorage;
import java.io.IOException;
import java.util.List;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * Defines the REST controller for managing large files within the connector system.
 */
@RestController
public class ConnectorAttachmentController implements ConnectorAttachmentControllerApi {
    private final ConnectorFileStorage fileService;

    public ConnectorAttachmentController(ConnectorFileStorage fileService) {
        this.fileService = fileService;
    }

    @Override
    public List<String> upload(List<MultipartFile> attachments) {
        var fileCommand = attachments.stream()
                                     .map(attachment -> {
                                              try {
                                                  return new FileUploadCommand(
                                                          attachment.getOriginalFilename(),
                                                          attachment.getSize(),
                                                          attachment.getContentType(),
                                                          attachment.getInputStream()
                                                  );
                                              } catch (IOException e) {
                                                  throw new RuntimeException(e);
                                              }
                                          }
                                     ).toList();
        return fileService.store(fileCommand);
    }
}
