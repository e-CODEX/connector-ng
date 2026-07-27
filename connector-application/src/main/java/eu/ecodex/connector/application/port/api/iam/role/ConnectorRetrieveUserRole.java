package eu.ecodex.connector.application.port.api.iam.role;

import eu.ecodex.connector.application.exception.NotFoundException;
import eu.ecodex.connector.domain.model.user.ConnectorUserRole;

public interface ConnectorRetrieveUserRole {
    ConnectorUserRole getById(Long identifier) throws NotFoundException;
    ConnectorUserRole getByName(String username) throws NotFoundException;
}
