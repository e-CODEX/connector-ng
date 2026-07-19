/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.port.api.attachment;

import eu.ecodex.connector.application.service.attachement.FileUploadCommand;
import eu.ecodex.connector.domain.model.message.attachment.ConnectorMessageAttachment;
import jakarta.annotation.Nonnull;
import java.util.List;

/**
 * Use case interface responsible for handling the upload of connector message attachments.
 *
 * <p>Implementations are expected to process the provided files, store them in the configured
 * attachment storage, and return the resulting {@link ConnectorMessageAttachment}.
 */
public interface ConnectorUploadAttachments {
    /**
     * Executes the upload process for the given list of files.
     *
     * @param files a non-null list of {@link FileUploadCommand} objects representing files to be
     *              uploaded
     *
     * @return a list of {@link ConnectorMessageAttachment} representing the successfully uploaded
     *     attachments
     *
     * @throws NullPointerException if {@code files} is null
     */
    List<ConnectorMessageAttachment> execute(@Nonnull List<FileUploadCommand> files);
}
