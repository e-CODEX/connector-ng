/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.port.api.auth.role;

import eu.ecodex.connector.application.exception.ConnectorUserNotFoundException;
import eu.ecodex.connector.application.exception.ConnectorUserRoleNotFoundException;
import eu.ecodex.connector.domain.model.user.ConnectorUser;
import eu.ecodex.connector.domain.model.user.ConnectorUserRole;

/**
 * Defines a contract for registering and updating user roles within the Connector system.
 * Implementations of this interface handle the creation of new {@link ConnectorUserRole}
 * instances and the modification of existing ones.
 * <p>
 * The primary purpose of this interface is to manage user roles in the underlying
 * persistence mechanism, ensuring that each role is correctly registered or updated
 * based on the provided data.
 */
public interface ConnectorRegisterRoleAssignment {

    ConnectorUser register(String identifier, String role)
            throws ConnectorUserRoleNotFoundException,
            ConnectorUserNotFoundException;

    ConnectorUser remove(String identifier, String role)
            throws ConnectorUserRoleNotFoundException, ConnectorUserNotFoundException;
}
