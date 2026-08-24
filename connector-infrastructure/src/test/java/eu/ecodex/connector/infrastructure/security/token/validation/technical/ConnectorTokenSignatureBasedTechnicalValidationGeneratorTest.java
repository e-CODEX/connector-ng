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
import eu.ecodex.connector.infrastructure.outbound.security.model.token.ConnectorTokenTechnicalTrustLevel;
import eu.ecodex.connector.infrastructure.outbound.security.model.token.ConnectorTokenValidation;
import eu.ecodex.connector.infrastructure.outbound.security.token.validation.technical.ConnectorTokenSignatureBasedTechnicalValidationGenerator;
import eu.ecodex.connector.infrastructure.security.token.BaseTokenTest;
import eu.europa.esig.dss.model.InMemoryDocument;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("ConnectorTokenSignatureBasedTechnicalValidationGenerator")
public class ConnectorTokenSignatureBasedTechnicalValidationGeneratorTest extends BaseTokenTest {
    @Autowired
    private ConnectorTokenSignatureBasedTechnicalValidationGenerator validationGenerator;

    private ConnectorTokenValidation validateDocument(String documentPath) throws Exception {
        var document = FileTestFixtures.readAsBytes(documentPath);
        var businessDocument = new InMemoryDocument(document);

        return validationGenerator.generate(businessDocument, null);
    }

    private ConnectorTokenValidation validateDocumentWithDetachedSignature(
        String signaturePath
    ) throws Exception {
        var document = FileTestFixtures.readAsBytes("raw/document/NonSigned.pdf");
        var detached = FileTestFixtures.readAsBytes(signaturePath);

        var businessDocument = new InMemoryDocument(document);
        var detachedSignature = new InMemoryDocument(detached);

        return validationGenerator.generate(
            businessDocument,
            detachedSignature
        );
    }

    private void assertTechnicalFailure(
        ConnectorTokenValidation validation,
        String expectedComment
    ) {
        assertTechnicalFailure(validation, expectedComment, true);
    }

    private void assertTechnicalFailure(
        ConnectorTokenValidation validation,
        String expectedComment,
        boolean assertOriginalReport
    ) {
        assertThat(validation).isNotNull();

        var technicalResult = validation.getTechnicalResult();

        assertThat(technicalResult.getTrustLevel())
            .isEqualTo(ConnectorTokenTechnicalTrustLevel.FAIL);

        assertThat(technicalResult.getComment())
            .isEqualTo(expectedComment);

        if (assertOriginalReport) {
            assertThat(validation.getOriginalValidationReport()).isNotNull();
            assertThat(validation.getOriginalValidationReport().getReports())
                .isNotNull();
            assertThat(validation.getOriginalValidationReport().getAny())
                .isNotNull();
        }
    }

    @Nested
    @DisplayName("embedded signatures")
    class EmbeddedSignatures {
        @Test
        void should_fail_when_document_has_no_signature() throws Exception {
            var validation = validateDocument("raw/document/NonSigned.pdf");

            assertTechnicalFailure(
                validation,
                "Unable to find a signature.",
                false
            );
        }

        @Test
        void should_fail_when_embedded_signature_is_mathematically_incorrect()
            throws Exception {
            var validation = validateDocument("raw/document/Signed.pdf");

            assertTechnicalFailure(
                validation,
                "The signature is not mathematically correct."
            );
        }

        @Test
        void should_fail_when_embedded_signature_conclusion_is_not_sufficient()
            throws Exception {
            var validation = validateDocument("raw/document/Signed_Visible.pdf");

            assertTechnicalFailure(
                validation,
                "The signature conclusion is not sufficient."
            );
        }

        @Test
        void should_fail_when_document_has_two_embedded_signatures()
            throws Exception {
            var validation = validateDocument("raw/document/Two_Signatures.pdf");

            assertTechnicalFailure(
                validation,
                "The signature is not mathematically correct."
            );
        }

        @Test
        void should_fail_when_embedded_signature_is_expired()
            throws Exception {
            var validation = validateDocument("raw/document/SignExpired.pdf");

            assertTechnicalFailure(
                validation,
                "The signature is not mathematically correct."
            );
        }
    }

    @Nested
    @DisplayName("detached signatures")
    class DetachedSignatures {
        @Test
        void should_fail_when_xades_detached_signature_is_present()
            throws Exception {
            var validation = validateDocumentWithDetachedSignature(
                "raw/signature/DetachedNonSigned.xml"
            );

            // XAdES signatures are detected by DSS but do not provide
            // enough information for signature-based validation.
            assertTechnicalFailure(
                validation,
                "Unable to find a signature."
            );
        }

        @Test
        void should_fail_when_p7s_detached_signature_conclusion_is_not_sufficient()
            throws Exception {
            var validation = validateDocumentWithDetachedSignature(
                "raw/signature/DetachedNonSigned.p7s"
            );

            assertTechnicalFailure(
                validation,
                "The signature conclusion is not sufficient."
            );
        }
    }
}