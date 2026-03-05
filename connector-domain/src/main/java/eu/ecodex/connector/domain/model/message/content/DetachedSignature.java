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
import java.io.Serializable;
import lombok.Builder;

/**
 * The DetachedSignature class represents a detached signature for a businessDocument. It contains
 * the signature data, signature name, and MIME type of the signature.
 *
 * @param uuid      The unique uuid of the detached signature.
 * @param signature The signature data.
 * @param name      The signature name.
 * @param mimeType  The MIME type of the signature.
 */
@Builder
public record DetachedSignature(
        String uuid,
        byte[] signature,
        String name,
        DetachedSignatureMimeType mimeType
) implements Serializable {
    @Override
    @Nonnull
    public String toString() {
        return String.format("{name=%s, contentType=%s}", name, mimeType);
    }
}
