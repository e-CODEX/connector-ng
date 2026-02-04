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

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;
import lombok.Builder;

/**
 * Holds the printable document to a message. The document itself is a byte[]. A name that the
 * document is identified with and optionally a {@link DetachedSignature} that the document is
 * signed with are also content of this object.
 *
 * @param uuid              The unique uuid of the document.
 * @param name              The name of the document.
 * @param detachedSignature The detached signature of the document.
 * @param hashValue         The hash value of the document.
 */
@Builder(toBuilder = true)
public record ConnectorMessageDocument(
        String uuid,
        // TODO add large file which is the content of the document
        @NotBlank String name,
        @Nullable DetachedSignature detachedSignature,
        String hashValue
) implements Serializable {
    @Override
    @Nonnull
    public String toString() {
        return String.format("{name=%s, detachedSignature=%s}", name, detachedSignature);
    }
}
