/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.config;

import java.time.Clock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for creating and managing the application-wide clock instance.
 *
 * <p>This class provides a central configuration for the application's time management by
 * exposing a {@link Clock} bean that uses the UTC time zone. The {@link Clock} instance can
 * be injected and utilized wherever the application requires consistent and centralized
 * time reference.
 *
 * <p>By using the UTC clock, this configuration ensures that any time-related operations
 * across the application remain consistent and independent of local time zones.
 */
@Configuration
public class ClockConfig {
    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }
}
