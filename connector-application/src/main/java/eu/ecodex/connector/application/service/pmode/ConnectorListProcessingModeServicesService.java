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

import eu.ecodex.connector.application.port.api.pmode.ConnectorListProcessingModeServices;
import eu.ecodex.connector.application.port.spi.pmode.ConnectorServiceRepository;
import eu.ecodex.connector.domain.model.businessdomain.ConnectorBusinessDomainIdentifier;
import eu.ecodex.connector.domain.model.pmode.ConnectorService;
import java.util.List;
import lombok.NonNull;
import org.springframework.stereotype.Service;

/**
 * Implementation of the {@link ConnectorListProcessingModeServices} service.
 */
@Service
public class ConnectorListProcessingModeServicesService implements
    ConnectorListProcessingModeServices {
    private final ConnectorServiceRepository connectorServiceRepository;

    public ConnectorListProcessingModeServicesService(
        ConnectorServiceRepository connectorServiceRepository) {
        this.connectorServiceRepository = connectorServiceRepository;
    }

    @Override
    public List<ConnectorService> execute(@NonNull String businessDomainIdentifier) {
        var businessDomain = ConnectorBusinessDomainIdentifier.builder()
                                                              .messageLaneIdentifier(
                                                                  businessDomainIdentifier)
                                                              .build();
        return connectorServiceRepository.findAllByBusinessDomainIdentifier(businessDomain);
    }
}
