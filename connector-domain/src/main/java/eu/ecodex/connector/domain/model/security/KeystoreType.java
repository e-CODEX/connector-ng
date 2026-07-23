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

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
    JKS("jks", "keystore"),
    PKCS12("p12", "pfx");

    private static final Map<String, KeystoreType> BY_EXTENSION =
        Arrays.stream(values())
              .flatMap(type -> type.extensions.stream().map(ext -> Map.entry(ext, type)))
              .collect(Collectors.toUnmodifiableMap(
                  Map.Entry::getKey,
                  Map.Entry::getValue
              ));

    private final List<String> extensions;

    KeystoreType(String... extensions) {
        this.extensions = List.of(extensions);
    }

    /**
     * Attempts to determine a {@code KeystoreType} based on the provided file extension.
     *
     * @param extension The file extension, which may include or omit a leading dot. It may also be
     *                  {@code null}, in which case an empty {@code Optional} is returned.
     *
     * @return An {@code Optional} containing the corresponding {@code KeystoreType} if the
     *     extension is known; otherwise, an empty {@code Optional}.
     */
    public static Optional<KeystoreType> fromExtension(String extension) {
        if (extension == null) {
            return Optional.empty();
        }
        String normalized = extension.trim().toLowerCase(Locale.ROOT);
        int lastDot = normalized.lastIndexOf('.');
        if (lastDot >= 0) {
            normalized = normalized.substring(lastDot + 1);
        }
        return Optional.ofNullable(BY_EXTENSION.get(normalized));
    }

    public static Optional<KeystoreType> fromFileName(String fileName) {
        return fromExtension(fileName);
    }
}
