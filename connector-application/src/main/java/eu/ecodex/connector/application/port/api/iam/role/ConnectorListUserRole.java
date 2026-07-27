package eu.ecodex.connector.application.port.api.iam.role;

import eu.ecodex.connector.domain.model.user.ConnectorUserRole;
import java.util.List;

public interface ConnectorListUserRole {
    List<ConnectorUserRole> findAll();
}
