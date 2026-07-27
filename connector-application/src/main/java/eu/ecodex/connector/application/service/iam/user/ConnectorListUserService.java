package eu.ecodex.connector.application.service.iam.user;

import eu.ecodex.connector.application.port.api.iam.user.ConnectorListUser;
import eu.ecodex.connector.application.port.spi.iam.user.ConnectorUserRepository;
import eu.ecodex.connector.domain.model.user.ConnectorUser;
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
public class ConnectorListUserService implements ConnectorListUser {

    ConnectorUserRepository repository;

    @Override
    public List<ConnectorUser> findAll() {
        return repository.findAll();
    }
}
