package eu.ecodex.connector.application.service.iam.user;

import eu.ecodex.connector.application.exception.ConnectorUserAlreadyExistsException;
import eu.ecodex.connector.application.exception.ConnectorUserBadRequestException;
import eu.ecodex.connector.application.exception.ConnectorUserNotFoundException;
import eu.ecodex.connector.application.port.api.iam.user.ConnectorRegisterUser;
import eu.ecodex.connector.application.port.spi.iam.user.ConnectorUserRepository;
import eu.ecodex.connector.domain.model.user.ConnectorUser;
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
public class ConnectorRegisterUserService implements ConnectorRegisterUser {

    ConnectorUserRepository repository;

    @Override
    public ConnectorUser register(ConnectorUser user) {
        if (user.identifier() != null) {
            throw new ConnectorUserBadRequestException("Connector user id should be blank");
        }
        checkEmail(null, user);
        checkUsername(null, user);
        return repository.save(user);
    }

    @Override
    public ConnectorUser update(Long id, ConnectorUser user) {
        var existingUser = getExistingUser(id, user);

        if (existingUser.hasSameContent(user)) {
            log.info("Nothing to update");
            return existingUser;
        }
        var userBuilder = existingUser.toBuilder();
        userBuilder.username(user.username());
        userBuilder.password(user.password());
        userBuilder.email(user.email());
        userBuilder.enabled(user.enabled());

        return repository.save(userBuilder.build());
    }

    @Override
    public ConnectorUser patch(Long id, ConnectorUser user) {
        var existingUser = getExistingUser(id, user);

        var userBuilder = existingUser.toBuilder();
        if (user.email() != null) {
            userBuilder.email(user.email());
        }

        if (user.username() != null) {
            userBuilder.username(user.username());
        }

        if (user.password() != null) {
            userBuilder.password(user.password());
        }

        if (user.enabled() != null) {
            userBuilder.enabled(user.enabled());
        }

        return repository.save(userBuilder.build());
    }

    private ConnectorUser getExistingUser(Long id, ConnectorUser user) {
        if (id == null && user.identifier() == null) {
            throw new ConnectorUserBadRequestException("Connector user id should not be blank");
        }

        var existingUser = repository.findById(id)
                .orElseThrow(() -> new ConnectorUserNotFoundException(
                        "No existing user found with id " + id));

        checkUsername(id, user);

        checkEmail(id, user);

        return existingUser;
    }

    private void checkUsername(Long id, ConnectorUser user) {
        var existingUser = repository.findByUsername(user.username());

        if (existingUser.isPresent() && !Objects.equals(existingUser.get().identifier(), id)) {
            throw new ConnectorUserAlreadyExistsException(
                    "Username '%s' already exists".formatted(user.username())
            );
        }
    }

    private void checkEmail(Long id, ConnectorUser user) {
        if (user.email() != null) {
            var existingUser = repository.findByEmail(user.username());

            if (existingUser.isPresent() && !Objects.equals(existingUser.get().identifier(), id)) {
                throw new ConnectorUserAlreadyExistsException(
                        "User email '%s' already exists".formatted(user.email())
                );
            }
        }
    }
}
