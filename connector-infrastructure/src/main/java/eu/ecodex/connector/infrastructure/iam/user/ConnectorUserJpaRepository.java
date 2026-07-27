package eu.ecodex.connector.infrastructure.iam.user;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConnectorUserJpaRepository extends JpaRepository<ConnectorUserEntity, Long> {
    Optional<ConnectorUserEntity> findByUsername(String username);

    Optional<ConnectorUserEntity> findByEmail(String email);

    Optional<ConnectorUserEntity> findByUsernameAndEmail(String username, String email);
}
