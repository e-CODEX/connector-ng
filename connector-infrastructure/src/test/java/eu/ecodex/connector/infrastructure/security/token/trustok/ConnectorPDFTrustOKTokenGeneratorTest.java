/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.security.token.trustok;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import eu.ecodex.connector.ConnectorMessageDocumentTestFixtures;
import eu.ecodex.connector.FileTestFixtures;
import eu.ecodex.connector.MessageContentTestFixtures;
import eu.ecodex.connector.MessageTestFixtures;
import eu.ecodex.connector.infrastructure.outbound.security.token.trustok.pdf.ConnectorPDFTrustOKTokenGenerator;
import eu.ecodex.connector.infrastructure.outbound.security.token.validation.ConnectorTokenValidationGenerator;
import eu.ecodex.connector.infrastructure.outbound.security.token.validation.technical.ConnectorTokenValidationFactory;
import eu.ecodex.connector.infrastructure.security.SecurityUtil;
import eu.ecodex.connector.infrastructure.security.token.BaseTokenTest;
import eu.europa.esig.dss.enumerations.MimeTypeEnum;
import eu.europa.esig.dss.model.InMemoryDocument;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class ConnectorPDFTrustOKTokenGeneratorTest extends BaseTokenTest {
    @Autowired
    private ConnectorPDFTrustOKTokenGenerator trustOKTokenGenerator;
    @Autowired
    private ConnectorTokenValidationGenerator validationGenerator;
    @Autowired
    private ConnectorTokenValidationFactory validationFactory;

    @Test
    void should_create_pdf_trust_ok_token_for_signature_based_document_and_sign_it_successfully()
        throws IOException {
        var message = MessageTestFixtures.createOutboundBusinessMessage();
        var document = FileTestFixtures.readAsBytes("raw/document/NonSigned.pdf");
        var businessDocument = new InMemoryDocument(document);
        var issuer = validationFactory.getTokenIssuer(message);

        var token = validationGenerator.createToken(
            message,
            businessDocument,
            null,
            issuer
        );

        var pdfToken = trustOKTokenGenerator.generate(token);

        assertThat(pdfToken).isNotNull();
        assertThat(pdfToken.getMimeType()).isEqualTo(MimeTypeEnum.PDF);
        assertThat(SecurityUtil.hasSignature(pdfToken)).isTrue();
        try (var stream = pdfToken.openStream()) {
            assertThat(SecurityUtil.getPageNumbers(stream.readAllBytes())).isGreaterThanOrEqualTo(2);
        }
    }

    @Test
    void should_create_pdf_trust_ok_token_with_appendix_for_signature_based_document_and_sign_it_successfully()
        throws IOException {
        var message = MessageTestFixtures.createOutboundBusinessMessage();
        var document = FileTestFixtures.readAsBytes("raw/document/Signed_Visible.pdf");
        var businessDocument = new InMemoryDocument(document);
        var issuer = validationFactory.getTokenIssuer(message);

        var token = validationGenerator.createToken(
            message,
            businessDocument,
            null,
            issuer
        );

        var pdfToken = trustOKTokenGenerator.generate(token);

        assertThat(pdfToken).isNotNull();
        assertThat(pdfToken.getMimeType()).isEqualTo(MimeTypeEnum.PDF);
        assertThat(SecurityUtil.hasSignature(pdfToken)).isTrue();
        try (var stream = pdfToken.openStream()) {
            assertThat(SecurityUtil.getPageNumbers(stream.readAllBytes())).isEqualTo(3);
        }
    }

    @Test
    void should_create_pdf_trust_ok_token_for_signature_based_document_with_two_signatures_and_sign_it_successfully()
        throws IOException {
        var message = MessageTestFixtures.createOutboundBusinessMessage();
        var document = FileTestFixtures.readAsBytes("raw/document/Two_Signatures.pdf");
        var businessDocument = new InMemoryDocument(document);
        var issuer = validationFactory.getTokenIssuer(message);

        var token = validationGenerator.createToken(
            message,
            businessDocument,
            null,
            issuer
        );

        var pdfToken = trustOKTokenGenerator.generate(token);

        assertThat(pdfToken).isNotNull();
        assertThat(pdfToken.getMimeType()).isEqualTo(MimeTypeEnum.PDF);
        assertThat(SecurityUtil.hasSignature(pdfToken)).isTrue();
        try (var stream = pdfToken.openStream()) {
            assertThat(SecurityUtil.getPageNumbers(stream.readAllBytes())).isEqualTo(5);
        }
    }

    @Test
    void should_create_pdf_trust_ok_token_for_auth_based_document_and_sign_it_successfully()
        throws IOException {
        var message = MessageTestFixtures
            .createOutboundBusinessMessage()
            .toBuilder()
            .businessContent(
                MessageContentTestFixtures
                    .createContent()
                    .toBuilder()
                    .businessDocument(
                        ConnectorMessageDocumentTestFixtures
                            .createDocumentWithoutSignature()
                    )
                    .build()
            )
            .build();
        var document = FileTestFixtures.readAsBytes("raw/document/NonSigned.pdf");
        var businessDocument = new InMemoryDocument(document);
        var issuer = validationFactory.getTokenIssuer(message);

        var token = validationGenerator.createToken(
            message,
            businessDocument,
            null,
            issuer
        );

        var pdfToken = trustOKTokenGenerator.generate(token);

        assertThat(pdfToken).isNotNull();
        assertThat(pdfToken.getMimeType()).isEqualTo(MimeTypeEnum.PDF);
        assertThat(SecurityUtil.hasSignature(pdfToken)).isTrue();
        try (var stream = pdfToken.openStream()) {
            assertThat(SecurityUtil.getPageNumbers(stream.readAllBytes())).isGreaterThanOrEqualTo(2);
        }
    }
}
