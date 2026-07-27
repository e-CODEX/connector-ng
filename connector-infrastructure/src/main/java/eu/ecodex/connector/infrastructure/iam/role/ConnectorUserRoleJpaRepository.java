package eu.ecodex.connector.infrastructure.iam.role;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConnectorUserRoleJpaRepository
        extends JpaRepository<ConnectorUserRoleEntity, Long> {
    Optional<ConnectorUserRoleEntity> findByName(String username);
}
