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
import eu.ecodex.connector.domain.exception.ConnectorActionNotFoundException;
import eu.ecodex.connector.domain.model.businessdomain.ConnectorBusinessDomainIdentifier;
import eu.ecodex.connector.domain.model.pmode.ConnectorAction;
import eu.ecodex.connector.domain.spi.pmode.ConnectorActionRepository;
import java.util.List;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

/**
 * Default implementation of the {@link ConnectorActionService}.
 *
 * <p>This service provides methods to query {@link ConnectorAction} entities
 * within a specific business domain using a defined repository.
 */
@Slf4j
@DomainService
public class ConnectorActionServiceImpl implements ConnectorActionService {
    private final ConnectorActionRepository actionRepository;

    public ConnectorActionServiceImpl(ConnectorActionRepository actionRepository) {
        this.actionRepository = actionRepository;
    }

    @Override
    public List<ConnectorAction> persistAll(
            @NonNull List<ConnectorAction> actions,
            @NonNull ConnectorBusinessDomainIdentifier businessDomainIdentifier) {
        log.debug(
                "Saving actions [{}] for business domain [{}]", actions, businessDomainIdentifier
        );

        return this.actionRepository.saveAll(actions, businessDomainIdentifier);
    }

    @Override
    public ConnectorAction findByNameAndBusinessDomain(
            String actionName,
            ConnectorBusinessDomainIdentifier businessDomainIdentifier) {
        var action = this.actionRepository.findByNameAndBusinessDomain(
                actionName, businessDomainIdentifier
        );

        if (action == null) {
            log.warn(
                    "Action with name [{}] and business domain [{}] not found",
                    actionName, businessDomainIdentifier
            );

            throw new ConnectorActionNotFoundException("Action not found");
        }

        return action;
    }
}
