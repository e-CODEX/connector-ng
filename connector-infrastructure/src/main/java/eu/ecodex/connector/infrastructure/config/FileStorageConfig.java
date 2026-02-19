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

import eu.ecodex.connector.infrastructure.property.ConnectorS3ProviderProperties;
import java.net.URI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

/**
 * FileStorageConfig class for configuring the file storage.
 */
@Configuration
@SuppressWarnings("checkstyle:MissingJavadocMethod")
public class FileStorageConfig {
    @Bean
    public S3Client s3Client(ConnectorS3ProviderProperties properties) {
        return S3Client.builder()
                       .region(Region.of(properties.getRegion()))
                       .endpointOverride(URI.create(properties.getEndpoint()))
                       .credentialsProvider(
                               StaticCredentialsProvider.create(
                                       AwsBasicCredentials.create(
                                               properties.getAccessKey(),
                                               properties.getSecretKey()
                                       )
                               )
                       )
                       .forcePathStyle(true)
                       .build();
    }
}
