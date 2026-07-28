package eu.ecodex.connector.application.service.iam.auth;

import eu.ecodex.connector.application.port.api.iam.user.ConnectorRetrieveUser;
import eu.ecodex.connector.domain.model.user.ConnectorUser;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Implementation of the {@link UserDetailsService} interface for loading user-specific data.
 * This class primarily uses the {@link ConnectorRetrieveUser} service to fetch user details
 * based on the provided username and adapt them to a format compatible with Spring Security.
 * <p>
 * Responsibilities:
 * - Retrieve user data from the {@link ConnectorRetrieveUser} service.
 * - Convert the retrieved {@link ConnectorUser} object into a {@link UserDetails} instance.
 * - Throw a {@link UsernameNotFoundException} if the user is not found.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ConnectorUserDetailsService implements UserDetailsService {

    ConnectorRetrieveUser retrieveUser;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        ConnectorUser connectorUser = retrieveUser.getByUsername(username);
        return new ConnectorUserDetails(connectorUser);
    }
}
