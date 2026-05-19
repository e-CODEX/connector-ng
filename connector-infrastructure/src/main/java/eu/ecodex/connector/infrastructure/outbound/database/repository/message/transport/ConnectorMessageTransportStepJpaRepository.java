/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.outbound.database.repository.message.transport;

import eu.ecodex.connector.infrastructure.outbound.database.entity.message.transport.ConnectorMessageTransportStepEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for performing CRUD operations on the
 * {@link ConnectorMessageTransportStepEntity}.
 */
@Repository
public interface ConnectorMessageTransportStepJpaRepository extends
        JpaRepository<ConnectorMessageTransportStepEntity, Long> {
    ConnectorMessageTransportStepEntity findByIdentifier(String identifier);

    ConnectorMessageTransportStepEntity findByMessageIdentifier(String messageIdentifier);
}
