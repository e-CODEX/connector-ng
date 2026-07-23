/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.domain.model.security;

import lombok.Builder;

/**
 * Represents the truststore configuration used for secure communication.
 *
 * <p>A truststore contains trusted certificates that are used to establish trust in
 * secure communication channels by validating the authenticity of certificates presented during
 * cryptographic exchanges.
 *
 * <p>Each instance of this class encapsulates the truststore content as a byte array,
 * its associated password for access, and the type of the keystore format, such as JKS or PKCS12.
 *
 * @param filename The filename of the truststore file.
 * @param content  The byte array representing the binary content of the truststore.
 * @param password The password required to access the truststore.
 * @param type     The type of the truststore (e.g., JKS or PKCS12).
 */
@Builder
public record ConnectorTruststore(
    String filename,
    byte[] content,
    String password,
    KeystoreType type
) {
}
