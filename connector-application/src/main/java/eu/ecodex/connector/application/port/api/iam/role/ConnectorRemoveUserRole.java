package eu.ecodex.connector.application.port.api.iam.role;

import eu.ecodex.connector.domain.model.user.ConnectorUserRole;

public interface ConnectorRemoveUserRole {
    void delete(ConnectorUserRole userRole);

    void deleteById(Long user);
}
