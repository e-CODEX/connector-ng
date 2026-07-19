/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector;

import eu.ecodex.connector.infrastructure.config.BeanConfig;
import eu.ecodex.connector.infrastructure.config.DatabaseConfig;
import eu.ecodex.connector.infrastructure.config.DssConfig;
import eu.ecodex.connector.infrastructure.config.RepositoryConfig;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@SpringBootConfiguration
@EnableAutoConfiguration
@SuppressWarnings("checkstyle:MissingJavadocType")
@Import({DssConfig.class, DatabaseConfig.class, RepositoryConfig.class, BeanConfig.class})
@ComponentScan(
        basePackages = {
                "eu.ecodex.connector.domain",
                "eu.ecodex.connector.infrastructure.outbound.database",
                "eu.ecodex.connector.infrastructure.property",
                "eu.ecodex.connector.infrastructure.outbound.persistence",
                "eu.ecodex.connector.infrastructure.outbound.provider",
                "eu.ecodex.connector.infrastructure.dss",
                "eu.ecodex.connector.infrastructure.outbound.security"
        }
)
public class ContainerContextConfiguration {
}
