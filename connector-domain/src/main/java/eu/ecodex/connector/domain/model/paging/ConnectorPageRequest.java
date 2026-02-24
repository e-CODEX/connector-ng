/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.domain.model.paging;

import lombok.Builder;

/**
 * Represents a pagination request for a connector.
 *
 * <p>This record encapsulates pagination parameters including the
 * zero-based page index and the page size. Validation is performed to ensure that the page index is
 * non-negative and the size is within an acceptable range.</p>
 *
 * @param page the zero-based index of the requested page (must be {@code >= 0})
 * @param size the number of elements per page (must be {@code > 0} and {@code <= 100})
 */
@Builder
public record ConnectorPageRequest(int page, int size) {
    /**
     * Compact constructor with validation rules.
     */
    public ConnectorPageRequest {
        if (page < 0) {
            throw new IllegalArgumentException("Page must be >= 0");
        }

        if (size > 100) {
            throw new IllegalArgumentException("Max size is 100");
        }
    }
}
