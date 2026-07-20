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

import eu.ecodex.connector.infrastructure.outbound.database.entity.message.ConnectorMessageEntity;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

/**
 * Specification for {@link ConnectorMessageEntity}.
 */
public class MessageSpecification {
    private static final String IDENTIFIER_FIELD = "identifier";
    private static final String BACKEND_MESSAGE_IDENTIFIER_FIELD = "backendMessageIdentifier";
    private static final String REFERENCE_TO_BACKEND_MESSAGE_IDENTIFIER =
        "referenceToBackendMessageIdentifier";
    private static final String AS4_PROPERTIES_FIELD = "as4Properties";
    private static final String SERVICE_FIELD = "service";
    private static final String ACTION_FIELD = "action";
    private static final String NAME_FIELD = "name";
    private static final String CONVERSATION_IDENTIFIER_FIELD = "conversationIdentifier";
    private static final String EBMS_IDENTIFIER_FIELD = "ebmsMessageIdentifier";
    private static final String BACKEND_NAME_FIELD = "backendName";
    private static final String BUSINESS_DOMAIN_FIELD = "businessDomain";

    /**
     * Creates a {@link Specification} for querying {@link ConnectorMessageEntity} instances based
     * on the provided identifier. The method evaluates the identifier against multiple fields such
     * as the main identifier, backend message identifier, reference to backend message identifier,
     * conversation identifier, and EBMS identifier.
     *
     * @param identifier               the identifier value to search for. The search will be
     *                                 performed as a case-insensitive "LIKE" match across multiple
     *                                 entity fields. If the identifier is {@code null}, the
     *                                 specification will not add any criteria.
     * @param backendName              the name of the backend to filter the results.
     * @param businessDomainIdentifier the identifier of the business domain to filter the results.
     * @param serviceName              the name of the service to filter the results.
     * @param actionName               the name of the action to filter the results.
     *
     * @return a {@link Specification} that can be used for querying {@link ConnectorMessageEntity}
     *     instances matching the given identifier across one or more relevant fields.
     */
    public static Specification<ConnectorMessageEntity> withFilters(
        String identifier,
        String backendName,
        String businessDomainIdentifier,
        String serviceName,
        String actionName) {
        return Specification
            .where(withIdentifier(identifier))
            .or(
                withBackendMessageIdentifier(identifier)
                    .or(withRefToBackendMessageIdentifier(identifier))
                    .or(withConversationIdentifier(identifier))
                    .or(withEbmsIdentifier(identifier))
            )
            .and(withBackendName(backendName))
            .and(withBusinessDomain(businessDomainIdentifier))
            .and(withServiceName(serviceName))
            .and(withActionName(actionName));
    }

    private static Specification<ConnectorMessageEntity> withIdentifier(String identifier) {
        return ((root, query, cb) -> {
            if (!StringUtils.hasText(identifier)) {
                return null;
            }

            var pattern = "%" + identifier + "%";

            return cb.like(root.get(IDENTIFIER_FIELD), pattern);
        });
    }

    private static Specification<ConnectorMessageEntity> withBackendMessageIdentifier(
        String identifier) {
        return ((root, query, cb) -> {
            if (!StringUtils.hasText(identifier)) {
                return null;
            }

            var pattern = "%" + identifier + "%";

            return cb.like(root.get(BACKEND_MESSAGE_IDENTIFIER_FIELD), pattern);
        });
    }

    private static Specification<ConnectorMessageEntity> withRefToBackendMessageIdentifier(
        String identifier) {
        return ((root, query, cb) -> {
            if (!StringUtils.hasText(identifier)) {
                return null;
            }

            var pattern = "%" + identifier + "%";

            return cb.like(root.get(REFERENCE_TO_BACKEND_MESSAGE_IDENTIFIER), pattern);
        });
    }

    private static Specification<ConnectorMessageEntity> withConversationIdentifier(
        String identifier) {
        return ((root, query, cb) -> {
            if (!StringUtils.hasText(identifier)) {
                return null;
            }

            var pattern = "%" + identifier + "%";

            return cb.like(
                root.get(AS4_PROPERTIES_FIELD).get(CONVERSATION_IDENTIFIER_FIELD),
                pattern
            );
        });
    }

    private static Specification<ConnectorMessageEntity> withEbmsIdentifier(
        String identifier) {
        return ((root, query, cb) -> {
            if (!StringUtils.hasText(identifier)) {
                return null;
            }

            var pattern = "%" + identifier + "%";

            return cb.like(
                root.get(AS4_PROPERTIES_FIELD).get(EBMS_IDENTIFIER_FIELD),
                pattern
            );
        });
    }

    private static Specification<ConnectorMessageEntity> withBackendName(String backendName) {
        return ((root, query, cb) -> {
            if (!StringUtils.hasText(backendName)) {
                return null;
            }

            var pattern = "%" + backendName + "%";

            return cb.like(
                root.get(BACKEND_NAME_FIELD), pattern
            );
        });
    }

    private static Specification<ConnectorMessageEntity> withBusinessDomain(
        String businessDomainIdentifier) {
        return ((root, query, cb) -> {
            if (!StringUtils.hasText(businessDomainIdentifier)) {
                return null;
            }

            var pattern = "%" + businessDomainIdentifier + "%";

            return cb.like(
                root.get(BUSINESS_DOMAIN_FIELD).get(IDENTIFIER_FIELD),
                pattern
            );
        });
    }

    private static Specification<ConnectorMessageEntity> withServiceName(String serviceName) {
        return ((root, query, cb) -> {
            if (!StringUtils.hasText(serviceName)) {
                return null;
            }

            var pattern = "%" + serviceName + "%";

            return cb.like(
                root.get(AS4_PROPERTIES_FIELD).get(SERVICE_FIELD).get(NAME_FIELD),
                pattern
            );
        });
    }

    private static Specification<ConnectorMessageEntity> withActionName(String actionName) {
        return ((root, query, cb) -> {
            if (!StringUtils.hasText(actionName)) {
                return null;
            }

            var pattern = "%" + actionName + "%";

            return cb.like(
                root.get(AS4_PROPERTIES_FIELD).get(ACTION_FIELD).get(NAME_FIELD),
                pattern
            );
        });
    }
}
