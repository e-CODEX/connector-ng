/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.outbound.security.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import lombok.NonNull;
import org.bouncycastle.util.encoders.Base64;

/**
 * Utility class for computing message digests and encoding them.
 */
public class DigestUtil {
    /**
     * Computes the cryptographic hash (digest) of the given byte array using the specified
     * algorithm and encodes the result in Base64.
     *
     * <p>Supported algorithms depend on the underlying Java security provider,
     * but commonly include "MD5", "SHA-1", "SHA-256", etc.
     *
     * @param bytes     the input data to hash (must not be null)
     * @param algorithm the name of the digest algorithm (e.g. "SHA-256")
     *
     * @return the Base64-encoded digest of the input data
     *
     * @throws RuntimeException if the specified algorithm is not available
     */
    public static byte[] digest(final byte @NonNull [] bytes, @NonNull final String algorithm) {
        try {
            final var digest = MessageDigest.getInstance(algorithm);
            final byte[] digestValue = digest.digest(bytes);

            return Base64.encode(digestValue);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
