/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.outbound.persistence.stats;

import eu.ecodex.connector.application.port.spi.ConnectorMessageStatsRepository;
import eu.ecodex.connector.domain.model.stats.ConnectorMessageStats;
import eu.ecodex.connector.domain.model.stats.ConnectorMessageStatsItem;
import eu.ecodex.connector.domain.model.stats.report.ConnectorMessageReport;
import eu.ecodex.connector.infrastructure.outbound.database.repository.stats.ConnectorMessageStatsJpaRepository;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * Default implementation of the {@link ConnectorMessageStatsRepository} interface.
 */
@Component
public class ConnectorMessageStatsRepositoryImpl implements ConnectorMessageStatsRepository {
    private final ConnectorMessageStatsJpaRepository messageStatsJpaRepository;

    public ConnectorMessageStatsRepositoryImpl(
        ConnectorMessageStatsJpaRepository messageStatsJpaRepository) {
        this.messageStatsJpaRepository = messageStatsJpaRepository;
    }

    @Override
    public ConnectorMessageStats findAll(Instant from, Instant to) {
        var stats = messageStatsJpaRepository.computeStats(from, to);

        if (stats == null) {
            return ConnectorMessageStats.ofZero();
        }

        var all = ConnectorMessageStatsItem
            .builder()
            .total(stats.total())
            .delivered(stats.delivered())
            .rejected(stats.rejected())
            .pending(stats.total() - stats.delivered() - stats.rejected())
            .build();
        var outbound = ConnectorMessageStatsItem
            .builder()
            .total(stats.backendToGateway())
            .delivered(stats.backendToGatewayDelivered())
            .rejected(stats.backendToGatewayRejected())
            .pending(
                stats.backendToGateway()
                    - stats.backendToGatewayDelivered()
                    - stats.backendToGatewayRejected())
            .build();
        var inbound = ConnectorMessageStatsItem
            .builder()
            .total(stats.gatewayToBackend())
            .delivered(stats.gatewayToBackendDelivered())
            .rejected(stats.gatewayToBackendRejected())
            .pending(
                stats.gatewayToBackend()
                    - stats.gatewayToBackendDelivered()
                    - stats.gatewayToBackendRejected())
            .build();

        return ConnectorMessageStats.builder()
                                    .all(all)
                                    .outbound(outbound)
                                    .inbound(inbound)
                                    .build();
    }

    @Override
    public List<ConnectorMessageReport> computeReport(Instant from, Instant to) {
        var reports = messageStatsJpaRepository.computeReports(from, to);
        return reports.stream().map((report) -> new ConnectorMessageReport(
            report.year(),
            report.month(),
            report.party(),
            report.service(),
            report.direction(),
            report.total()
        )).toList();
    }
}
