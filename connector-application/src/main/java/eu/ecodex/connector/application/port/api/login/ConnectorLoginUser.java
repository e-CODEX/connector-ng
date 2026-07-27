package eu.ecodex.connector.application.port.api.login;

import eu.ecodex.connector.domain.model.login.LoginResponse;

public interface ConnectorLoginUser {
    LoginResponse login(String username, String password);
}
