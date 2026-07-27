package eu.ecodex.connector.application.service.iam.role;

import eu.ecodex.connector.application.port.api.iam.role.ConnectorListUserRole;
import eu.ecodex.connector.application.port.spi.iam.role.ConnectorUserRoleRepository;
import eu.ecodex.connector.domain.model.user.ConnectorUserRole;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ConnectorListUserRoleService implements ConnectorListUserRole {

    ConnectorUserRoleRepository repository;

    @Override
    public List<ConnectorUserRole> findAll() {
        return repository.findAll();
    }
}
