/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.evidence;

import static eu.ecodex.connector.infrastructure.config.EvidenceConfig.REM_EVIDENCE_SIGNING_TOKEN_BEAN;

import eu.ecodex.connector.infrastructure.dss.ConnectorDssDocumentSigner;
import eu.ecodex.connector.infrastructure.dss.ConnectorDssSigningTokenProvider;
import eu.europa.esig.dss.enumerations.DigestAlgorithm;
import eu.europa.esig.dss.enumerations.EncryptionAlgorithm;
import eu.europa.esig.dss.model.InMemoryDocument;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * Applies an enveloped XAdES-B signature to marshalled REM evidence XML bytes.
 *
 * <p>Uses the dedicated REM keystore bean
 * ({@link eu.ecodex.connector.infrastructure.config.EvidenceConfig}) and SHA-1 as
 * the reference digest algorithm to stay compatible with legacy evidence signing.
 *
 * @see eu.ecodex.connector.infrastructure.config.EvidenceConfig#REM_EVIDENCE_SIGNING_TOKEN_BEAN
 */
@Slf4j
@Component
public class DssConnectorRemEvidenceXmlSigner {

    /**
     * Digest for the XML signature (legacy {@code EvidenceUtilsXades} used SHA-1).
     */
    public static final DigestAlgorithm REM_EVIDENCE_XML_SIGNATURE_DIGEST = DigestAlgorithm.SHA1;

    private final ConnectorDssDocumentSigner documentSigner;
    private final ConnectorDssSigningTokenProvider signingTokenProvider;

    /**
     * Creates the signer with shared DSS and REM keystore dependencies.
     *
     * @param documentSigner       shared DSS document signer for XAdES
     * @param signingTokenProvider keystore-backed token for REM evidence (qualified bean name)
     */
    public DssConnectorRemEvidenceXmlSigner(
            ConnectorDssDocumentSigner documentSigner,
            @Qualifier(REM_EVIDENCE_SIGNING_TOKEN_BEAN)
            ConnectorDssSigningTokenProvider signingTokenProvider) {
        this.documentSigner = documentSigner;
        this.signingTokenProvider = signingTokenProvider;
    }

    /**
     * Signs marshalled REM evidence XML and returns the full document bytes including
     * {@code ds:Signature}.
     *
     * @param unsignedXml marshalled REM evidence without signature
     * @return enveloped signed XML bytes
     * @throws IllegalStateException if reading signed bytes after DSS processing fails
     */
    public byte[] signUnsignedRemEvidenceXml(byte[] unsignedXml) {
        var doc = new InMemoryDocument(unsignedXml);
        try {
            var signedDocument = documentSigner.signWithXAdES(
                    doc,
                    EncryptionAlgorithm.RSA,
                    REM_EVIDENCE_XML_SIGNATURE_DIGEST,
                    signingTokenProvider
            );
            try (var stream = signedDocument.openStream()) {
                return stream.readAllBytes();
            }
        } catch (IOException e) {
            log.error("failed to read signed REM evidence document bytes", e);
            throw new IllegalStateException("signing REM evidence failed", e);
        }
    }
}
