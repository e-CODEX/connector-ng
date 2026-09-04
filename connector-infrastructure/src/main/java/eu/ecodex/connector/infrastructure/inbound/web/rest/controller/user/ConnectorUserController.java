/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.inbound.web.rest.controller.user;

import static eu.ecodex.connector.infrastructure.inbound.web.rest.request.user.ConnectorUserRequest.toDomain;

import eu.ecodex.connector.application.port.api.auth.user.ConnectorRegisterUser;
import eu.ecodex.connector.application.port.api.auth.user.ConnectorRetrieveUser;
import eu.ecodex.connector.infrastructure.inbound.web.rest.dto.user.ConnectorUserDto;
import eu.ecodex.connector.infrastructure.inbound.web.rest.request.user.ConnectorUserRequest;
import eu.ecodex.connector.infrastructure.outbound.auth.login.ConnectorUserDetails;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for managing connector users. Provides APIs for operations such as registration,
 * updating, partial updates, retrieval, listing, and deletion of users.
 *
 * <p>This controller relies on service classes for handling user-related operations and
 * ensures additional processing like password encoding before delegation.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ConnectorUserController implements ConnectorUserApi {
    ConnectorRegisterUser connectorRegisterUser;
    ConnectorRetrieveUser connectorRetrieveUser;

    @Override
    public ConnectorUserDto patch(ConnectorUserDetails userDetails,
                                  ConnectorUserRequest userRequest) {
        log.info("Patching existing user");
        var registered =
            connectorRegisterUser.patch(userDetails.getUserId(), toDomain(userRequest));
        log.info("User patched");
        return ConnectorUserDto.from(registered);
    }

    @Override
    public ConnectorUserDto getByIdentifier(ConnectorUserDetails userDetails) {
        var found = connectorRetrieveUser.getByIdentifier(userDetails.getUserId());
        return ConnectorUserDto.from(found);
    }

}
