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

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import eu.ecodex.connector.FileTestFixtures;
import eu.ecodex.connector.domain.model.security.KeystoreType;
import eu.ecodex.connector.infrastructure.property.common.KeystoreProperties;
import eu.ecodex.connector.infrastructure.property.common.PrivateKeyProperties;
import eu.europa.esig.dss.enumerations.DigestAlgorithm;
import eu.europa.esig.dss.enumerations.EncryptionAlgorithm;
import eu.europa.esig.dss.enumerations.MimeTypeEnum;
import eu.europa.esig.dss.model.InMemoryDocument;
import eu.europa.esig.dss.spi.DSSUtils;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class ConnectorDssDocumentSignerTest extends BaseDssTest {
    private final ConnectorDssSigningTokenProvider signingTokenProvider;
    @Autowired
    private ConnectorDssDocumentSigner documentSigner;

    {
        var keystoreProperties = new KeystoreProperties();
        keystoreProperties.setPath("classpath:keystores/connector-keystore.jks");
        keystoreProperties.setPassword("12345");
        keystoreProperties.setType(KeystoreType.JKS);

        var privateKeyProperties = new PrivateKeyProperties();
        privateKeyProperties.setAlias("connector_blue");
        privateKeyProperties.setPassword("12345");

        signingTokenProvider = new ConnectorDssSigningTokenProvider(
            keystoreProperties, privateKeyProperties
        );
    }

    @Test
    void should_sign_with_pades_successfully() {
        var pdf = FileTestFixtures.readAsBytes("raw/test-pdf.pdf");
        var document = new InMemoryDocument(pdf);
        var signedDocument = documentSigner.signWithPadES(
            document,
            EncryptionAlgorithm.RSA,
            DigestAlgorithm.SHA256,
            signingTokenProvider
        );

        assertThat(signedDocument).isNotNull();
        assertThat(signedDocument.getMimeType()).isEqualTo(MimeTypeEnum.PDF);
    }

    @Test
    void should_sign_with_xades_successfully() {
        var xml = FileTestFixtures.readAsBytes("raw/test-xml.xml");
        var document = new InMemoryDocument(xml);
        var signedDocument = documentSigner.signWithXAdES(
            document,
            EncryptionAlgorithm.RSA,
            DigestAlgorithm.SHA256,
            signingTokenProvider
        );

        assertThat(signedDocument).isNotNull();
        assertThat(signedDocument.getMimeType()).isEqualTo(MimeTypeEnum.XML);
    }

    @Test
    void should_sign_with_xades_asic_successfully() throws IOException {
        var signedContentBytes = new ByteArrayOutputStream();
        var signedContentZip = new ZipOutputStream(signedContentBytes);
        signedContentZip.setLevel(ZipEntry.DEFLATED);

        var xml = FileTestFixtures.readAsBytes("raw/test-xml.xml");
        var xmlDocument = new InMemoryDocument(xml);
        signedContentZip.putNextEntry(new ZipEntry("test-xml.xml"));
        signedContentZip.write(DSSUtils.toByteArray(xmlDocument.openStream()));

        var pdf = FileTestFixtures.readAsBytes("raw/test-pdf.pdf");
        var pdfDocument = new InMemoryDocument(pdf);

        signedContentZip.putNextEntry(new ZipEntry("test-pdf.pdf"));
        signedContentZip.write(DSSUtils.toByteArray(pdfDocument.openStream()));

        signedContentZip.close();

        final var document = new InMemoryDocument(
            signedContentBytes.toByteArray(),
            "SignedContent.zip",
            MimeTypeEnum.BINARY
        );

        var signedDocument = documentSigner.signWithASIC(
            document, signingTokenProvider
        );

        assertThat(signedDocument).isNotNull();
        assertThat(signedDocument.getMimeType()).isEqualTo(MimeTypeEnum.ASICS);
    }
}
