package eu.ecodex.connector.domain.model.login;

import lombok.Builder;

/**
 * Represents the response resulting from a successful user login operation.
 * This record encapsulates the authentication token details that are generated
 * and returned to the client upon successful authentication.
 *
 * <p>Fields:
 * - accessToken: The token issued to the authenticated user, used for
 * authorizing subsequent requests.
 * - expiresIn: The duration (in seconds) for which the token remains valid.
 * - refreshToken: An optional refresh token that can be used to obtain new
 *
 * <p>This class is immutable and is designed to securely transport token-related
 * data to ensure a proper authentication and authorization workflow within the
 * system.
 */
@Builder
public record ConnectorLoginResponse(
        String accessToken,
        String refreshToken,
        long expiresIn,
        long refreshExpiresIn
) {
}
