/*
 * Copyright 2025 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.inbound.web.rest.request.message;

import eu.ecodex.connector.domain.model.message.content.DetachedSignatureMimeType;
import java.io.Serializable;
import lombok.Builder;
import org.springframework.web.multipart.MultipartFile;


/**
 * Represents a detached signature associated with an outbound message business document.
 *
 * <p>A detached signature contains the raw signature bytes and metadata describing the signature
 * artifact. The signature is not embedded within the business document itself but is transmitted or
 * stored separately.
 *
 * @param signature the raw signature bytes; may be {@code null} depending on validation rules
 * @param mimeType  the MIME type describing the signature format; may be {@code null}
 */
@Builder
public record ConnectorOutboundMessageDetachedSignature(
        MultipartFile signature,
        DetachedSignatureMimeType mimeType
) implements Serializable {
}
