/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.adapter.in.web.rest.request.login;

import eu.ecodex.connector.application.port.api.auth.user.ConnectorUpdateUserPasswordCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

/**
 * Represents a request to update the password of the authenticated user in the connector system.
 *
 * <p>All components are mandatory and validated with Bean Validation. The constraints are enforced
 * only when the record is validated, for example through {@code @Valid} on a controller parameter.
 *
 * @param refreshToken    the refresh token of the user's current session (must not be blank)
 * @param currentPassword the user's current password (must not be blank)
 * @param newPassword     the new password to set (must not be blank, at least 6 characters)
 */
@Builder
public record ConnectorUpdateUserPasswordRequest(
    @NotBlank(message = "The [refreshToken] must not be blank")
    String refreshToken,

    @NotBlank(message = "The [currentPassword] must not be blank")
    String currentPassword,

    @NotBlank(message = "The [newPassword] must not be blank")
    @Size(min = 6, message = "The [newPassword] must be at least 6 characters")
    String newPassword
) {
    /**
     * Map a request user into a domain user.
     *
     * @param userRequest user to map
     * @param accessToken user access token
     * @param uuid        user identifier
     *
     * @return domain user
     */
    public static ConnectorUpdateUserPasswordCommand from(
        String uuid,
        String accessToken,
        ConnectorUpdateUserPasswordRequest userRequest) {
        return ConnectorUpdateUserPasswordCommand.builder()
                                                 .uuid(uuid)
                                                 .accessToken(accessToken)
                                                 .refreshToken(userRequest.refreshToken())
                                                 .currentPassword(userRequest.currentPassword())
                                                 .newPassword(userRequest.newPassword())
                                                 .build();
    }
}
