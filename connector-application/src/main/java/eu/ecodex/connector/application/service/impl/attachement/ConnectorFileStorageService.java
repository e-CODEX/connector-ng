/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.service.impl.attachement;

import eu.ecodex.connector.application.service.usecase.attachment.ConnectorFileStorage;
import eu.ecodex.connector.domain.exception.ConnectorStorageException;
import eu.ecodex.connector.domain.spi.ConnectorFileStorageProvider;
import java.util.List;
import java.util.Objects;
import lombok.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * Implementation of the {@link ConnectorFileStorage} service.
 */
@Service
public class ConnectorFileStorageService implements ConnectorFileStorage {
    private final ConnectorFileStorageProvider fileStorageProvider;

    public ConnectorFileStorageService(ConnectorFileStorageProvider fileStorageProvider) {
        this.fileStorageProvider = fileStorageProvider;
    }

    @Override
    public List<String> store(@NonNull List<FileUploadCommand> files) {
        try {
            return files
                    .stream()
                    .map(file -> fileStorageProvider.save(
                            StringUtils.cleanPath(Objects.requireNonNull(file.filename())),
                            file.size(),
                            file.contentType(),
                            file.inputStream()
                    ))
                    .toList();
        } catch (Exception e) {
            throw new ConnectorStorageException(e.getMessage());
        }
    }
}
