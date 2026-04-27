/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.dss;

import eu.europa.esig.dss.asic.xades.ASiCWithXAdESSignatureParameters;
import eu.europa.esig.dss.enumerations.ASiCContainerType;
import eu.europa.esig.dss.enumerations.DigestAlgorithm;
import eu.europa.esig.dss.enumerations.EncryptionAlgorithm;
import eu.europa.esig.dss.enumerations.SignatureLevel;
import eu.europa.esig.dss.enumerations.SignaturePackaging;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.pades.PAdESSignatureParameters;
import eu.europa.esig.dss.signature.AbstractSignatureParameters;
import eu.europa.esig.dss.signature.AbstractSignatureService;
import eu.europa.esig.dss.xades.XAdESSignatureParameters;
import java.time.Instant;
import java.util.Date;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * High-level service responsible for signing documents using DSS in multiple formats.
 *
 * <p>This component orchestrates the full signing process:
 * <ol>
 *     <li>Build signature parameters</li>
 *     <li>Retrieve the signing key and certificate chain</li>
 *     <li>Compute the data to sign via a DSS service</li>
 *     <li>Delegate the cryptographic signing operation to the signing token</li>
 *     <li>Produce the final signed document</li>
 * </ol>
 *
 * <p>Supported signature formats:
 * <ul>
 *     <li>ASiC-S container with XAdES signature</li>
 *     <li>XAdES (XML Advanced Electronic Signatures)</li>
 *     <li>PAdES (PDF Advanced Electronic Signatures)</li>
 * </ul>
 *
 * <p><b>Note:</b> All signatures are currently created at BASELINE_B level.
 */
@Slf4j
@Component
public class ConnectorDssDocumentSigner {
    private final ConnectorDssServiceFactory dssServiceFactory;

    /**
     * Constructs a document signer with required DSS components.
     *
     * @param dssServiceFactory factory for DSS services
     */
    public ConnectorDssDocumentSigner(
            ConnectorDssServiceFactory dssServiceFactory) {
        this.dssServiceFactory = dssServiceFactory;
    }

    /**
     * Signs a document using an ASiC-S container with a detached XAdES signature.
     *
     * <p>Configuration:
     * <ul>
     *     <li>Signature level: {@link SignatureLevel#XAdES_BASELINE_B}</li>
     *     <li>Packaging: {@link SignaturePackaging#DETACHED}</li>
     *     <li>Container type: {@link ASiCContainerType#ASiC_S}</li>
     * </ul>
     *
     * @param documentToSign the document to sign
     *
     * @return the resulting ASiC container as a {@link DSSDocument}
     * @throws NullPointerException if {@code documentToSign} is null
     */
    public DSSDocument signWithASIC(
            @NonNull final DSSDocument documentToSign,
            ConnectorDssSigningTokenProvider signingTokenProvider) {
        var params = new ASiCWithXAdESSignatureParameters();
        params.setSignatureLevel(SignatureLevel.XAdES_BASELINE_B);
        params.setSignaturePackaging(SignaturePackaging.DETACHED);
        params.aSiC().setContainerType(ASiCContainerType.ASiC_S);

        return sign(
                documentToSign,
                params,
                dssServiceFactory.createAsicWithXAdESService(),
                signingTokenProvider
        );
    }

    /**
     * Signs a document using XAdES (XML Advanced Electronic Signatures).
     *
     * <p>Configuration:
     * <ul>
     *     <li>Signature level: {@link SignatureLevel#XAdES_BASELINE_B}</li>
     *     <li>Packaging: {@link SignaturePackaging#ENVELOPED}</li>
     * </ul>
     *
     * @param documentToSign      the XML document to sign
     * @param encryptionAlgorithm encryption algorithm (e.g. RSA, ECDSA)
     * @param digestAlgorithm     digest algorithm (e.g. SHA-256)
     *
     * @return the signed XML document
     * @throws NullPointerException if {@code documentToSign} is null
     */
    public DSSDocument signWithXAdES(
            @NonNull final DSSDocument documentToSign,
            EncryptionAlgorithm encryptionAlgorithm,
            DigestAlgorithm digestAlgorithm,
            ConnectorDssSigningTokenProvider signingTokenProvider) {
        var params = new XAdESSignatureParameters();
        params.setSignatureLevel(SignatureLevel.XAdES_BASELINE_B);
        params.setSignaturePackaging(SignaturePackaging.ENVELOPED);
        params.setEncryptionAlgorithm(encryptionAlgorithm);
        params.setDigestAlgorithm(digestAlgorithm);

        return sign(
                documentToSign,
                params,
                dssServiceFactory.createXadESSService(),
                signingTokenProvider
        );
    }

    /**
     * Signs a PDF document using PAdES (PDF Advanced Electronic Signatures).
     *
     * <p>Configuration:
     * <ul>
     *     <li>Signature level: {@link SignatureLevel#PAdES_BASELINE_B}</li>
     *     <li>Packaging: {@link SignaturePackaging#ENVELOPED}</li>
     * </ul>
     *
     * @param documentToSign      the PDF document to sign
     * @param encryptionAlgorithm encryption algorithm (e.g. RSA, ECDSA)
     * @param digestAlgorithm     digest algorithm (e.g. SHA-256)
     *
     * @return the signed PDF document
     * @throws NullPointerException if {@code documentToSign} is null
     */
    public DSSDocument signWithPadES(
            @NonNull final DSSDocument documentToSign,
            EncryptionAlgorithm encryptionAlgorithm,
            DigestAlgorithm digestAlgorithm,
            ConnectorDssSigningTokenProvider signingTokenProvider) {
        var params = new PAdESSignatureParameters();
        params.setSignaturePackaging(SignaturePackaging.ENVELOPED);
        params.setSignatureLevel(SignatureLevel.PAdES_BASELINE_B);
        params.setEncryptionAlgorithm(encryptionAlgorithm);
        params.setDigestAlgorithm(digestAlgorithm);

        return sign(
                documentToSign,
                params,
                dssServiceFactory.createPadESSService(),
                signingTokenProvider
        );
    }

    private <P extends AbstractSignatureParameters<?>> DSSDocument sign(
            DSSDocument documentToSign,
            P params,
            AbstractSignatureService<P, ?> service,
            ConnectorDssSigningTokenProvider signingTokenProvider) {
        var privateKey = signingTokenProvider.getSigningKey();
        params.setSigningCertificate(privateKey.getCertificate());
        params.setCertificateChain(privateKey.getCertificateChain());
        params.bLevel().setSigningDate(Date.from(Instant.now()));

        try {
            var dataToSign = service.getDataToSign(documentToSign, params);
            var signedData = signingTokenProvider.getSigningToken()
                                                 .sign(
                                                         dataToSign,
                                                         params.getDigestAlgorithm(),
                                                         privateKey
                                                 );
            return service.signDocument(documentToSign, params, signedData);
        } catch (Exception e) {
            log.error(
                    "failed to sign document [{}] with params [{}]",
                    documentToSign.getName(), params.getSignatureLevel(), e
            );
            throw new RuntimeException("document signing failed", e);
        }
    }
}
