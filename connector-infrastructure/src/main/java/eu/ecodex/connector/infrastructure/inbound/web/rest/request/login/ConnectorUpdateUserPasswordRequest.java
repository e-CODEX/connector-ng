/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.inbound.web.rest.request.login;

import eu.ecodex.connector.domain.model.auth.ConnectorUpdateUserPasswordData;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

/**
 * Represents a request to update a user password in to the connector system s.
 */
@Builder
public record ConnectorUpdateUserPasswordRequest(
    @NotBlank(message = "Refresh token is mandatory")
    String refreshToken,
    @NotBlank(message = "Current password is mandatory")
    String currentPassword,
    @NotBlank(message = "New password is mandatory")
    @Size(min = 6, message = "New password must be at least 8 characters")
    String newPassword) {

    /**
     * Map a request user into a domain user.
     *
     * @param userRequest user to map
     * @param accessToken user access token
     * @param uuid        user identifier
     *
     * @return domain user
     */
    public static ConnectorUpdateUserPasswordData toDomain(
        String uuid,
        String accessToken,
        ConnectorUpdateUserPasswordRequest userRequest) {
        return ConnectorUpdateUserPasswordData.builder()
            .uuid(uuid)
            .accessToken(accessToken)
            .refreshToken(userRequest.refreshToken())
            .currentPassword(userRequest.currentPassword())
            .newPassword(userRequest.newPassword())
            .build();
    }
}
