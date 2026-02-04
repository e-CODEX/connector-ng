/*
 * Copyright 2025 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.domain.model.keystore;

import java.io.Serializable;
import lombok.Getter;

/**
 * Represents the supported types of keystores with their associated file extensions. This enum
 * provides a way to differentiate keystore formats used in secure communications and cryptographic
 * operations.
 *
 * <p>The available keystore types are:
 * <ul>
 *     <li>JKS: Java KeyStore format with the file extension ".jks".</li>
 *     <li>
 *         JCEKS: Java Cryptography Extension KeyStore format with the file extension ".jceks".
 *     </li>
 *     <li>
 *         PKCS12: Public Key Cryptography Standard #12 format with the file extension ".pkcs12".
 *     </li>
 *     <li>
 *         PKCS12S2: A variant or extension of the PKCS12 format with the file extension
 *         ".pkcs12s2".
 *     </li>
 * </ul>
 */
@Getter
public enum ConnectorKeystoreType implements Serializable {
    JKS(".jks"),
    JCEKS(".jceks"),
    PKCS12(".pkcs12"),
    PKCS12S2(".pkcs12s2");

    private final String extension;

    ConnectorKeystoreType(String extension) {
        this.extension = extension;
    }
}
