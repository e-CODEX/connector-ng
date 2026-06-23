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
 * @param page          the zero-based index of the requested page (must be {@code >= 0})
 * @param size          the number of elements per page (must be {@code > 0} and {@code <= 100})
 * @param sortBy        the field to sort by
 * @param sortDirection the sort direction (ascending or descending)
 */
@Builder
public record ConnectorPageRequest(int page, int size, String sortBy, SortDirection sortDirection) {
    private static final int MAX_PAGE_SIZE = 100;
    private static final int MIN_PAGE_SIZE = 1;

    /**
     * Compact constructor with validation rules.
     */
    public ConnectorPageRequest {
        if (page < 0) {
            throw new IllegalArgumentException("Page must be greater than or equal to 0");
        }

        if (size < 1 || size > 100) {
            throw new IllegalArgumentException(
                    "size must be between %d and %d".formatted(MIN_PAGE_SIZE, MAX_PAGE_SIZE)
            );
        }

        if (sortDirection != null && !(sortDirection == SortDirection.ASC)
                && !(sortDirection == SortDirection.DESC)) {
            throw new IllegalArgumentException("Sort direction must be ASC or DESC");
        }
    }

    public static ConnectorPageRequest of(int page, int size) {
        return new ConnectorPageRequest(page, size, null, SortDirection.DESC);
    }

    public static ConnectorPageRequest of(
            int page,
            int size,
            String sortBy,
            SortDirection sortDirection) {
        return new ConnectorPageRequest(page, size, sortBy, sortDirection);
    }
}
