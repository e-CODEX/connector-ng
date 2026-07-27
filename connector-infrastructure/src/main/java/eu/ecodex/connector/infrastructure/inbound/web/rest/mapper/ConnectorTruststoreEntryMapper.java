/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.inbound.web.rest.mapper;

import eu.ecodex.connector.domain.model.security.ConnectorTruststore;
import eu.ecodex.connector.infrastructure.inbound.web.rest.dto.pmode.ConnectorCertificateInfoDto;
import eu.ecodex.connector.infrastructure.inbound.web.rest.exception.ConnectorTruststoreDecodingException;
import java.io.ByteArrayInputStream;
import java.security.KeyStore;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

/**
 * Maps content from a {@link ConnectorTruststore} to a list of {@link ConnectorCertificateInfoDto}
 * representations containing detailed certificate information.
 *
 * <p>This class provides utility methods to process truststore data, extract
 * certificates, and convert them into a more consumable format for application use.
 *
 * <p>Primary functionality includes loading a truststore, iterating over
 * its entries, and mapping each certificate into a relevant DTO containing metadata such as alias,
 * entry type, subject, issuer, validity, and other certificate details.
 */
@Slf4j
public class ConnectorTruststoreEntryMapper {
    /**
     * Converts the entries of a given truststore into a list of DTOs representing certificates.
     *
     * @param truststore the truststore containing certificate data to process; may be null, in
     *                   which case an empty list is returned
     *
     * @return a list of {@code ConnectorCertificateInfoDto} objects representing the entries in the
     *     truststore
     *
     * @throws ConnectorTruststoreDecodingException if an error occurs while processing the
     *                                              truststore
     */
    public static List<ConnectorCertificateInfoDto> toEntries(ConnectorTruststore truststore) {
        if (truststore == null || truststore.content() == null) {
            return List.of();
        }
        try {
            KeyStore ks = load(truststore);
            List<ConnectorCertificateInfoDto> entries = new ArrayList<>();
            for (String alias : Collections.list(ks.aliases())) {
                var cert = ks.getCertificate(alias);
                if (cert instanceof X509Certificate x509) {
                    entries.add(toDto(ks, alias, x509));
                }
            }
            return entries;
        } catch (Exception e) {
            log.error("Failed to enumerate truststore entries", e);
            return List.of();
        }
    }

    private static KeyStore load(ConnectorTruststore ts) {
        char[] pwd = ts.password() == null ? null : ts.password().toCharArray();
        try (var in = new ByteArrayInputStream(ts.content())) {
            KeyStore ks = (ts.type() == null)
                ? KeyStore.getInstance(KeyStore.getDefaultType())
                : KeyStore.getInstance(ts.type().name());
            ks.load(in, pwd);
            return ks;
        } catch (Exception e) {
            throw new ConnectorTruststoreDecodingException("Failed to load truststore binary", e);
        }
    }

    private static ConnectorCertificateInfoDto toDto(KeyStore ks, String alias, X509Certificate c)
        throws Exception {
        return new ConnectorCertificateInfoDto(
            alias,
            resolveEntryType(ks, alias),
            c.getSubjectX500Principal().getName(),
            c.getIssuerX500Principal().getName(),
            c.getSerialNumber().toString(16),
            c.getNotBefore().toInstant(),
            c.getNotAfter().toInstant(),
            c.getSigAlgName(),
            c.getPublicKey().getAlgorithm(),
            resolveValidity(c)
        );
    }

    private static String resolveEntryType(KeyStore ks, String alias) throws Exception {
        if (ks.isCertificateEntry(alias)) {
            return "TRUSTED_CERTIFICATE";
        }
        if (ks.isKeyEntry(alias)) {
            return "KEY";
        }
        return "OTHER";
    }

    private static String resolveValidity(X509Certificate c) {
        try {
            c.checkValidity();
            return "VALID";
        } catch (CertificateExpiredException e) {
            return "EXPIRED";
        } catch (CertificateNotYetValidException e) {
            return "NOT_YET_VALID";
        }
    }
}
