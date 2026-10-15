/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.service.auth.refreshtoken;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ConnectorRefreshTokenGeneratorTest {
    @Test
    void hash_should_return_hashed_value() {
        // Given
        var rawToken = "abc";

        // When
        var hashed = ConnectorRefreshTokenGenerator.hash(rawToken);
        var sha256 = "ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad";

        // Then
        assertThat(hashed).isEqualTo(sha256);
    }

    @Test
    void generate_should_generate_new_raw_token_with_fix_length() {
        // Given
        // When
        var token = ConnectorRefreshTokenGenerator.generate();

        // Then
        assertThat(token).matches(
            "[A-Za-z0-9_-]{" + ConnectorRefreshTokenGenerator.TOKEN_LENGTH + "}");
        assertThat(ConnectorRefreshTokenGenerator.generate()).isNotEqualTo(token);
    }
}