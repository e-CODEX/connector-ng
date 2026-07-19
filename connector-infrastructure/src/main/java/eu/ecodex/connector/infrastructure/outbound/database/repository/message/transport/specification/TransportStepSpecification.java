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

import eu.ecodex.connector.infrastructure.outbound.database.entity.message.transport.ConnectorMessageTransportStepEntity;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

/**
 * Specification for {@link ConnectorMessageTransportStepEntity}.
 */
public class TransportStepSpecification {
    private static final String TRANSPORTED_MESSAGE_IDENTIFIER = "transportedMessageIdentifier";
    private static final String REMOTE_SYSTEM_IDENTIFIER = "remoteSystemIdentifier";
    private static final String LINK_PARTNER_NAME = "linkPartnerName";

    /**
     * Constructs a combined {@link Specification} for filtering
     * {@link ConnectorMessageTransportStepEntity} based on the provided identifier and backendName.
     * The specification combines conditions for the {@code transportedMessageIdentifier},
     * {@code remoteSystemIdentifier}, and {@code backendName} fields.
     *
     * @param messageOrRemoteSystemIdentifier the identifier used to filter by transported message
     *                                        or remote system identifier. If the value is null or
     *                                        empty, the corresponding filters are ignored.
     * @param linkPartnerName                 the name of the link partner used to filter by backend
     *                                        name.
     *
     * @return a {@link Specification} representing the combined filter criteria. Returns null if
     *     all the input parameters are null or empty.
     */
    public static Specification<ConnectorMessageTransportStepEntity> withFilters(
        String messageOrRemoteSystemIdentifier,
        String linkPartnerName) {
        return Specification
            .where(withTransportedMessageIdentifier(messageOrRemoteSystemIdentifier))
            .or(
                withRemoteSystemIdentifier(messageOrRemoteSystemIdentifier)
            )
            .and(withLinkPartnerName(linkPartnerName));
    }

    private static Specification<ConnectorMessageTransportStepEntity>
    withTransportedMessageIdentifier(String identifier) {
        return ((root, query, cb) -> {
            if (!StringUtils.hasText(identifier)) {
                return null;
            }

            var pattern = "%" + identifier + "%";

            return cb.like(root.get(TRANSPORTED_MESSAGE_IDENTIFIER), pattern);
        });
    }

    private static Specification<ConnectorMessageTransportStepEntity> withRemoteSystemIdentifier(
        String identifier) {
        return ((root, query, cb) -> {
            if (!StringUtils.hasText(identifier)) {
                return null;
            }

            var pattern = "%" + identifier + "%";

            return cb.like(root.get(REMOTE_SYSTEM_IDENTIFIER), pattern);
        });
    }

    private static Specification<ConnectorMessageTransportStepEntity> withLinkPartnerName(
        String linkPartnerName) {
        return ((root, query, cb) -> {
            if (!StringUtils.hasText(linkPartnerName)) {
                return null;
            }

            var pattern = "%" + linkPartnerName + "%";

            return cb.like(root.get(LINK_PARTNER_NAME), pattern);
        });
    }
}
