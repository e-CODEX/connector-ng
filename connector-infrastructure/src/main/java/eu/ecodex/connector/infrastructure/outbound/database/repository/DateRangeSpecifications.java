/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.outbound.database.repository;

import eu.ecodex.connector.domain.model.filter.ConnectorDateRangeFilter;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;

/**
 * Utility class providing reusable JPA {@link Specification} factories for date range filtering.
 *
 * <p>This class is not meant to be instantiated.
 */
public final class DateRangeSpecifications {

    private DateRangeSpecifications() {
    }

    /**
     * Builds a {@link Specification} restricting results to entities whose date attribute falls
     * within the range defined by the given {@link ConnectorDateRangeFilter}.
     *
     * <p>Both bounds are inclusive: the lower bound is applied as {@code field >= from} and the
     * upper bound as {@code field <= to}. A {@code null} bound leaves the range open on that side.
     * If the filter is {@code null}, or if both of its bounds are {@code null}, the returned
     * specification adds no restriction (it yields a {@code null} predicate).
     *
     * @param filter the date range to apply; may be {@code null}
     * @param field  name of the {@link Instant} attribute of the entity to filter on (for example a
     *               creation date); must match an attribute of type {@code Instant} on the root
     *               entity
     * @param <T>    the type of the entity the specification applies to
     *
     * @return a {@link Specification} applying the date range to the given attribute
     *
     * @throws IllegalArgumentException when the specification is evaluated, if {@code field} is not
     *                                  an attribute of the root entity
     */
    public static <T> Specification<T> withDateRange(
        ConnectorDateRangeFilter filter, String field) {
        return (root, query, cb) -> {
            if (filter == null) {
                return null;
            }

            Path<Instant> path = root.get(field);
            List<Predicate> predicates = new ArrayList<>(2);

            if (filter.from() != null) {
                predicates.add(cb.greaterThanOrEqualTo(path, filter.from()));
            }
            if (filter.to() != null) {
                predicates.add(cb.lessThanOrEqualTo(path, filter.to()));
            }

            return predicates.isEmpty()
                ? null
                : cb.and(predicates.toArray(Predicate[]::new));
        };
    }
}
