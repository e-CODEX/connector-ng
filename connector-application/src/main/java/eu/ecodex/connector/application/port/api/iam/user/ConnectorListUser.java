package eu.ecodex.connector.application.port.api.iam.user;

import eu.ecodex.connector.domain.model.user.ConnectorUser;
import java.util.List;

public interface ConnectorListUser {
    List<ConnectorUser> findAll();
}
