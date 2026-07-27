package eu.ecodex.connector.application.service.iam.user;

import eu.ecodex.connector.application.exception.ConnectorUserNotFoundException;
import eu.ecodex.connector.application.port.api.iam.user.ConnectorRemoveUser;
import eu.ecodex.connector.application.port.spi.iam.user.ConnectorUserRepository;
import eu.ecodex.connector.domain.model.user.ConnectorUser;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ConnectorRemoveUserService implements ConnectorRemoveUser {

    ConnectorUserRepository repository;

    @Override
    public void delete(ConnectorUser user) {
        repository.delete(user);
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
