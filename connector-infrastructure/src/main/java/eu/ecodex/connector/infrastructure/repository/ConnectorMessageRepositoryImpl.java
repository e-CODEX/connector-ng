/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.repository;

import eu.ecodex.connector.domain.model.message.ConnectorMessage;
import eu.ecodex.connector.domain.model.message.ConnectorMessageDirection;
import eu.ecodex.connector.domain.model.message.evidence.ConnectorEvidence;
import eu.ecodex.connector.domain.spi.ConnectorMessageRepository;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * Default Implementation of the {@link ConnectorMessageRepository}.
 */
@Component
public class ConnectorMessageRepositoryImpl implements ConnectorMessageRepository {
    @Override
    public ConnectorMessage save(ConnectorMessage message) {
        throw new UnsupportedOperationException("not yet implemented");
    }

    @Override
    public ConnectorMessage findByIdentifier(String identifier) {
        throw new UnsupportedOperationException("not yet implemented");
    }

    @Override
    public ConnectorMessage findByIdentifierAndDirection(
            ConnectorMessage message,
            ConnectorMessageDirection direction) {
        throw new UnsupportedOperationException("not yet implemented");
    }

    @Override
    public List<ConnectorMessage> findByConversationIdentifier(String conversationIdentifier) {
        throw new UnsupportedOperationException("not yet implemented");
    }

    @Override
    public ConnectorMessage addEvidence(ConnectorMessage message, ConnectorEvidence evidence) {
        throw new UnsupportedOperationException("not yet implemented");
    }

    @Override
    public ConnectorMessage setAsRejected(ConnectorMessage message) {
        throw new UnsupportedOperationException("not yet implemented");
    }

    @Override
    public ConnectorMessage setAsConfirmed(ConnectorMessage message) {
        throw new UnsupportedOperationException("not yet implemented");
    }
}
