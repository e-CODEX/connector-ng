package eu.ecodex.connector.infrastructure.iam.user;

import eu.ecodex.connector.application.port.spi.iam.user.ConnectorUserRepository;
import eu.ecodex.connector.domain.model.user.ConnectorUser;
import eu.ecodex.connector.domain.model.user.ConnectorUserRole;
import eu.ecodex.connector.infrastructure.iam.role.ConnectorUserRoleEntity;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ConnectorUserRepositoryImpl implements ConnectorUserRepository {

    ConnectorUserJpaRepository jpaRepository;

    @Override
    public ConnectorUser save(ConnectorUser user) {
        var saved = jpaRepository.save(toEntity(user));
        return toDomain(saved);
    }

    @Override
    public Optional<ConnectorUser> findById(Long id) {
        var found = jpaRepository.findById(id);
        return found.map(this::toDomain);
    }

    @Override
    public Optional<ConnectorUser> findByUsername(String username) {
        var found = jpaRepository.findByUsername(username);
        return found.map(this::toDomain);
    }

    @Override
    public Optional<ConnectorUser> findByEmail(String email) {
        var found = jpaRepository.findByEmail(email);
        return found.map(this::toDomain);
    }

    @Override
    public Optional<ConnectorUser> findByUsernameAndEmail(String username, String email) {
        var found = jpaRepository.findByUsernameAndEmail(username, email);
        return found.map(this::toDomain);
    }

    @Override
    public List<ConnectorUser> findAll() {
        return jpaRepository.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public void deleteById(Long identifier) {
        jpaRepository.deleteById(identifier);
    }

    @Override
    public void delete(ConnectorUser user) {
        jpaRepository.delete(toEntity(user));
    }

    @Override
    public boolean existsById(Long identifier) {
        return jpaRepository.existsById(identifier);
    }

    private ConnectorUserEntity toEntity(ConnectorUser domainUser) {
        return new ConnectorUserEntity(domainUser.identifier(), domainUser.username(),
                domainUser.password(), domainUser.email(), domainUser.enabled(),
                domainUser.roles() == null ? null :
                        domainUser.roles().stream()
                                .map(domain -> ConnectorUserRoleEntity.builder()
                                        .id(domain.identifier())
                                        .name(domain.name())
                                        .build())
                                .collect(Collectors.toUnmodifiableSet()));
    }

    private ConnectorUser toDomain(ConnectorUserEntity entity) {
        return new ConnectorUser(entity.getId(), entity.getUsername(), entity.getPassword(),
                entity.getEmail(),
                entity.isEnabled(),
                entity.getRoles() == null ? null :
                        entity.getRoles().stream()
                                .map(role ->
                                        ConnectorUserRole.builder()
                                                .identifier(role.getId())
                                                .name(role.getName())
                                                .build())
                                .collect(Collectors.toUnmodifiableSet()),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }
}
