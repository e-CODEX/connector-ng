package eu.ecodex.connector.application.service.iam.role;

import eu.ecodex.connector.application.exception.ConnectorUserNotFoundException;
import eu.ecodex.connector.application.port.api.iam.role.ConnectorRemoveUserRole;
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
public class ConnectorRemoveUserRoleService implements ConnectorRemoveUserRole {

    ConnectorUserRoleRepository repository;

    @Override
    public void delete(ConnectorUserRole userRole) {
        repository.delete(userRole);
    }

    @Override
    public void deleteById(Long identifier) {
        if(repository.existsById(identifier)) {
            repository.deleteById(identifier);
        }else {
            throw new ConnectorUserNotFoundException("User not found by Id");
        }
    }
}
