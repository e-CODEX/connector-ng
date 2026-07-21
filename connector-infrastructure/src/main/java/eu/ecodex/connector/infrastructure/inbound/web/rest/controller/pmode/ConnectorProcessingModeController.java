/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.inbound.web.rest.controller.pmode;

import eu.ecodex.connector.application.port.api.pmode.ConnectorListProcessingModeActions;
import eu.ecodex.connector.domain.model.pmode.ConnectorAction;
import java.util.List;
import org.springframework.web.bind.annotation.RestController;

/**
 * Defines the REST controller for managing processing modes within the connector system.
 */
@RestController
public class ConnectorProcessingModeController implements ConnectorProcessingModeApi {
    private final ConnectorListProcessingModeActions listProcessingModeActionsService;

    public ConnectorProcessingModeController(
        ConnectorListProcessingModeActions listProcessingModeActionsService) {
        this.listProcessingModeActionsService = listProcessingModeActionsService;
    }

    @Override
    public List<ConnectorAction> listProcessingModeActions(String identifier) {
        return this.listProcessingModeActionsService.execute(identifier);
    }
}
