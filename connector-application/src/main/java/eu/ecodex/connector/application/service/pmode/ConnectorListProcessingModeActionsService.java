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

import eu.ecodex.connector.application.port.api.pmode.ConnectorListProcessingModeActions;
import eu.ecodex.connector.application.port.spi.pmode.ConnectorActionRepository;
import eu.ecodex.connector.domain.model.businessdomain.ConnectorBusinessDomainIdentifier;
import eu.ecodex.connector.domain.model.pmode.ConnectorAction;
import java.util.List;
import lombok.NonNull;
import org.springframework.stereotype.Service;

/**
 * Implementation of the {@link ConnectorListProcessingModeActions} service.
 */
@Service
public class ConnectorListProcessingModeActionsService
    implements ConnectorListProcessingModeActions {
    private final ConnectorActionRepository actionRepository;

    public ConnectorListProcessingModeActionsService(ConnectorActionRepository actionRepository) {
        this.actionRepository = actionRepository;
    }

    @Override
    public List<ConnectorAction> execute(@NonNull String businessDomainIdentifier) {
        return actionRepository.findAllByBusinessDomainIdentifier(
            ConnectorBusinessDomainIdentifier.builder()
                                             .messageLaneIdentifier(businessDomainIdentifier)
                                             .build()
        );
    }
}
