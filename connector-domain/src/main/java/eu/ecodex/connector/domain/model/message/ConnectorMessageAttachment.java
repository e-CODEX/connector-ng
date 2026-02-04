/*
 * Copyright 2025 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.domain.model.message;

import jakarta.annotation.Nonnull;
import java.io.Serializable;
import lombok.Builder;

/**
 * This object contains an attachment for a message. With every message there can be some documents
 * (mostly PDF's) sent along with. Therefore, those documents are attached to the message over this
 * type.
 *
 * @param uuid  The uuid of the attachment.
 * @param name        The name of the attachment.
 * @param mimeType    The MIME type of the attachment.
 * @param description A description of the attachment.
 */
@Builder(toBuilder = true)
public record ConnectorMessageAttachment(
        String uuid,
        // TODO add large file which is the content of the document
        String name,
        String mimeType,
        String description
) implements Serializable {
    @Override
    @Nonnull
    public String toString() {
        return String.format(
                "{uuid=%s, name=%s, mimeType=%s, description=%s}",
                uuid, name, mimeType, description
        );
    }
}
