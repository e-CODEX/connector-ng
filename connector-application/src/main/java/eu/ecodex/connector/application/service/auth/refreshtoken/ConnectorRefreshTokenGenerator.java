/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.service.auth.refreshtoken;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HexFormat;

/**
 * Utilities to encode and hash user's refresh tokens in the Connector System.
 */
public final class ConnectorRefreshTokenGenerator {
    public static final int TOKEN_BYTES = 128;

    /**
     * Length of the base64url-encoded token without padding.
     * base64 encodes 3 bytes as 4 characters, so n bytes need ceil(n * 4 / 3) characters.
     * Integer division rounds down, hence the "+ 2" (divisor - 1) to round up instead.
     */
    public static final int TOKEN_LENGTH = (TOKEN_BYTES * 4 + 2) / 3;

    private static final SecureRandom RANDOM = new SecureRandom();

    private ConnectorRefreshTokenGenerator() {
    }

    /**
     * Generate a new random refresh token.
     *
     * @return A 256-bit random value, 43 characters of hexadecimal characters.
     */
    public static String generate() {
        var bytes = new byte[TOKEN_BYTES];
        RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    /**
     * Hash any raw refresh token.
     *
     * @param rawToken refresh token to hash
     *
     * @return hashed refresh token
     */
    public static String hash(String rawToken) {
        try {
            var digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(
                digest.digest(rawToken.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }
}
