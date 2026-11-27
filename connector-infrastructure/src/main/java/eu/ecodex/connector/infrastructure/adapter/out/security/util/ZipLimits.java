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

/**
 * The limits applied when a ZIP document is extracted in memory, which keep a small document from
 * exhausting the memory once expanded (a "zip bomb").
 *
 * @param maxEntries   the maximum number of entries
 * @param maxEntrySize the maximum size of one entry, once expanded, in bytes
 * @param maxTotalSize the maximum size of all the entries together, once expanded, in bytes
 */
public record ZipLimits(int maxEntries, long maxEntrySize, long maxTotalSize) {
    private static final long MAX_SIZE = 250L * 1024 * 1024;

    /**
     * The default limits: 1000 entries, and 250 MB for an entry and for all the entries together.
     */
    public static final ZipLimits DEFAULT = new ZipLimits(1_000, MAX_SIZE, MAX_SIZE);

    /**
     * Creates the limits.
     */
    public ZipLimits {
        if (maxEntries < 1 || maxEntrySize < 1 || maxTotalSize < 1) {
            throw new IllegalArgumentException("The ZIP limits must be positive");
        }
    }
}
