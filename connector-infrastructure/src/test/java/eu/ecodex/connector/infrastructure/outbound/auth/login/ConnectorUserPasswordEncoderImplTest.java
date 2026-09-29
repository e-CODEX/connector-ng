/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.outbound.auth.login;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

class ConnectorUserPasswordEncoderImplTest {

    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    ConnectorUserPasswordEncoderImpl service =
        new ConnectorUserPasswordEncoderImpl(passwordEncoder);


    @Test
    void encodePassword_should_encode_password_with_password_encoder() {
        // Given
        var password = "password";

        // When
        var actual = service.encodePassword(password);

        // Then
        assertThat(actual).isNotEqualTo(password).isNotBlank();
        assertThat(passwordEncoder.matches(password, actual)).isTrue();
    }

    @Test
    void encodePassword_should_return_password() {
        // Given
        var password = "";

        // When
        var actual = service.encodePassword(password);

        // Then
        assertThat(actual).isEqualTo(password);
    }


    @Test
    void matches() {
        // Given
        var password = "password";
        var encodedPassword = passwordEncoder.encode(password);

        // When
        var actual = service.matches(password, encodedPassword);

        // Then
        assertThat(actual).isTrue();
    }
}