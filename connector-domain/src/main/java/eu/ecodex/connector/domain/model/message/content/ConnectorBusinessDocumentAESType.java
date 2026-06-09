/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.domain.model.message.content;

import java.util.Arrays;

/**
 * Defines the supported AES handling types for a business businessDocument within the connector
 * domain.
 *
 * <p>This enum describes how AES-related processing (e.g. key handling or
 * encryption context) is derived for a {@code ConnectorMessageBusinessDocument}.</p>
 *
 * <ul>
 *     <li>{@link #AUTHENTICATION_BASED} – AES handling is derived from the
 *     authentication context (e.g. based on the authenticated party or session).
 *     <li>{@link #SIGNATURE_BASED} – AES handling is derived from the
 *     businessDocument's digital signature context.
 * </ul>
 */
public enum ConnectorBusinessDocumentAESType {
    /**
     * AES handling is determined based on the authentication context of the message exchange.
     */
    AUTHENTICATION_BASED,
    /**
     * AES handling is determined based on the digital signature associated with the business
     * businessDocument.
     */
    SIGNATURE_BASED;

    /**
     * Converts a string representation of an AES type into its corresponding enum constant of
     * {@code ConnectorBusinessDocumentAESType}.
     *
     * @param type the string representation of an AES handling type. This value is
     *             case-insensitive; if it is {@code null} or blank, the method will return
     *             {@code null}.
     *
     * @return the {@code ConnectorBusinessDocumentAESType} enum constant that matches the provided
     *         string. If no match is found, an {@link IllegalArgumentException} is thrown.
     *
     * @throws IllegalArgumentException if the provided type does not correspond to any
     *                                  {@code ConnectorBusinessDocumentAESType}.
     */
    public static ConnectorBusinessDocumentAESType from(String type) {
        if (type == null || type.isBlank()) {
            return null;
        }

        return Arrays.stream(values())
                     .filter(value -> value.name().equalsIgnoreCase(type))
                     .findFirst()
                     .orElseThrow(() -> new IllegalArgumentException(
                             "Unsupported detached signature AES type: " + type
                     ));
    }
}
