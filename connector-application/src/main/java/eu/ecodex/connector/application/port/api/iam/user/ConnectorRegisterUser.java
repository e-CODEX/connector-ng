package eu.ecodex.connector.application.port.api.iam.user;

import eu.ecodex.connector.domain.model.user.ConnectorUser;

public interface ConnectorRegisterUser {
    ConnectorUser register(ConnectorUser user);
    ConnectorUser update(Long id, ConnectorUser user);
    ConnectorUser patch(Long id, ConnectorUser user);
}
