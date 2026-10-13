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

import eu.ecodex.connector.domain.model.message.attachment.ConnectorMessageAttachment;
import jakarta.annotation.Nonnull;
import java.util.List;

/**
 * Use case for uploading one or more attachments.
 */
public interface ConnectorUploadAttachments {
    /**
     * Uploads the given files and registers them as {@link ConnectorMessageAttachment} objects.
     *
     * @param files the upload commands describing the files to upload (must not be null)
     *
     * @return the list of {@link ConnectorMessageAttachment} objects created from the uploaded
     *     files, in the same order as the given commands
     */
    List<ConnectorMessageAttachment> execute(@Nonnull List<FileUploadCommand> files);
}
