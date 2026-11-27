/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.adapter.out.security.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.model.InMemoryDocument;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.junit.jupiter.api.Test;

class ZipStreamUtilTest {
    private static final ZipLimits SMALL = new ZipLimits(3, 1_000, 1_500);

    @Test
    void should_extract_the_entries_in_order() throws IOException {
        var zip = zip(Map.of("a.txt", bytes(10)));

        var documents = ZipStreamUtil.extract(zip);

        assertThat(documents).extracting(DSSDocument::getName).containsExactly("a.txt");
        assertThat(documents.getFirst().openStream().readAllBytes()).hasSize(10);
    }

    @Test
    void should_refuse_an_entry_which_expands_beyond_the_entry_limit() throws IOException {
        // 10 MB of zeros compress to a few KB
        var bomb = zip(Map.of("bomb.bin", bytes(10_000_000)));
        assertThat(bomb.openStream().readAllBytes()).hasSizeLessThan(100_000);

        assertThatThrownBy(() -> ZipStreamUtil.extract(bomb, SMALL))
            .isInstanceOf(ConnectorZipLimitExceededException.class)
            .hasMessageContaining("1000 bytes");
    }

    @Test
    void should_refuse_entries_which_expand_beyond_the_total_limit() throws IOException {
        var entries = new LinkedHashMap<String, byte[]>();
        entries.put("a.bin", bytes(800));
        entries.put("b.bin", bytes(800));

        var zipEntries = zip(entries);

        assertThatThrownBy(() -> ZipStreamUtil.extract(zipEntries, SMALL))
            .isInstanceOf(ConnectorZipLimitExceededException.class)
            .hasMessageContaining("1500 bytes");
    }

    @Test
    void should_accept_entries_which_reach_the_limits_exactly() throws IOException {
        var entries = new LinkedHashMap<String, byte[]>();
        entries.put("a.bin", bytes(1_000));
        entries.put("b.bin", bytes(500));

        assertThat(ZipStreamUtil.extract(zip(entries), SMALL)).hasSize(2);
    }

    @Test
    void should_refuse_a_zip_with_too_many_entries() throws IOException {
        var entries = new LinkedHashMap<String, byte[]>();
        for (var i = 0; i < 4; i++) {
            entries.put("e" + i, bytes(1));
        }

        var zipEntries = zip(entries);

        assertThatThrownBy(() -> ZipStreamUtil.extract(zipEntries, SMALL))
            .isInstanceOf(ConnectorZipLimitExceededException.class)
            .hasMessageContaining("more than 3 entries");
    }

    @Test
    void should_refuse_an_unsafe_entry_name() throws IOException {
        for (var name : new String[] {"../evil.txt", "a/../../evil.txt", "/etc/passwd",
            "..\\evil.txt"}) {
            var zip = zip(Map.of(name, bytes(1)));
            assertThatThrownBy(() -> ZipStreamUtil.extract(zip))
                .as(name)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("unsafe name");
        }
    }

    @Test
    void should_accept_a_name_which_only_contains_dots() throws IOException {
        var documents = ZipStreamUtil.extract(zip(Map.of("my..file.txt", bytes(1))));

        assertThat(documents).extracting(DSSDocument::getName).containsExactly("my..file.txt");
    }

    @Test
    void should_refuse_a_document_which_is_not_a_zip() {
        var notZip = new InMemoryDocument(bytes(20), "x.zip");

        assertThatThrownBy(() -> ZipStreamUtil.extract(notZip))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("not in zip format");
    }

    @Test
    void should_refuse_limits_which_are_not_positive() {
        assertThatThrownBy(() -> new ZipLimits(0, 1, 1))
            .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new ZipLimits(1, 0, 1))
            .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new ZipLimits(1, 1, 0))
            .isInstanceOf(IllegalArgumentException.class);
    }

    private static byte[] bytes(int size) {
        return new byte[size];
    }

    private static DSSDocument zip(Map<String, byte[]> entries) throws IOException {
        var out = new ByteArrayOutputStream();
        try (var zip = new ZipOutputStream(out)) {
            for (var entry : entries.entrySet()) {
                zip.putNextEntry(new ZipEntry(entry.getKey()));
                zip.write(entry.getValue());
                zip.closeEntry();
            }
        }
        return new InMemoryDocument(out.toByteArray(), "test.zip");
    }
}
