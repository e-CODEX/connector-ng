package eu.ecodex.connector.application.service.iam.user;

import eu.ecodex.connector.application.exception.NotFoundException;
import eu.ecodex.connector.application.port.api.iam.user.ConnectorRetrieveUser;
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
public class ConnectorRetrieveUserService implements ConnectorRetrieveUser {

    ConnectorUserRepository repository;

    @Override
    public ConnectorUser getById(Long identifier) throws NotFoundException {
        return repository.findById(identifier).orElseThrow(() -> new NotFoundException(
                String.format("User not found by identifier %s", identifier)));
    }

    @Override
    public ConnectorUser getByUsername(String username) throws NotFoundException {
        return repository.findByUsername(username).orElseThrow(() -> new NotFoundException(
                String.format("User not found by username %s", username)));
    }

    @Override
    public ConnectorUser getByEmail(String email) throws NotFoundException {
        return repository.findByEmail(email).orElseThrow(() -> new NotFoundException(
                String.format("User not found by email %s", email)));
    }

    @Override
    public ConnectorUser getByUsernameAndEmail(String username, String email)
            throws NotFoundException {
        return repository.findByUsernameAndEmail(username, email)
                .orElseThrow(() -> new NotFoundException(
                        String.format("User not found by username %s and email %s", username,
                                email)));
    }
}
