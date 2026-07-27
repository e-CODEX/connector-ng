package eu.ecodex.connector.application.port.api.iam.user;

import eu.ecodex.connector.domain.model.user.ConnectorUser;

public interface ConnectorRemoveUser {
    void delete(ConnectorUser user);
    void deleteById(Long user);
}
