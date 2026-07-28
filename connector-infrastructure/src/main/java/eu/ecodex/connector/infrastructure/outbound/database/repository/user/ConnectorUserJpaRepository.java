/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.outbound.database.repository.user;

import eu.ecodex.connector.infrastructure.outbound.database.entity.user.ConnectorUserEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConnectorUserJpaRepository extends JpaRepository<ConnectorUserEntity, Long> {

    Optional<ConnectorUserEntity> findByUuid(String uuid);

    Optional<ConnectorUserEntity> findByUsername(String username);

    Optional<ConnectorUserEntity> findByEmail(String email);

    Optional<ConnectorUserEntity> findByUsernameAndEmail(String username, String email);

    boolean existsByUuid(String uuid);
}
