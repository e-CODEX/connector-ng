/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.security;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.spi.validation.CommonCertificateVerifier;
import eu.europa.esig.dss.validation.SignedDocumentValidator;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class SecurityUtil {
    public static boolean hasSignature(DSSDocument document) {
        var validator = SignedDocumentValidator.fromDocument(document);
        validator.setCertificateVerifier(new CommonCertificateVerifier(true));

        var reports = validator.validateDocument();
        var simpleReport = reports.getSimpleReport();

        return !simpleReport.getSignatureIdList().isEmpty();
    }

    public static int getPageNumbers(byte[] mergedPdf) throws IOException {
        int totalPages = 0;

        var pdfOutputStream = new ByteArrayOutputStream();
        try (var pdfDoc = new PdfDocument(
                new PdfReader(new ByteArrayInputStream(mergedPdf)),
                new PdfWriter(pdfOutputStream)
        )) {

            totalPages = pdfDoc.getNumberOfPages();
        }

        return totalPages;
    }
}
