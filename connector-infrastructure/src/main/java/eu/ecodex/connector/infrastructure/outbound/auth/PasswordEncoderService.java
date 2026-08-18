/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.outbound.auth;

import eu.ecodex.connector.application.port.spi.auth.user.ConnectorUserPasswordEncoder;
import eu.ecodex.connector.domain.model.user.ConnectorUser;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Service implementation for encoding passwords of {@link ConnectorUser} entities.
 *
 * <p>This class provides functionality for encoding the password of a {@link ConnectorUser}
 * and is an implementation of the {@link ConnectorUserPasswordEncoder} interface.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class PasswordEncoderService implements ConnectorUserPasswordEncoder {

    PasswordEncoder passwordEncoder;

    /**
     * Encodes the password of a {@link ConnectorUser} entity.
     *
     * @param user user's password to encode
     *
     * @return user with encoded password
     */
    @Override
    public ConnectorUser encodePassword(ConnectorUser user) {
        if (user.password() == null) {
            return user;
        }
        var encodedPassword = encodePassword(user.password());
        return user
            .toBuilder()
            .password(encodedPassword)
            .build();
    }

    @Override
    public String encodePassword(String password) {
        if (password == null) {
            return null;
        }
        return passwordEncoder.encode(password);
    }

    /**
     * Checks if the given encoded raw password matches the encoded password of a
     * {@link ConnectorUser}
     * entity.
     *
     * @param encodedPassword the {@code ConnectorUser} whose password is to be compared.
     * @param rawPassword     the raw password to be checked against the user's encoded password.
     *
     * @return {@code true} if the raw password matches the user's encoded password, otherwise
     */
    @Override
    public boolean matches(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}
