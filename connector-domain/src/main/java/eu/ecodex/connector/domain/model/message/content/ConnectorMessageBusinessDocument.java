/*
 * Copyright 2025 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.domain.model.message.content;

import eu.ecodex.connector.domain.model.message.attachment.ConnectorMessageAttachment;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import java.io.Serializable;
import lombok.Builder;

/**
 * Holds the printable businessDocument to a message. The businessDocument itself is a byte[]. A
 * name that the businessDocument is identified with and optionally a {@link DetachedSignature} that
 * the businessDocument is signed with are also businessContent of this object.
 *
 * @param uuid              The unique uuid of the businessDocument.
 * @param attachment        the {@link ConnectorMessageAttachment} containing the actual
 *                          businessDocument payload
 * @param detachedSignature The detached signature of the businessDocument.
 * @param aesType           the optional AES encryption type applied to the businessDocument; may be
 *                          {@code null} if the businessDocument is unsigned may be {@code null} if
 *                          the businessDocument is not encrypted
 */
@Builder(toBuilder = true)
public record ConnectorMessageBusinessDocument(
        @Nullable String uuid,
        @Nonnull ConnectorMessageAttachment attachment,
        @Nullable DetachedSignature detachedSignature,
        @Nullable ConnectorBusinessDocumentAESType aesType
) implements Serializable {
    @Override
    @Nonnull
    public String toString() {
        return String.format(
                "{attachment=%s, detachedSignature=%s, aesType=%s}",
                attachment, detachedSignature, aesType
        );
    }
}
