/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.domain.model.filter;


import eu.ecodex.connector.domain.model.message.ConnectorMessageDirection;
import eu.ecodex.connector.domain.model.paging.ConnectorPageRequest;
import jakarta.annotation.Nonnull;
import lombok.Builder;
import lombok.NonNull;


/**
 * Groups the pagination parameters and the optional filter criteria used to list connector
 * messages.
 *
 * @param pageRequest              the pagination and query parameters used to retrieve the messages
 *                                 (must not be null)
 * @param identifier               identifier of the message to match; ignored if {@code null} or
 *                                 empty
 * @param backendName              name of the backend the messages are associated with; ignored if
 *                                 {@code null} or empty
 * @param direction                direction of the message ({@link ConnectorMessageDirection}) to
 *                                 match; ignored if {@code null}
 * @param businessDomainIdentifier identifier of the business domain the messages belong to; ignored
 *                                 if {@code null} or empty
 * @param service                  business service of the message to match; ignored if {@code null}
 *                                 or empty
 * @param action                   business action of the message to match; ignored if {@code null}
 *                                 or empty
 * @param dateFilter               date range ({@link ConnectorDateRangeFilter}) restricting the
 *                                 messages to a time window; ignored if {@code null}, and a
 *                                 {@code null} bound means the range is open on that side
 */
@Builder
public record ConnectorMessagesListFilter(
    @Nonnull ConnectorPageRequest pageRequest,
    String identifier,
    String backendName,
    ConnectorMessageDirection direction,
    String businessDomainIdentifier,
    String service,
    String action,
    ConnectorDateRangeFilter dateFilter
) {
    /**
     * Creates a filter with all criteria.
     *
     * @param pageRequest              pagination and sorting information; never {@code null}
     * @param identifier               message identifier to match, or {@code null} to ignore
     * @param backendName              backend name to match, or {@code null} to ignore
     * @param direction                message direction to match, or {@code null} to ignore
     * @param businessDomainIdentifier business domain identifier to match, or {@code null} to
     *                                 ignore
     * @param service                  business service to match, or {@code null} to ignore
     * @param action                   business action to match, or {@code null} to ignore
     * @param dateFilter               creation date range, or {@code null} to ignore
     *
     * @return a new filter instance
     */
    public static ConnectorMessagesListFilter of(
        @NonNull ConnectorPageRequest pageRequest,
        String identifier,
        String backendName,
        ConnectorMessageDirection direction,
        String businessDomainIdentifier,
        String service,
        String action,
        ConnectorDateRangeFilter dateFilter
    ) {
        return new ConnectorMessagesListFilter(
            pageRequest,
            identifier,
            backendName,
            direction,
            businessDomainIdentifier,
            service,
            action,
            dateFilter
        );
    }

    /**
     * Creates a filter that only paginates, without any filtering criteria.
     *
     * @param pageRequest pagination and sorting information; never {@code null}
     *
     * @return a new filter instance matching all messages
     */
    public static ConnectorMessagesListFilter of(@NonNull ConnectorPageRequest pageRequest) {
        return new ConnectorMessagesListFilter(
            pageRequest,
            null,
            null,
            null,
            null,
            null,
            null,
            null
        );
    }
}
