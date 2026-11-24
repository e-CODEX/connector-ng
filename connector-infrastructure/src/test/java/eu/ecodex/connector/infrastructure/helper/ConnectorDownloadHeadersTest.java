/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.helper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.MediaType;

@DisplayName("ConnectorDownloadHeaders")
@SuppressWarnings({"MissingJavadocType", "MissingJavadocMethod"})
class ConnectorDownloadHeadersTest {
    @Test
    void should_quote_a_simple_filename() {
        var header = ConnectorDownloadHeaders.attachment("report.pdf");

        var parsed = ContentDisposition.parse(header);
        assertThat(parsed.getType()).isEqualTo("attachment");
        assertThat(parsed.getFilename()).isEqualTo("report.pdf");
    }

    @Test
    void should_not_let_a_filename_add_a_header_parameter() {
        var header = ConnectorDownloadHeaders.attachment("a.pdf; filename=evil.exe");

        var parsed = ContentDisposition.parse(header);
        assertThat(parsed.getFilename()).isEqualTo("a.pdf; filename=evil.exe");
        assertThat(header).doesNotContain("filename=evil.exe;");
    }

    @Test
    void should_neutralize_quotes_and_line_breaks_in_a_filename() {
        var header = ConnectorDownloadHeaders.attachment("a\"b\r\nSet-Cookie: x=1.pdf");

        // the header itself stays on one line, so it cannot inject another header
        assertThat(header).doesNotContain("\r").doesNotContain("\n");
    }

    @Test
    void should_keep_a_non_ascii_filename() {
        var header = ConnectorDownloadHeaders.attachment("résumé €.pdf");

        assertThat(ContentDisposition.parse(header).getFilename()).isEqualTo("résumé €.pdf");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  "})
    void should_use_a_default_filename_when_it_is_blank(String filename) {
        var header = ConnectorDownloadHeaders.attachment(filename);

        assertThat(ContentDisposition.parse(header).getFilename()).isEqualTo("download");
    }

    @Test
    void should_parse_a_valid_content_type() {
        assertThat(ConnectorDownloadHeaders.mediaType("application/pdf"))
            .isEqualTo(MediaType.APPLICATION_PDF);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"not a media type", "text", "a/b/c", "*/*;q=x"})
    void should_fall_back_to_octet_stream_when_the_content_type_is_invalid(String contentType) {
        assertThat(ConnectorDownloadHeaders.mediaType(contentType))
            .isEqualTo(MediaType.APPLICATION_OCTET_STREAM);
    }
}
