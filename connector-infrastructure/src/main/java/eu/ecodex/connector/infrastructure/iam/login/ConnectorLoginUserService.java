package eu.ecodex.connector.infrastructure.iam.login;

import eu.ecodex.connector.application.port.api.login.ConnectorLoginUser;
import eu.ecodex.connector.domain.model.login.LoginResponse;
import eu.ecodex.connector.infrastructure.iam.auth.JwtService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ConnectorLoginUserService implements ConnectorLoginUser {

    AuthenticationManager authenticationManager;
    JwtService jwtService;


    @Override
    public LoginResponse login(String username, String password) {

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                username,
                                password
                        )
                );

        UserDetails user = (UserDetails) authentication.getPrincipal();
        if (user == null) {
            throw new RuntimeException("Error reading principal");
        }
        var authToken = jwtService.generateToken(user);
        return new LoginResponse(authToken, "Bearer", 3600);
    }
}
