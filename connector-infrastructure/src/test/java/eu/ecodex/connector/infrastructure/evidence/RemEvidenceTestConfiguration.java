/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.evidence;

import eu.ecodex.connector.infrastructure.config.DssConfig;
import eu.ecodex.connector.infrastructure.config.EvidenceConfig;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

/**
 * Loads DSS + REM evidence beans (aligned with infra property + dss scans used at runtime).
 */
@SpringBootConfiguration
@EnableAutoConfiguration
@Import({DssConfig.class, EvidenceConfig.class})
@ComponentScan(
        basePackages = {
                "eu.ecodex.connector.infrastructure.property",
                "eu.ecodex.connector.infrastructure.dss",
                "eu.ecodex.connector.infrastructure.evidence",
                "eu.ecodex.connector.evidences",
        }
)
@SuppressWarnings("checkstyle:MissingJavadocType")
public class RemEvidenceTestConfiguration {
}
