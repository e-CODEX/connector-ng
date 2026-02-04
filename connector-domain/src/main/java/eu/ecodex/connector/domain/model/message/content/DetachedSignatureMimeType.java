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

import java.io.Serializable;
import lombok.Getter;

/**
 * Represents the MIME type of detached signature. This enum provides predefined MIME types that can
 * be used to indicate the format of a detached signature.
 *
 * <p>The MIME types indicate the type of data handled, such as binary, XML, or PKCS7 signatures.
 * Enum constants:
 * <ul>
 *     <li>BINARY: Represents the MIME type for binary data ("application/octet-stream").</li>
 *     <li>XML: Represents the MIME type for XML files ("text/xml").</li>
 *     <li>
 *         PKCS7: Represents the MIME type for PKCS7 signatures ("application/pkcs7-signature").
 *     </li>
 * </ul>
 *
 * <p>Each constant is associated with a string value that specifies the MIME type.
 */
@Getter
public enum DetachedSignatureMimeType implements Serializable {
    /**
     * application/octet-stream.
     */
    BINARY("application/octet-stream"),
    /**
     * text/xml.
     */
    XML("text/xml"),
    /**
     * application/pkcs7-signature.
     */
    PKCS7("application/pkcs7-signature");

    private final String mimeType;

    DetachedSignatureMimeType(String mimeType) {
        this.mimeType = mimeType;
    }
}
