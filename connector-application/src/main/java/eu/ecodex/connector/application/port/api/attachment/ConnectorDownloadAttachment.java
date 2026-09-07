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

import jakarta.annotation.Nonnull;

/**
 * Defines the contract for downloading message attachments from a connector.
 *
 * <p>The primary use case is to manage attachments that are sent alongside messages, typically in
 * the form of documents like PDFs.
 */
public interface ConnectorDownloadAttachment {
    /**
     * Downloads the binary content of a message attachment identified by the provided identifier.
     * This method is typically used to retrieve documents or files associated with a specific
     * message.
     *
     * @param identifier a non-null string that uniquely identifies the attachment to be downloaded
     *
     * @return a byte array containing the binary content of the specified attachment
     *
     * @throws NullPointerException if {@code identifier} is null
     */
    byte[] execute(@Nonnull String identifier);
}
