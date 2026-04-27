/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.property.common;

/**
 * Represents the supported types of keystores.
 *
 * <p>This enum defines the two most commonly used keystore formats: JKS and PKCS12.
 * These formats are used for secure storage of cryptographic keys and certificates.
 *
 * <ul>
 *   <li>JKS: The Java KeyStore format, primarily used in Java applications.
 *   <li>PKCS12: A standardized format for storing cryptographic objects, supported by various
 *   platforms.
 * </ul>
 */
public enum KeystoreType {
    JKS, PKCS12
}
