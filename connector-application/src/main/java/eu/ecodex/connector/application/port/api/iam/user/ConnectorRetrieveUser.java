package eu.ecodex.connector.application.port.api.iam.user;

import eu.ecodex.connector.application.exception.NotFoundException;
import eu.ecodex.connector.domain.model.user.ConnectorUser;

public interface ConnectorRetrieveUser {
    ConnectorUser getById(Long identifier) throws NotFoundException;
    ConnectorUser getByUsername(String username) throws NotFoundException;
    ConnectorUser getByEmail(String email) throws NotFoundException;
    ConnectorUser getByUsernameAndEmail(String username, String email) throws NotFoundException;
}
