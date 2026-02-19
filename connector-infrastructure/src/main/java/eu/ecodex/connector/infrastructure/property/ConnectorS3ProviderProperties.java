/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.property;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration properties required to connect to an Amazon S3 (or compatible)
 * object storage service.
 * </p>
 */
@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "connector.file.storage.s3")
public class ConnectorS3ProviderProperties {
    private String region;
    private String accessKey;
    private String secretKey;
    private String bucket;
    private String endpoint;
}
