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
import eu.ecodex.connector.application.port.api.pmode.ConnectorListProcessingModeParties;
import eu.ecodex.connector.application.port.api.pmode.ConnectorListProcessingModeServices;
import eu.ecodex.connector.domain.model.pmode.ConnectorAction;
import eu.ecodex.connector.domain.model.pmode.ConnectorParty;
import eu.ecodex.connector.domain.model.pmode.ConnectorService;
import java.util.List;
import org.springframework.web.bind.annotation.RestController;

/**
 * Defines the REST controller for managing processing modes within the connector system.
 */
@RestController
public class ConnectorProcessingModeController implements ConnectorProcessingModeApi {
    private final ConnectorListProcessingModeServices listProcessingModeServicesService;
    private final ConnectorListProcessingModeActions listProcessingModeActionsService;
    private final ConnectorListProcessingModeParties listProcessingModePartiesService;

    /**
     * Constructs an instance of the {@code ConnectorProcessingModeController}, which is responsible
     * for managing processing modes within the connector system.
     *
     * @param listProcessingModeServicesService the service handling operations related to listing
     *                                          {@link ConnectorService} entities in the processing
     *                                          mode.
     * @param listProcessingModeActionsService  the service handling operations related to listing
     *                                          {@link ConnectorAction} entities in the processing
     *                                          mode.
     * @param listProcessingModePartiesService  the service handling operations related to listing
     *                                          {@link ConnectorParty} entities in the processing
     *                                          mode.
     */
    public ConnectorProcessingModeController(
        ConnectorListProcessingModeServices listProcessingModeServicesService,
        ConnectorListProcessingModeActions listProcessingModeActionsService,
        ConnectorListProcessingModeParties listProcessingModePartiesService) {
        this.listProcessingModeServicesService = listProcessingModeServicesService;
        this.listProcessingModeActionsService = listProcessingModeActionsService;
        this.listProcessingModePartiesService = listProcessingModePartiesService;
    }

    @Override
    public List<ConnectorService> listProcessingModeServices(String identifier) {
        return listProcessingModeServicesService.execute(identifier);
    }

    @Override
    public List<ConnectorAction> listProcessingModeActions(String identifier) {
        return this.listProcessingModeActionsService.execute(identifier);
    }

    @Override
    public List<ConnectorParty> listProcessingModeParties(String identifier) {
        return listProcessingModePartiesService.execute(identifier);
    }
}
