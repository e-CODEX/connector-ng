package eu.ecodex.connector.application.port.spi.iam.role;

import eu.ecodex.connector.domain.model.user.ConnectorUserRole;
import java.util.List;
import java.util.Optional;

public interface ConnectorUserRoleRepository {

    ConnectorUserRole save(ConnectorUserRole user);

    Optional<ConnectorUserRole> findById(Long id);

    Optional<ConnectorUserRole> findByName(String name);

    List<ConnectorUserRole> findAll();

    void deleteById(Long identifier);

    void delete(ConnectorUserRole userRole);

    boolean existsById(Long identifier);
}
