/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.service.pmode;

import eu.ecodex.connector.application.exception.ConnectorBusinessDomainAlreadyExistsException;
import eu.ecodex.connector.application.exception.ConnectorBusinessDomainNotFoundException;
import eu.ecodex.connector.application.exception.ConnectorProcessingModeException;
import eu.ecodex.connector.application.port.api.pmode.ConnectorRegisterProcessingMode;
import eu.ecodex.connector.application.port.spi.ConnectorBusinessDomainRepository;
import eu.ecodex.connector.application.port.spi.pmode.ConnectorActionRepository;
import eu.ecodex.connector.application.port.spi.pmode.ConnectorPartyRepository;
import eu.ecodex.connector.application.port.spi.pmode.ConnectorProcessingModeRepository;
import eu.ecodex.connector.application.port.spi.pmode.ConnectorServiceRepository;
import eu.ecodex.connector.domain.model.businessdomain.ConnectorBusinessDomainIdentifier;
import eu.ecodex.connector.domain.model.pmode.ConnectorProcessingMode;
import eu.ecodex.connector.domain.spi.ConnectorProcessingModeParser;
import java.util.List;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of the {@link ConnectorRegisterProcessingMode} service.
 */
@Slf4j
@Service
@Transactional
public class ConnectorRegisterProcessingModeService implements ConnectorRegisterProcessingMode {
    private final ConnectorProcessingModeRepository processingModeRepository;
    private final ConnectorBusinessDomainRepository businessDomainRepository;
    private final ConnectorPartyRepository partyRepository;
    private final ConnectorActionRepository actionRepository;
    private final ConnectorServiceRepository serviceRepository;
    private final ConnectorProcessingModeParser processingModeParser;

    /**
     * Creates an instance of {@code ConnectorRegisterProcessingModeService}.
     *
     * @param processingModeRepository The repository used for managing
     *                                 {@link ConnectorProcessingMode} entities.
     * @param businessDomainRepository The repository used for managing
     *                                 {@code ConnectorBusinessDomain} entities.
     * @param partyRepository          The repository used for managing {@code ConnectorParty}
     *                                 entities.
     * @param actionRepository         The repository used for managing {@code ConnectorAction}
     *                                 entities.
     * @param serviceRepository        The repository used for managing {@code ConnectorService}
     *                                 entities.
     * @param processingModeParser     The parser extracting parties, services, and actions from the
     *                                 processing mode definition.
     */
    public ConnectorRegisterProcessingModeService(
        ConnectorProcessingModeRepository processingModeRepository,
        ConnectorBusinessDomainRepository businessDomainRepository,
        ConnectorPartyRepository partyRepository, ConnectorActionRepository actionRepository,
        ConnectorServiceRepository serviceRepository,
        ConnectorProcessingModeParser processingModeParser) {
        this.processingModeRepository = processingModeRepository;
        this.businessDomainRepository = businessDomainRepository;
        this.partyRepository = partyRepository;
        this.actionRepository = actionRepository;
        this.serviceRepository = serviceRepository;
        this.processingModeParser = processingModeParser;
    }

    @Override
    public ConnectorProcessingMode execute(
        @NonNull ConnectorBusinessDomainIdentifier businessDomainIdentifier,
        @NonNull ConnectorProcessingMode processingMode) {
        log.info(
            "Registering a new processing mode for the business domain [{}]: ",
            businessDomainIdentifier
        );

        var businessDomain = this.businessDomainRepository.findByIdentifier(
            businessDomainIdentifier
        );

        if (businessDomain == null) {
            throw new ConnectorBusinessDomainNotFoundException(
                "Business domain not found: %s".formatted(businessDomainIdentifier)
            );
        }

        var existingProcessingMode = this.processingModeRepository.findByBusinessDomainIdentifier(
            businessDomainIdentifier
        );

        if (existingProcessingMode != null) {
            throw new ConnectorBusinessDomainAlreadyExistsException(
                "The business domain [%s] already has a processing mode"
                    .formatted(businessDomainIdentifier)
            );
        }

        ConnectorProcessingModeParser.ParsedProcessingMode parsed;

        try {
            parsed = this.processingModeParser.parse(processingMode.content().getBytes());
        } catch (Exception e) {
            log.error("Error parsing processing mode", e);
            throw new ConnectorProcessingModeException(
                "Error parsing processing mode for business domain [%s]: %s"
                    .formatted(businessDomainIdentifier, e.getMessage())
            );
        }

        log.debug("Processing mode parsed successfully [{}]", parsed);

        var toPersist = processingMode
            .toBuilder()
            .businessDomain(businessDomain)
            .build();

        var created = this.processingModeRepository.save(toPersist, businessDomainIdentifier);

        log.info(
            "Processing mode [{}] registered for the business domain [{}]",
            created.uuid(), businessDomainIdentifier
        );

        this.partyRepository.saveAll(List.copyOf(parsed.parties()), businessDomainIdentifier);
        this.serviceRepository.saveAll(List.copyOf(parsed.services()), businessDomainIdentifier);
        this.actionRepository.saveAll(List.copyOf(parsed.actions()), businessDomainIdentifier);

        log.info(
            "With {} parties, {} services, {} actions",
            parsed.parties().size(), parsed.services().size(), parsed.actions().size()
        );

        return created;
    }
}
