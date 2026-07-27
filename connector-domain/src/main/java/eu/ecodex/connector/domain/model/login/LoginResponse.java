package eu.ecodex.connector.domain.model.login;

public record LoginResponse(
        String accessToken,
        String tokenType,
        long expiresIn
) {
}
