/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.repository;

import eu.ecodex.connector.domain.model.paging.ConnectorPageRequest;
import eu.ecodex.connector.domain.model.paging.ConnectorPageResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

/**
 * Utility class for mapping between Spring Data's Pageable and ConnectorPageRequest.
 */
@Component
public class PaginationMapper {
    /**
     * Converts a {@link ConnectorPageRequest} to a {@link Pageable} instance.
     *
     * @param request the pagination request containing the page index and page size
     *
     * @return a Pageable instance configured with the page index, page size, and sort order
     */
    public Pageable toPageable(ConnectorPageRequest request) {
        return PageRequest.of(
                request.page(),
                request.size(),
                Sort.by(Sort.Direction.DESC, "createdAt")
        );
    }

    /**
     * Converts a {@link Page} instance to a {@link ConnectorPageResult}.
     *
     * @param <T>  the type of elements contained in the page
     * @param page the {@link Page} instance containing the content and pagination metadata
     *
     * @return a {@link ConnectorPageResult} representing the paginated result with content, total
     *         elements, current page index, and page size extracted from the given {@link Page}
     */
    public <T> ConnectorPageResult<T> toPageResult(Page<T> page) {
        return new ConnectorPageResult<>(
                page.getContent(),
                page.getTotalElements(),
                page.getNumber(),
                page.getSize()
        );
    }
}
