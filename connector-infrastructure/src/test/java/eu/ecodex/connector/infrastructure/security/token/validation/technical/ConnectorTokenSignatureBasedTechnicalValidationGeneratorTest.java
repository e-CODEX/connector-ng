/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.security.token.validation.technical;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import eu.ecodex.connector.FileTestFixtures;
import eu.ecodex.connector.infrastructure.security.model.token.ConnectorTokenTechnicalTrustLevel;
import eu.ecodex.connector.infrastructure.security.token.BaseTokenTest;
import eu.europa.esig.dss.model.InMemoryDocument;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class ConnectorTokenSignatureBasedTechnicalValidationGeneratorTest extends BaseTokenTest {
    @Autowired
    private ConnectorTokenSignatureBasedTechnicalValidationGenerator validationGenerator;

    @Test
    void should_validate_document_with_no_embedded_or_detached_signature_successfully()
            throws Exception {
        var document = FileTestFixtures.readAsBytes("raw/document/NonSigned.pdf");
        var businessDocument = new InMemoryDocument(document);

        var validation = validationGenerator.generate(businessDocument, null);
        assertThat(validation).isNotNull();

        var technicalResult = validation.getTechnicalResult();
        assertThat(technicalResult.getTrustLevel()).isEqualTo(ConnectorTokenTechnicalTrustLevel.FAIL);
        assertThat(technicalResult.getComment()).isEqualTo("Unable to find a signature.");
    }

    @Test
    void should_validate_document_with_mathematically_incorrect_embedded_signature_successfully()
            throws Exception {
        var document = FileTestFixtures.readAsBytes("raw/document/Signed.pdf");
        var businessDocument = new InMemoryDocument(document);

        var validation = validationGenerator.generate(businessDocument, null);
        assertThat(validation).isNotNull();

        var technicalResult = validation.getTechnicalResult();
        assertThat(technicalResult.getTrustLevel()).isEqualTo(ConnectorTokenTechnicalTrustLevel.FAIL);
        assertThat(technicalResult.getComment()).isEqualTo(
                "The signature is not mathematically correct.");

        var originalReport = validation.getOriginalValidationReport();
        assertThat(originalReport).isNotNull();
        assertThat(originalReport.getReports()).isNotNull();
        assertThat(originalReport.getAny()).isNotNull();
    }

    @Test
    void should_validate_document_with_non_sufficient_conclusion_embedded_signature_successfully()
            throws Exception {
        var document = FileTestFixtures.readAsBytes("raw/document/Signed_Visible.pdf");
        var businessDocument = new InMemoryDocument(document);

        var validation = validationGenerator.generate(businessDocument, null);
        assertThat(validation).isNotNull();

        var technicalResult = validation.getTechnicalResult();
        assertThat(technicalResult.getTrustLevel()).isEqualTo(ConnectorTokenTechnicalTrustLevel.FAIL);
        assertThat(technicalResult.getComment()).isEqualTo(
                "The signature conclusion is not sufficient.");

        var originalReport = validation.getOriginalValidationReport();
        assertThat(originalReport).isNotNull();
        assertThat(originalReport.getReports()).isNotNull();
        assertThat(originalReport.getAny()).isNotNull();
    }

    @Test
    void should_validate_document_with_two_embedded_signatures_successfully() throws Exception {
        var document = FileTestFixtures.readAsBytes("raw/document/Two_Signatures.pdf");
        var businessDocument = new InMemoryDocument(document);

        var validation = validationGenerator.generate(businessDocument, null);
        assertThat(validation).isNotNull();

        var technicalResult = validation.getTechnicalResult();
        assertThat(technicalResult.getTrustLevel()).isEqualTo(ConnectorTokenTechnicalTrustLevel.FAIL);
        assertThat(technicalResult.getComment()).isEqualTo(
                "The signature is not mathematically correct.");

        var originalReport = validation.getOriginalValidationReport();
        assertThat(originalReport).isNotNull();
        assertThat(originalReport.getReports()).isNotNull();
        assertThat(originalReport.getAny()).isNotNull();
    }

    @Test
    void should_validate_document_with_expired_embedded_signature_successfully() throws Exception {
        var document = FileTestFixtures.readAsBytes("raw/document/SignExpired.pdf");
        var businessDocument = new InMemoryDocument(document);

        var validation = validationGenerator.generate(businessDocument, null);
        assertThat(validation).isNotNull();

        var technicalResult = validation.getTechnicalResult();
        assertThat(technicalResult.getTrustLevel()).isEqualTo(ConnectorTokenTechnicalTrustLevel.FAIL);
        assertThat(technicalResult.getComment()).isEqualTo(
                "The signature is not mathematically correct.");

        var originalReport = validation.getOriginalValidationReport();
        assertThat(originalReport).isNotNull();
        assertThat(originalReport.getReports()).isNotNull();
        assertThat(originalReport.getAny()).isNotNull();
    }

    @Test
    void should_validate_document_with_xades_detached_signature_successfully() throws Exception {
        var document = FileTestFixtures.readAsBytes("raw/document/NonSigned.pdf");
        // XADES Signature
        var detached = FileTestFixtures.readAsBytes("raw/signature/DetachedNonSigned.xml");
        var businessDocument = new InMemoryDocument(document);
        var detachedSignature = new InMemoryDocument(detached);

        var validation = validationGenerator.generate(businessDocument, detachedSignature);
        assertThat(validation).isNotNull();

        var technicalResult = validation.getTechnicalResult();
        assertThat(technicalResult.getTrustLevel()).isEqualTo(ConnectorTokenTechnicalTrustLevel.FAIL);
        // Filter out XAdES signatures — detected by DSS but not providing enough information
        assertThat(technicalResult.getComment()).isEqualTo("Unable to find a signature.");

        var originalReport = validation.getOriginalValidationReport();
        assertThat(originalReport).isNotNull();
        assertThat(originalReport.getReports()).isNotNull();
        assertThat(originalReport.getAny()).isNotNull();
    }

    @Test
    void should_validate_document_with_p7s_detached_signature_successfully() throws Exception {
        var document = FileTestFixtures.readAsBytes("raw/document/NonSigned.pdf");
        // P7S Signature
        var detached = FileTestFixtures.readAsBytes("raw/signature/DetachedNonSigned.p7s");
        var businessDocument = new InMemoryDocument(document);
        var detachedSignature = new InMemoryDocument(detached);

        var validation = validationGenerator.generate(businessDocument, detachedSignature);
        assertThat(validation).isNotNull();

        var technicalResult = validation.getTechnicalResult();
        assertThat(technicalResult.getTrustLevel()).isEqualTo(ConnectorTokenTechnicalTrustLevel.FAIL);
        assertThat(technicalResult.getComment()).isEqualTo(
                "The signature conclusion is not sufficient.");

        var originalReport = validation.getOriginalValidationReport();
        assertThat(originalReport).isNotNull();
        assertThat(originalReport.getReports()).isNotNull();
        assertThat(originalReport.getAny()).isNotNull();
    }
}
