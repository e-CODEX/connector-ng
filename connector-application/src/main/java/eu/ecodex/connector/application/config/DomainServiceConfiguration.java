/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.config;

import eu.ecodex.connector.domain.annotation.DomainService;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

/**
 * Registers domain-layer services as Spring beans.
 *
 * <p>Domain services are annotated with {@link DomainService} instead of Spring stereotypes to keep
 * the domain module free of framework dependencies.
 */
@Configuration
@ComponentScan(
        basePackages = "eu.ecodex.connector.domain.service",
        includeFilters = @ComponentScan.Filter(
                type = FilterType.ANNOTATION,
                classes = DomainService.class
        ),
        useDefaultFilters = false
)
public class DomainServiceConfiguration {
}
