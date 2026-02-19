/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.service.usecase.attachment;

import eu.ecodex.connector.application.service.impl.attachement.FileUploadCommand;
import jakarta.annotation.Nonnull;
import java.util.List;

/**
 * Service interface for storing one or more files.
 *
 * <p>This interface represents an application-facing file storage abstraction.
 */
public interface ConnectorFileStorage {
    /**
     * Stores a collection of files.
     *
     * <p>Each {@link FileUploadCommand} contains the metadata and content required
     * to persist a file. Implementations must process all provided files and return a corresponding
     * identifier for each successfully stored file.
     *
     * <p>The order of returned identifiers must match the order of the input list.
     *
     * @param files a non-null list of file upload commands to be stored; must not contain
     *              {@code null} elements
     *
     * @return a list of storage identifiers (e.g. keys, URLs, or IDs) corresponding to the stored
     *         files; never {@code null}
     * @throws IllegalArgumentException if the input list is empty or contains invalid elements
     * @throws RuntimeException         if one or more files cannot be stored
     */
    List<String> store(@Nonnull List<FileUploadCommand> files);
}
