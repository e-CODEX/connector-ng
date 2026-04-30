/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.security.token.trustok.pdf;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;

/**
 * Creates and returns a {@code PDFDocumentFonts} instance using iText {@link StandardFonts} (no
 * external font files required).
 *
 * @param bold    Helvetica Bold – emphasis and inline labels.
 * @param regular Helvetica – default body text.
 * @param header1 Helvetica Bold – H1 headings.
 * @param header2 Helvetica Bold – H2 headings.
 * @param header3 Helvetica Bold – H3 headings.
 * @param header4 Helvetica Bold Oblique – H4 headings.
 * @param header5 Helvetica – H5 headings.
 * @param mono    Courier – monospaced / code text.
 */
public record PDFDocumentFonts(
        PdfFont bold,
        PdfFont regular,
        PdfFont header1,
        PdfFont header2,
        PdfFont header3,
        PdfFont header4,
        PdfFont header5,
        PdfFont mono
) {
    /**
     * Creates and returns a {@code PDFDocumentFonts} instance using iText {@link StandardFonts} (no
     * external font files required).
     *
     * @return a fully initialized {@code PDFDocumentFonts} instance.
     * @throws RuntimeException if any font cannot be created by {@link PdfFontFactory}.
     */
    public static PDFDocumentFonts createFonts() {
        try {
            return new PDFDocumentFonts(
                    PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD),
                    PdfFontFactory.createFont(StandardFonts.HELVETICA),
                    PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD),
                    PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD),
                    PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD),
                    PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLDOBLIQUE),
                    PdfFontFactory.createFont(StandardFonts.HELVETICA),
                    PdfFontFactory.createFont(StandardFonts.COURIER)
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to create PDF fonts", e);
        }
    }
}
