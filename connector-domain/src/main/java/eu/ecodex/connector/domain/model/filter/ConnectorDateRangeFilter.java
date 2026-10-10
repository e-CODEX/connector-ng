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

import java.time.Instant;

/**
 * Represents a date range filter used to restrict query results to a time window.
 *
 * <p>Both bounds are optional: a {@code null} bound means the range is open on that side. When
 * both bounds are provided, {@code from} is expected to be before or equal to {@code to}.
 *
 * @param from the lower bound of the range (start of the window); no lower limit if {@code null}
 * @param to   the upper bound of the range (end of the window); no upper limit if {@code null}
 */
public record ConnectorDateRangeFilter(
    Instant from,
    Instant to
) {
    public static ConnectorDateRangeFilter of(Instant from, Instant to) {
        return new ConnectorDateRangeFilter(from, to);
    }
}
