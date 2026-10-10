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


import eu.ecodex.connector.domain.model.message.attachment.ConnectorAttachmentStorage;
import eu.ecodex.connector.domain.model.message.attachment.ConnectorAttachmentType;
import eu.ecodex.connector.domain.model.paging.ConnectorPageRequest;
import jakarta.annotation.Nonnull;
import java.util.List;
import lombok.Builder;
import lombok.NonNull;

/**
 * Groups the pagination parameters and the optional filter criteria used to list the attachments of
 * connector messages.
 *
 * @param pageRequest       the pagination and query parameters used to retrieve the attachments
 *                          (must not be null)
 * @param messageIdentifier identifier of the message the attachments belong to; ignored if
 *                          {@code null} or empty
 * @param name              attachment name (or part of a name) to match; ignored if {@code null} or
 *                          empty
 * @param types             attachment types ({@link ConnectorAttachmentType}) to match; ignored if
 *                          {@code null} or empty
 * @param storages          storage modes ({@link ConnectorAttachmentStorage}) to match; ignored if
 *                          {@code null} or empty
 * @param dateFilter        date range ({@link ConnectorDateRangeFilter}) restricting the
 *                          attachments to a time window; ignored if {@code null}, and a
 *                          {@code null} bound means the range is open on that side
 */
@Builder
public record ConnectorAttachmentsListFilter(
    @Nonnull ConnectorPageRequest pageRequest,
    String messageIdentifier,
    String name,
    List<ConnectorAttachmentType> types,
    List<ConnectorAttachmentStorage> storages,
    ConnectorDateRangeFilter dateFilter
) {
    /**
     * Normalizes the filter: {@code null} {@code types} and {@code storages} lists become empty
     * lists, and non-null lists are defensively copied to immutable ones.
     */
    public ConnectorAttachmentsListFilter {
        types = types == null ? List.of() : List.copyOf(types);
        storages = storages == null ? List.of() : List.copyOf(storages);
    }

    /**
     * Creates a filter with all criteria.
     *
     * @param pageRequest       pagination and sorting information; never {@code null}
     * @param messageIdentifier message identifier to match, or {@code null} to ignore
     * @param name              attachment name to match, or {@code null} to ignore
     * @param types             attachment types to include, or {@code null}/empty to ignore
     * @param storages          attachment storage kinds to include, or {@code null}/empty to
     *                          ignore
     * @param dateFilter        creation date range, or {@code null} to ignore
     *
     * @return a new filter instance
     */
    public static ConnectorAttachmentsListFilter of(
        @NonNull ConnectorPageRequest pageRequest,
        String messageIdentifier,
        String name,
        List<ConnectorAttachmentType> types,
        List<ConnectorAttachmentStorage> storages,
        ConnectorDateRangeFilter dateFilter
    ) {
        return new ConnectorAttachmentsListFilter(
            pageRequest,
            messageIdentifier,
            name,
            types,
            storages,
            dateFilter
        );
    }

    /**
     * Creates a filter that only paginates, without any filtering criteria.
     *
     * @param pageRequest pagination and sorting information; never {@code null}
     *
     * @return a new filter instance matching all attachments
     */
    public static ConnectorAttachmentsListFilter of(@NonNull ConnectorPageRequest pageRequest) {
        return new ConnectorAttachmentsListFilter(pageRequest, null, null, null, null, null);
    }
}
