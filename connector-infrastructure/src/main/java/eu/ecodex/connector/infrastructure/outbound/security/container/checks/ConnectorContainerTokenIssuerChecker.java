/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.outbound.security.container.checks;

import eu.ecodex.connector.infrastructure.outbound.security.model.container.ConnectorContainer;
import eu.ecodex.connector.infrastructure.outbound.security.model.token.ConnectorTokenIssuer;
import java.util.Arrays;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.NonNull;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Checker for the {@link ConnectorTokenIssuer}.
 */
@Order(2)
@Component("ConnectorContainerTokenIssuerChecker")
public class ConnectorContainerTokenIssuerChecker implements ConnectorMessageContainerChecker {
    private static final Set<String> ISO_COUNTRIES =
        Arrays.stream(Locale.getISOCountries())
              .map(code -> code.toUpperCase(Locale.ROOT))
              .collect(Collectors.toUnmodifiableSet());

    @Override
    public void check(@NonNull ConnectorContainer container) {
        var tokenIssuer = container.token().getIssuer();

        if (tokenIssuer == null) {
            throw new IllegalStateException("ConnectorToken must contain an issuer");
        }

        if (!StringUtils.hasText(tokenIssuer.getServiceProvider())) {
            throw new IllegalStateException(
                "ConnectorTokenIssuer must contain a service provider");
        }

        if (!StringUtils.hasText(tokenIssuer.getCountry())) {
            throw new IllegalStateException(
                "ConnectorTokenIssuer must contain a country");
        }

        if (!ISO_COUNTRIES.contains(tokenIssuer.getCountry().toUpperCase(Locale.ROOT))) {
            throw new IllegalStateException(
                "ConnectorTokenIssuer country must be a valid ISO 3166-1 alpha-2 country code");
        }

        if (tokenIssuer.getAdvancedElectronicSystem() == null) {
            throw new IllegalStateException(
                "ConnectorTokenIssuer must contain an advanced electronic system");
        }
    }
}
