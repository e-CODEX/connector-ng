/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.inbound.web.rest.request.user;

import eu.ecodex.connector.application.port.api.auth.user.ConnectorEditUserCommand;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.NonNull;


/**
 * Represents a request for creating or updating a user in the connector system.
 *
 * @param username           the new username (must not be blank, at most 255 characters)
 * @param email              the new email address; must be a valid email address of at most 255
 *                           characters if provided, and may be {@code null}
 * @param enabled            whether the user account is enabled; may be {@code null}
 * @param mustChangePassword whether the user must change their password at next login; may be
 *                           {@code null}
 */
@Builder(toBuilder = true)
public record ConnectorEditUserRequest(
    @NotBlank(message = "The [username] must not be blank")
    @Size(max = 255, message = "The [username] must not exceed 255")
    String username,

    @Email(message = "The [email] must be a valid email address")
    @Size(max = 255, message = "The [email] must not exceed 255")
    String email,

    Boolean enabled,
    Boolean mustChangePassword
) {
    /**
     * Maps an edit user request to an edit user command.
     *
     * @param identifier  the unique identifier of the user to edit (must not be null)
     * @param userRequest the {@link ConnectorEditUserRequest} carrying the new values (must not be
     *                    null)
     *
     * @return the {@link ConnectorEditUserCommand} built from the identifier and the request
     */
    public static ConnectorEditUserCommand toCommand(
        @NonNull String identifier,
        @NonNull ConnectorEditUserRequest userRequest) {
        return ConnectorEditUserCommand.builder()
                                       .uuid(identifier)
                                       .username(userRequest.username())
                                       .email(userRequest.email())
                                       .enabled(userRequest.enabled())
                                       .mustChangePassword(userRequest.mustChangePassword())
                                       .build();
    }
}
