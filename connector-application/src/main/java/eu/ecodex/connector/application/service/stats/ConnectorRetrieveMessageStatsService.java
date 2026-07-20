/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.service.stats;

import eu.ecodex.connector.application.port.api.stats.ConnectorRetrieveMessageStats;
import eu.ecodex.connector.application.port.spi.ConnectorMessageStatsRepository;
import eu.ecodex.connector.domain.model.stats.ConnectorMessageStats;
import java.time.Instant;
import org.springframework.stereotype.Service;

/**
 * Implementation of the {@link ConnectorRetrieveMessageStats} service.
 */
@Service
public class ConnectorRetrieveMessageStatsService implements ConnectorRetrieveMessageStats {
    private final ConnectorMessageStatsRepository statsRepository;

    public ConnectorRetrieveMessageStatsService(ConnectorMessageStatsRepository statsRepository) {
        this.statsRepository = statsRepository;
    }

    private static Instant parseInstant(String value) {
        return (value == null || value.isBlank()) ? null : Instant.parse(value);
    }

    @Override
    public ConnectorMessageStats execute(String from, String to, String businessDomain) {
        return statsRepository.findAll(parseInstant(from), parseInstant(to), businessDomain);
    }
}
