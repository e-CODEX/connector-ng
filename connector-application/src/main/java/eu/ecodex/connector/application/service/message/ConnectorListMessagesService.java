/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.service.message;

import eu.ecodex.connector.application.port.api.message.ConnectorListMessages;
import eu.ecodex.connector.application.port.spi.message.ConnectorMessageRepository;
import eu.ecodex.connector.domain.model.message.ConnectorMessage;
import eu.ecodex.connector.domain.model.paging.ConnectorPageRequest;
import eu.ecodex.connector.domain.model.paging.ConnectorPageResult;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

/**
 * Implementation of the {@link ConnectorListMessages} service.
 */
@Service
public class ConnectorListMessagesService implements ConnectorListMessages {
    private final ConnectorMessageRepository connectorMessageRepository;

    public ConnectorListMessagesService(ConnectorMessageRepository connectorMessageRepository) {
        this.connectorMessageRepository = connectorMessageRepository;
    }

    @Override
    public ConnectorPageResult<ConnectorMessage> execute(
        @NonNull ConnectorPageRequest pageRequest,
        String identifier,
        String backendName,
        String businessDomainIdentifier,
        String service,
        String action) {
        return connectorMessageRepository.findAll(
            pageRequest,
            identifier,
            backendName,
            businessDomainIdentifier,
            service,
            action
        );
    }
}
