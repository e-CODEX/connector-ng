package eu.ecodex.connector.infrastructure.iam.auth;

import eu.ecodex.connector.domain.model.user.ConnectorUser;
import java.util.Collection;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Slf4j
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ConnectorUserDetails implements UserDetails {

    ConnectorUser connectorUser;

    public ConnectorUserDetails(ConnectorUser connectorUser) {
        this.connectorUser = connectorUser;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return connectorUser.roles()
                .stream()
                .map(role -> new SimpleGrantedAuthority(role.name()))
                .toList();
    }

    @Override
    public @Nullable String getPassword() {
        return connectorUser.password();
    }

    @Override
    public String getUsername() {
        return connectorUser.username();
    }
}
