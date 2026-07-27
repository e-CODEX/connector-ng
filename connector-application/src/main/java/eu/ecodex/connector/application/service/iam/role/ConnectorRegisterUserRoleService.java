package eu.ecodex.connector.application.service.iam.role;

import eu.ecodex.connector.application.exception.ConnectorUserAlreadyExistsException;
import eu.ecodex.connector.application.exception.ConnectorUserBadRequestException;
import eu.ecodex.connector.application.exception.ConnectorUserNotFoundException;
import eu.ecodex.connector.application.port.api.iam.role.ConnectorRegisterUserRole;
import eu.ecodex.connector.application.port.spi.iam.role.ConnectorUserRoleRepository;
import eu.ecodex.connector.domain.model.user.ConnectorUserRole;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ConnectorRegisterUserRoleService implements ConnectorRegisterUserRole {

    ConnectorUserRoleRepository repository;

    @Override
    public ConnectorUserRole register(ConnectorUserRole userRole) {
        if (userRole.identifier() != null) {
            throw new ConnectorUserBadRequestException("Connector user role id should be blank");
        }
        return repository.save(userRole);
    }

    @Override
    public ConnectorUserRole update(Long id, ConnectorUserRole userRole) {
        if (id == null && userRole.identifier() == null) {
            throw new ConnectorUserBadRequestException(
                    "Connector user role id should not be blank");
        }

        var existingUserRole = repository.findById(id)
                .orElseThrow(() -> new ConnectorUserNotFoundException(
                        "No existing user role found with id " + id));

        checkRoleName(id, userRole);

        if (existingUserRole.name().equalsIgnoreCase(userRole.name())) {
            log.info("Nothing to update");
            return existingUserRole;
        }
        var userBuilder = existingUserRole.toBuilder();
        userBuilder.name(userRole.name());

        return repository.save(userBuilder.build());
    }

    private void checkRoleName(Long id, ConnectorUserRole userRole) {
        var existingUser = repository.findByName(userRole.name());

        if (existingUser.isPresent() && !Objects.equals(existingUser.get().identifier(), id)) {
            throw new ConnectorUserAlreadyExistsException(
                    "Role name '%s' already exists".formatted(userRole.name())
            );
        }

    }

}
