package eu.ecodex.connector.application.service.iam.role;

import eu.ecodex.connector.application.exception.NotFoundException;
import eu.ecodex.connector.application.port.api.iam.role.ConnectorRetrieveUserRole;
import eu.ecodex.connector.application.port.spi.iam.role.ConnectorUserRoleRepository;
import eu.ecodex.connector.domain.model.user.ConnectorUserRole;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ConnectorRetrieveUserRoleService implements ConnectorRetrieveUserRole {

    ConnectorUserRoleRepository repository;

    @Override
    public ConnectorUserRole getById(Long identifier) throws NotFoundException {
        return repository.findById(identifier).orElseThrow(() -> new NotFoundException(
                String.format("User not found by identifier %s", identifier)));
    }

    @Override
    public ConnectorUserRole getByName(String name) throws NotFoundException {
        return repository.findByName(name).orElseThrow(() -> new NotFoundException(
                String.format("User not found by username %s", name)));
    }

}
