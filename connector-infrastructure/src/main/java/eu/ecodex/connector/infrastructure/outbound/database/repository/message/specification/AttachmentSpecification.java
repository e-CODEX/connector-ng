/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.outbound.database.repository.message.specification;

import eu.ecodex.connector.domain.model.filter.ConnectorAttachmentsListFilter;
import eu.ecodex.connector.domain.model.message.attachment.ConnectorAttachmentStorage;
import eu.ecodex.connector.domain.model.message.attachment.ConnectorAttachmentType;
import eu.ecodex.connector.infrastructure.outbound.database.entity.message.ConnectorMessageAttachmentEntity;
import eu.ecodex.connector.infrastructure.outbound.database.repository.DateRangeSpecifications;
import java.util.List;
import lombok.NonNull;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

/**
 * Specification for {@link ConnectorMessageAttachmentEntity}.
 */
public class AttachmentSpecification {
    private static final String MESSAGE_FIELD = "message";
    private static final String IDENTIFIER_FIELD = "identifier";
    private static final String TYPE_FIELD = "type";
    private static final String NAME_FIELD = "name";
    private static final String STORAGE_FIELD = "storage";
    private static final String CREATED_FIELD = "createdAt";

    public static Specification<ConnectorMessageAttachmentEntity> hasMessageIdentifierAndTypeIn(
        String messageIdentifier,
        List<ConnectorAttachmentType> types) {
        return Specification.where(withExactMessageIdentifier(messageIdentifier))
                            .and(withTypes(types));
    }

    /**
     * Builds a {@link Specification} that combines the search filters applicable to the attachments
     * of a connector message.
     *
     * @param filter the pagination parameters and filter criteria used to retrieve the attachments
     *               (must not be null)
     *
     * @return a {@link Specification} of {@link ConnectorMessageAttachmentEntity} ready to be
     *     passed to a repository ({@code JpaSpecificationExecutor})
     */
    public static Specification<ConnectorMessageAttachmentEntity> withFilters(
        @NonNull ConnectorAttachmentsListFilter filter) {
        return Specification
            .where(withMessageIdentifier(filter.messageIdentifier()))
            .and(withTypes(filter.types()))
            .and(withStorages(filter.storages()))
            .and(withName(filter.name()))
            .and(DateRangeSpecifications.withDateRange(filter.dateFilter(), CREATED_FIELD));
    }

    private static Specification<ConnectorMessageAttachmentEntity> withMessageIdentifier(
        String messageIdentifier) {
        return ((root, query, cb) -> {
            if (!StringUtils.hasText(messageIdentifier)) {
                return null;
            }
            var pattern = "%" + messageIdentifier + "%";


            return cb.like(root.get(MESSAGE_FIELD).get(IDENTIFIER_FIELD), pattern);
        });
    }

    private static Specification<ConnectorMessageAttachmentEntity> withExactMessageIdentifier(
        String messageIdentifier) {
        return ((root, query, cb) -> {
            if (!StringUtils.hasText(messageIdentifier)) {
                return null;
            }

            return cb.equal(root.get(MESSAGE_FIELD).get(IDENTIFIER_FIELD), messageIdentifier);
        });
    }

    private static Specification<ConnectorMessageAttachmentEntity> withTypes(
        List<ConnectorAttachmentType> types) {
        return ((root, query, cb) -> {
            if (types == null || types.isEmpty()) {
                return null;
            }

            return root.get(TYPE_FIELD).in(types);
        });
    }

    private static Specification<ConnectorMessageAttachmentEntity> withStorages(
        List<ConnectorAttachmentStorage> storages) {
        return ((root, query, cb) -> {
            if (storages == null || storages.isEmpty()) {
                return null;
            }

            return root.get(STORAGE_FIELD).in(storages);
        });
    }

    private static Specification<ConnectorMessageAttachmentEntity> withName(String name) {
        return ((root, query, cb) -> {
            if (!StringUtils.hasText(name)) {
                return null;
            }

            var pattern = "%" + name + "%";

            return cb.like(
                root.get(NAME_FIELD), pattern
            );
        });
    }
}
