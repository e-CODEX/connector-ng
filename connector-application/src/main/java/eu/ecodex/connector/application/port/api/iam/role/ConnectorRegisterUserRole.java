package eu.ecodex.connector.application.port.api.iam.role;

import eu.ecodex.connector.domain.model.user.ConnectorUserRole;

public interface ConnectorRegisterUserRole {
    ConnectorUserRole register(ConnectorUserRole userRole);

    ConnectorUserRole update(Long id, ConnectorUserRole userRole);
}
