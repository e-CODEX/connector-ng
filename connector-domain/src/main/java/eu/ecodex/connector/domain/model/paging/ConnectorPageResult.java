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

import java.util.List;
import lombok.Builder;

/**
 * Represents a paginated result returned by the connector.
 *
 * <p>This record encapsulates the content of a single page along with
 * pagination metadata such as the total number of elements, the current page index, and the page
 * size.
 *
 * @param <T>           the type of elements contained in the page
 * @param content       the list of elements on the current page
 * @param totalElements the total number of elements across all pages
 * @param page          the zero-based index of the current page
 * @param size          the number of elements per page
 */
@Builder
public record ConnectorPageResult<T>(
        List<T> content,
        long totalElements,
        int page,
        int size
) {
    public static <T> ConnectorPageResult<T> of(
            List<T> content,
            long totalElements,
            int page,
            int size
    ) {
        return new ConnectorPageResult<>(content, totalElements, page, size);
    }
}
