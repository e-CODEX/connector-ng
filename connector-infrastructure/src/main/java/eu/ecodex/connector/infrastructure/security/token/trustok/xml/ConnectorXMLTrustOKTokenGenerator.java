/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.security.token.trustok.xml;

import eu.ecodex.connector.infrastructure.dss.ConnectorDssDocumentSigner;
import eu.ecodex.connector.infrastructure.dss.ConnectorDssSigningTokenProvider;
import eu.ecodex.connector.infrastructure.property.container.ConnectorContainerProperties;
import eu.ecodex.connector.infrastructure.security.container.ConnectorContainerFileDefinitions;
import eu.ecodex.connector.infrastructure.security.exception.ConnectorTrustOKTokenException;
import eu.ecodex.connector.infrastructure.security.model.token.ConnectorToken;
import eu.ecodex.connector.infrastructure.security.token.trustok.ConnectorTrustOKTokenGenerator;
import eu.ecodex.connector.infrastructure.security.util.XMLStreamUtil;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.model.InMemoryDocument;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Generates a signed XML "TrustOK" token from a {@link ConnectorToken}.
 *
 * <p>This implementation serializes the token into XML format and applies an XAdES digital
 * signature.
 *
 * <p>The generation process includes:
 * <ol>
 *     <li>Marshalling the token into XML
 *     <li>Injecting validation report data (if available)
 *     <li>Wrapping the result into a {@link DSSDocument}
 *     <li>Applying an XAdES signature
 * </ol>
 */
@Slf4j
@Component
public class ConnectorXMLTrustOKTokenGenerator implements ConnectorTrustOKTokenGenerator {
    private final ConnectorDssDocumentSigner connectorDssDocumentSigner;
    private final ConnectorContainerProperties containerProperties;
    private final ConnectorDssSigningTokenProvider signingTokenProvider;

    /**
     * Constructs a new XML TrustOK token generator.
     *
     * @param connectorDssDocumentSigner the signer used to apply XAdES signatures
     * @param containerProperties        configuration properties (including signing settings)
     */
    public ConnectorXMLTrustOKTokenGenerator(
            ConnectorDssDocumentSigner connectorDssDocumentSigner,
            ConnectorContainerProperties containerProperties) {
        this.connectorDssDocumentSigner = connectorDssDocumentSigner;
        this.containerProperties = containerProperties;

        var signature = containerProperties.getSignature();
        this.signingTokenProvider = new ConnectorDssSigningTokenProvider(
                signature.getKeystore(),
                signature.getPrivateKey()
        );
    }

    @Override
    public DSSDocument generate(@NonNull ConnectorToken token) {
        log.debug("Creating XML TrustOK token");
        try {
            var xmlBytes = encodeToXMLBytes(token);
            log.debug("XML token encoded: {} bytes", xmlBytes.length);

            var xmlToken = new InMemoryDocument(
                    xmlBytes,
                    ConnectorContainerFileDefinitions.TOKEN_XML_REF
            );

            return sign(xmlToken);
        } catch (ConnectorTrustOKTokenException e) {
            throw e;
        } catch (Exception e) {
            throw new ConnectorTrustOKTokenException("Failed to generate XML TrustOK token", e);
        }
    }

    /**
     * Encodes the token to XML bytes, optionally injecting the simple validation report in place of
     * the placeholder OriginalValidationReport element.
     */
    private byte[] encodeToXMLBytes(@NonNull final ConnectorToken token) throws Exception {
        ByteArrayOutputStream outputStream;

        try {
            outputStream = XMLStreamUtil.encodeXMLStream(token);
        } catch (Exception e) {
            log.error("Failed to marshal token to XML", e);
            throw e;
        }

        var xmlString = outputStream.toString(StandardCharsets.UTF_8);

        return xmlString.getBytes(StandardCharsets.UTF_8);
    }

    private DSSDocument sign(DSSDocument xmlToken) {
        var signature = containerProperties.getSignature();

        return this.connectorDssDocumentSigner.signWithXAdES(
                xmlToken,
                signature.getEncryptionAlgorithm(),
                signature.getDigestAlgorithm(),
                signingTokenProvider
        );
    }
}
