/*
 * Copyright 2025 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.domain.service;

import eu.ecodex.connector.domain.annotation.DomainService;
import eu.ecodex.connector.domain.api.service.ConnectorActionService;
import eu.ecodex.connector.domain.api.service.ConnectorBusinessDomainService;
import eu.ecodex.connector.domain.api.service.ConnectorKeystoreService;
import eu.ecodex.connector.domain.api.service.ConnectorPartyService;
import eu.ecodex.connector.domain.api.service.ConnectorProcessingModeService;
import eu.ecodex.connector.domain.api.service.ConnectorServiceService;
import eu.ecodex.connector.domain.exception.ConnectorProcessingModeNotFoundException;
import eu.ecodex.connector.domain.exception.ConnectorProcessingModeVerificationException;
import eu.ecodex.connector.domain.exception.NotFoundException;
import eu.ecodex.connector.domain.model.ProcessingModeVerificationMode;
import eu.ecodex.connector.domain.model.message.ConnectorMessage;
import eu.ecodex.connector.domain.model.pmode.ConnectorProcessingMode;
import eu.ecodex.connector.domain.spi.ConnectorProcessingModeRepository;
import jakarta.annotation.Nonnull;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

/**
 * Default Implementation of the {@link ConnectorProcessingModeService}.
 */
// TODO to be removed
@Slf4j
@DomainService
public class ConnectorProcessingModeServiceImpl implements ConnectorProcessingModeService {
    private final ConnectorProcessingModeRepository processingModeRepository;
    private final ConnectorServiceService serviceService;
    private final ConnectorActionService actionService;
    private final ConnectorPartyService partyService;
    private final ConnectorKeystoreService keystoreService;

    /**
     * Constructs a new instance of {@code ConnectorProcessingModeServiceImpl} and initializes its
     * dependencies.
     *
     * @param serviceService  the service responsible for managing and retrieving service-related
     *                        data
     * @param actionService   the service responsible for managing and retrieving action-related
     *                        data
     * @param partyService    the service responsible for validating and managing party-related
     *                        data
     * @param keystoreService the service responsible for managing and retrieving keystore-related
     *                        data
     */
    public ConnectorProcessingModeServiceImpl(
            ConnectorProcessingModeRepository processingModeRepository,
            ConnectorBusinessDomainService businessDomainService,
            ConnectorServiceService serviceService,
            ConnectorActionService actionService,
            ConnectorPartyService partyService,
            ConnectorKeystoreService keystoreService) {
        this.processingModeRepository = processingModeRepository;
        this.serviceService = serviceService;
        this.actionService = actionService;
        this.partyService = partyService;
        this.keystoreService = keystoreService;
    }


    @Override
    public ConnectorProcessingMode updateKeystore(
            @Nonnull String uuid, @Nonnull String keystoreUuid) {
        log.debug("updating processing mode with uuid: [{}]", uuid);

        var existingProcessingMode = this.processingModeRepository.findByUuid(uuid);

        if (existingProcessingMode == null) {
            throw new ConnectorProcessingModeNotFoundException(
                    "not found processing mode for business domain");
        }

        return this.processingModeRepository.updateKeystore(uuid, keystoreUuid);
    }

    @Override
    public void checkMessage(
            @NonNull ConnectorMessage message,
            @NonNull ProcessingModeVerificationMode verificationMode) {
        log.debug(
                "verifying message [{}] with verification mode [{}]",
                message.identifier(), verificationMode
        );

        switch (verificationMode) {
            case STRICT -> this.processStrictVerification(message);
            case RELAXED -> this.processRelaxedVerification(message);
            default -> this.processCreateVerification(message);
        }
    }

    private void processStrictVerification(ConnectorMessage message) {
        this.processServiceAndActionVerification(message);
    }

    private void processRelaxedVerification(ConnectorMessage message) {
        this.processServiceAndActionVerification(message);

        var toParty = message.as4Properties().toParty();

        if (toParty != null && toParty.identifierType().isBlank()) {
            log.warn(
                    "message with uuid [{}] verification mode is RELAXED."
                    + "Assuming ToParty IdentifierType [{}] as empty!",
                    message.identifier(), toParty.identifierType()
            );
        }

        if (!this.partyService.exists(toParty, message.businessDomainIdentifier())) {
            throw new ConnectorProcessingModeVerificationException(
                    String.format(
                            "message toParty [%s] is not configured on the connector! "
                            + "Check the P-Mode linked to business domain with uuid [%s]",
                            toParty, message.businessDomainIdentifier()
                    )
            );
        }

        var fromParty = message.as4Properties().fromParty();

        if (fromParty != null && fromParty.identifierType().isBlank()) {
            log.warn(
                    "message with uuid [{}] verification mode is RELAXED."
                    + "Assuming FromParty IdentifierType [{}] as empty!",
                    message.identifier(), fromParty.identifierType()
            );
        }

        if (!this.partyService.exists(fromParty, message.businessDomainIdentifier())) {
            throw new ConnectorProcessingModeVerificationException(
                    String.format(
                            "message fromParty [%s] is not configured on the connector! "
                            + "Check the P-Mode linked to business domain with uuid [%s]",
                            fromParty, message.businessDomainIdentifier()
                    )
            );
        }
    }

    private void processCreateVerification(ConnectorMessage message) {
        var warning = String.format(
                "message with uuid [%s] verification failed because P-Mode CREATE "
                + "verification mode is not supported!",
                message.identifier()
        );
        log.warn(warning);
        throw new ConnectorProcessingModeVerificationException(warning);
    }

    private void processServiceAndActionVerification(ConnectorMessage message) {
        log.debug("verifying service and action for message [{}]", message.identifier());

        try {
            var as4Properties = message.as4Properties();

            this.serviceService.findByNameAndBusinessDomain(
                    as4Properties.service().name(),
                    message.businessDomainIdentifier()
            );

            this.actionService.findByNameAndBusinessDomain(
                    as4Properties.action().name(), message.businessDomainIdentifier()
            );
        } catch (NotFoundException e) {
            log.error("message with uuid [{}] verification failed", message.identifier());

            throw new ConnectorProcessingModeVerificationException(
                    "message verification failed", e
            );
        }
    }
}
