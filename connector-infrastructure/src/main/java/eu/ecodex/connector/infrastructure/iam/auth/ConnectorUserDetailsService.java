package eu.ecodex.connector.infrastructure.iam.auth;

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
 * Responsible for retrieving user credentials from provider/database
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
