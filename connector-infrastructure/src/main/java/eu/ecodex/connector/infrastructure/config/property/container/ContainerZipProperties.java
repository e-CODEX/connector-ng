/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.config.property.container;

import eu.ecodex.connector.infrastructure.adapter.out.security.util.ZipLimits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.unit.DataSize;

/**
 * Configuration properties for the limits applied when an ASiC-S container is extracted. A
 * container which expands beyond them is refused.
 */
@Getter
@Setter
public class ContainerZipProperties {
    /**
     * The maximum number of entries of a ZIP document.
     */
    @Min(1)
    private int maxEntries = ZipLimits.DEFAULT.maxEntries();

    /**
     * The maximum size of one entry, once expanded.
     */
    @NotNull
    private DataSize maxEntrySize = DataSize.ofBytes(ZipLimits.DEFAULT.maxEntrySize());

    /**
     * The maximum size of all the entries of a ZIP document together, once expanded.
     */
    @NotNull
    private DataSize maxTotalSize = DataSize.ofBytes(ZipLimits.DEFAULT.maxTotalSize());

    /**
     * Converts the properties to the limits of the extraction.
     *
     * @return the limits
     */
    public ZipLimits toLimits() {
        return new ZipLimits(maxEntries, maxEntrySize.toBytes(), maxTotalSize.toBytes());
    }
}
