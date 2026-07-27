package eu.ecodex.connector.infrastructure.iam.role;

import eu.ecodex.connector.application.port.spi.iam.role.ConnectorUserRoleRepository;
import eu.ecodex.connector.domain.model.user.ConnectorUserRole;
import java.util.List;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ConnectorUserRoleRepositoryImpl implements ConnectorUserRoleRepository {

    ConnectorUserRoleJpaRepository jpaRepository;

    @Override
    public ConnectorUserRole save(ConnectorUserRole userRole) {
        var saved = jpaRepository.save(toEntity(userRole));
        return toDomain(saved);
    }

    @Override
    public Optional<ConnectorUserRole> findById(Long id) {
        var found = jpaRepository.findById(id);
        return found.map(this::toDomain);
    }

    @Override
    public Optional<ConnectorUserRole> findByName(String name) {
        var found = jpaRepository.findByName(name);
        return found.map(this::toDomain);
    }

    @Override
    public List<ConnectorUserRole> findAll() {
        return jpaRepository.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public void deleteById(Long identifier) {
        jpaRepository.deleteById(identifier);
    }

    @Override
    public void delete(ConnectorUserRole userRole) {
        jpaRepository.delete(toEntity(userRole));
    }

    @Override
    public boolean existsById(Long identifier) {
        return jpaRepository.existsById(identifier);
    }

    private ConnectorUserRoleEntity toEntity(ConnectorUserRole domainUserRole) {
        return ConnectorUserRoleEntity.builder()
                .id(domainUserRole.identifier())
                .name(domainUserRole.name())
                .build();
    }

    private ConnectorUserRole toDomain(ConnectorUserRoleEntity entity) {
        return new ConnectorUserRole(entity.getId(), entity.getName(), entity.getCreatedAt(),
                entity.getUpdatedAt());
    }
}
