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


import eu.ecodex.connector.domain.model.message.transport.ConnectorMessageTransportStatus;
import eu.ecodex.connector.domain.model.paging.ConnectorPageRequest;
import jakarta.annotation.Nonnull;
import java.util.List;
import lombok.Builder;
import lombok.NonNull;

/**
 * Groups the pagination parameters and the optional filter criteria used to list the transport
 * steps of connector messages.
 *
 * @param pageRequest                     the pagination and query parameters used to retrieve the
 *                                        transport steps (must not be null)
 * @param messageOrRemoteSystemIdentifier identifier of the message, or of the message as known by
 *                                        the remote system, that the transport steps belong to;
 *                                        ignored if {@code null} or empty
 * @param linkPartnerName                 name of the link partner involved in the transport step;
 *                                        ignored if {@code null} or empty
 * @param statuses                        transport statuses
 *                                        ({@link ConnectorMessageTransportStatus}) to match;
 *                                        ignored if {@code null} or empty
 * @param dateFilter                      date range ({@link ConnectorDateRangeFilter}) restricting
 *                                        the transport steps to a time window; ignored if
 *                                        {@code null}, and a {@code null} bound means the range is
 *                                        open on that side
 */
@Builder
public record ConnectorTransportStepsListFilter(
    @Nonnull ConnectorPageRequest pageRequest,
    String messageOrRemoteSystemIdentifier,
    String linkPartnerName,
    List<ConnectorMessageTransportStatus> statuses,
    ConnectorDateRangeFilter dateFilter
) {
    /**
     * Normalizes the filter: a {@code null} {@code statuses} list becomes an empty list, and a
     * non-null list is defensively copied to an immutable one.
     */
    public ConnectorTransportStepsListFilter {
        statuses = statuses == null ? List.of() : List.copyOf(statuses);
    }

    /**
     * Creates a filter with all criteria.
     *
     * @param pageRequest                     pagination and sorting information; never
     *                                        {@code null}
     * @param messageOrRemoteSystemIdentifier message or remote system identifier to match, or
     *                                        {@code null} to ignore
     * @param linkPartnerName                 link partner name to match, or {@code null} to ignore
     * @param statuses                        transport statuses to include, or {@code null}/empty
     *                                        to ignore
     * @param dateFilter                      creation date range, or {@code null} to ignore
     *
     * @return a new filter instance
     */
    public static ConnectorTransportStepsListFilter of(
        @NonNull ConnectorPageRequest pageRequest,
        String messageOrRemoteSystemIdentifier,
        String linkPartnerName,
        List<ConnectorMessageTransportStatus> statuses,
        ConnectorDateRangeFilter dateFilter
    ) {
        return new ConnectorTransportStepsListFilter(
            pageRequest,
            messageOrRemoteSystemIdentifier,
            linkPartnerName,
            statuses,
            dateFilter
        );
    }

    /**
     * Creates a filter that only paginates, without any filtering criteria.
     *
     * @param pageRequest pagination and sorting information; never {@code null}
     *
     * @return a new filter instance matching all transport steps
     */
    public static ConnectorTransportStepsListFilter of(@NonNull ConnectorPageRequest pageRequest) {
        return new ConnectorTransportStepsListFilter(pageRequest, null, null, null, null);
    }
}
