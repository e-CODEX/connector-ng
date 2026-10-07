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
import eu.ecodex.connector.infrastructure.inbound.web.rest.dto.pmode.ConnectorProcessingModeActionDto;
import eu.ecodex.connector.infrastructure.inbound.web.rest.dto.pmode.ConnectorProcessingModeServiceDto;
import eu.ecodex.connector.infrastructure.property.c2ctest.Connector2ConnectorTestMessageProperties;
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
    private final Connector2ConnectorTestMessageProperties testMessageProperties;

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
     * @param testMessageProperties             the property for connector2Connector test message
     */
    public ConnectorProcessingModeController(
        ConnectorListProcessingModeServices listProcessingModeServicesService,
        ConnectorListProcessingModeActions listProcessingModeActionsService,
        ConnectorListProcessingModeParties listProcessingModePartiesService,
        Connector2ConnectorTestMessageProperties testMessageProperties) {
        this.listProcessingModeServicesService = listProcessingModeServicesService;
        this.listProcessingModeActionsService = listProcessingModeActionsService;
        this.listProcessingModePartiesService = listProcessingModePartiesService;
        this.testMessageProperties = testMessageProperties;
    }

    @Override
    public List<ConnectorProcessingModeServiceDto> listProcessingModeServices(
        String businessDomainIdentifier) {
        return listProcessingModeServicesService
            .execute(businessDomainIdentifier)
            .stream()
            .map(service -> ConnectorProcessingModeServiceDto.from(
                service,
                testMessageProperties.getService().getName(),
                testMessageProperties.getService().getType()
            ))
            .toList();
    }

    @Override
    public List<ConnectorProcessingModeActionDto> listProcessingModeActions(
        String businessDomainIdentifier) {
        return this.listProcessingModeActionsService
            .execute(businessDomainIdentifier)
            .stream()
            .map(action -> ConnectorProcessingModeActionDto.from(
                action, testMessageProperties.getAction()
            ))
            .toList();
    }

    @Override
    public List<ConnectorParty> listProcessingModeParties(String businessDomainIdentifier) {
        return listProcessingModePartiesService.execute(businessDomainIdentifier);
    }
}
