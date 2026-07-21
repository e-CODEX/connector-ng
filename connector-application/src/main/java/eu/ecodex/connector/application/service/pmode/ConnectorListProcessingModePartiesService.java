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

import eu.ecodex.connector.application.port.api.pmode.ConnectorListProcessingModeParties;
import eu.ecodex.connector.application.port.spi.pmode.ConnectorPartyRepository;
import eu.ecodex.connector.domain.model.businessdomain.ConnectorBusinessDomainIdentifier;
import eu.ecodex.connector.domain.model.pmode.ConnectorParty;
import java.util.List;
import lombok.NonNull;
import org.springframework.stereotype.Service;

/**
 * Implementation of the {@link ConnectorListProcessingModeParties} service.
 */
@Service
public class ConnectorListProcessingModePartiesService
    implements ConnectorListProcessingModeParties {
    private final ConnectorPartyRepository partyRepository;

    public ConnectorListProcessingModePartiesService(ConnectorPartyRepository partyRepository) {
        this.partyRepository = partyRepository;
    }

    @Override
    public List<ConnectorParty> execute(@NonNull String businessDomainIdentifier) {
        return partyRepository.findAllByBusinessDomainIdentifier(
            ConnectorBusinessDomainIdentifier.builder()
                                             .messageLaneIdentifier(businessDomainIdentifier)
                                             .build()
        );
    }
}
