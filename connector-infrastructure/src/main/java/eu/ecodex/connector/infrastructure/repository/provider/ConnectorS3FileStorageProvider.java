/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.repository.provider;

import eu.ecodex.connector.domain.model.message.attachment.ConnectorMessageAttachment;
import eu.ecodex.connector.domain.spi.ConnectorFileStorageProvider;
import eu.ecodex.connector.infrastructure.property.ConnectorS3ProviderProperties;
import java.io.InputStream;
import lombok.NonNull;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

/**
 * S3 Implementation of the {@link ConnectorFileStorageProvider}.
 */
@Component
@ConditionalOnProperty(value = "connector.file.storage.s3.enable", havingValue = "true")
public class ConnectorS3FileStorageProvider implements ConnectorFileStorageProvider {
    private final S3Client s3Client;
    private final ConnectorS3ProviderProperties s3ProviderProperties;

    public ConnectorS3FileStorageProvider(
            S3Client s3Client,
            ConnectorS3ProviderProperties s3ProviderProperties) {
        this.s3Client = s3Client;
        this.s3ProviderProperties = s3ProviderProperties;
    }

    @Override
    public String save(
            @NonNull ConnectorMessageAttachment attachment, @NonNull InputStream inputStream) {

        var putObjectRequest = PutObjectRequest.builder()
                                               .bucket(this.s3ProviderProperties.getBucket())
                                               .key(attachment.identifier())
                                               .build();
        this.s3Client.putObject(
                putObjectRequest,
                RequestBody.fromInputStream(inputStream, attachment.size())
        );

        return attachment.identifier();
    }
}
