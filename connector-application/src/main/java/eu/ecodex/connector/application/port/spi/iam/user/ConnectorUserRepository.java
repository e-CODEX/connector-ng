package eu.ecodex.connector.application.port.spi.iam.user;

import eu.ecodex.connector.domain.model.user.ConnectorUser;
import java.util.List;
import java.util.Optional;

public interface ConnectorUserRepository {

    ConnectorUser save(ConnectorUser user);

    Optional<ConnectorUser> findById(Long id);

    Optional<ConnectorUser> findByUsername(String username);

    Optional<ConnectorUser> findByEmail(String email);

    Optional<ConnectorUser> findByUsernameAndEmail(String username, String email);

    List<ConnectorUser> findAll();

    void deleteById(Long identifier);

    void delete(ConnectorUser user);

    boolean existsById(Long identifier);
}
