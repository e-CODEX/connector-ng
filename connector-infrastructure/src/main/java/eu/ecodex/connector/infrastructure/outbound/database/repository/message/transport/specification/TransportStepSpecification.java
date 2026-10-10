/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.outbound.database.repository.message.transport.specification;

import eu.ecodex.connector.domain.model.filter.ConnectorTransportStepsListFilter;
import eu.ecodex.connector.domain.model.message.transport.ConnectorMessageTransportStatus;
import eu.ecodex.connector.infrastructure.outbound.database.entity.message.transport.ConnectorMessageTransportStepEntity;
import eu.ecodex.connector.infrastructure.outbound.database.repository.DateRangeSpecifications;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

/**
 * Specification for {@link ConnectorMessageTransportStepEntity}.
 */
public class TransportStepSpecification {
    private static final String TRANSPORTED_MESSAGE_IDENTIFIER_FIELD
        = "transportedMessageIdentifier";
    private static final String REMOTE_SYSTEM_IDENTIFIER_FIELD = "remoteSystemIdentifier";
    private static final String LINK_PARTNER_NAME_FIELD = "linkPartnerName";
    private static final String STATUS_FIELD = "status";
    private static final String CREATED_FIELD = "createdAt";

    /**
     * Constructs a combined {@link Specification} for filtering
     * {@link ConnectorMessageTransportStepEntity} based on the provided identifier and backendName.
     * The specification combines conditions for the {@code transportedMessageIdentifier},
     * {@code remoteSystemIdentifier}, and {@code backendName} fields.
     *
     * @param filter the pagination parameters and filter criteria used to retrieve the transport
     *               steps (must not be null)
     *
     * @return a {@link Specification} representing the combined filter criteria. Returns null if
     *     all the input parameters are null or empty.
     */
    public static Specification<ConnectorMessageTransportStepEntity> withFilters(
        ConnectorTransportStepsListFilter filter) {
        return Specification
            .where(withTransportedMessageIdentifier(filter.messageOrRemoteSystemIdentifier()))
            .or(
                withRemoteSystemIdentifier(filter.messageOrRemoteSystemIdentifier())
            )
            .and(withLinkPartnerName(filter.linkPartnerName()))
            .and(withStatuses(filter.statuses()))
            .and(DateRangeSpecifications.withDateRange(filter.dateFilter(), CREATED_FIELD));
    }

    private static Specification<ConnectorMessageTransportStepEntity>
    withTransportedMessageIdentifier(String identifier) {
        return ((root, query, cb) -> {
            if (!StringUtils.hasText(identifier)) {
                return null;
            }

            var pattern = "%" + identifier + "%";

            return cb.like(root.get(TRANSPORTED_MESSAGE_IDENTIFIER_FIELD), pattern);
        });
    }

    private static Specification<ConnectorMessageTransportStepEntity> withRemoteSystemIdentifier(
        String identifier) {
        return ((root, query, cb) -> {
            if (!StringUtils.hasText(identifier)) {
                return null;
            }

            var pattern = "%" + identifier + "%";

            return cb.like(root.get(REMOTE_SYSTEM_IDENTIFIER_FIELD), pattern);
        });
    }

    private static Specification<ConnectorMessageTransportStepEntity> withLinkPartnerName(
        String linkPartnerName) {
        return ((root, query, cb) -> {
            if (!StringUtils.hasText(linkPartnerName)) {
                return null;
            }

            var pattern = "%" + linkPartnerName + "%";

            return cb.like(root.get(LINK_PARTNER_NAME_FIELD), pattern);
        });
    }

    private static Specification<ConnectorMessageTransportStepEntity> withStatuses(
        List<ConnectorMessageTransportStatus> statuses) {
        return ((root, query, cb) -> {
            if (statuses == null || statuses.isEmpty()) {
                return null;
            }

            return root.get(STATUS_FIELD).in(statuses);
        });
    }
}
