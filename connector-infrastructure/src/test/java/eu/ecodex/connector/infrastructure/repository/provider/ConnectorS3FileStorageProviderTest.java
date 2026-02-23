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

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import eu.ecodex.connector.MessageAttachmentTestFixtures;
import eu.ecodex.connector.infrastructure.property.ConnectorS3ProviderProperties;
import java.io.InputStream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

@SuppressWarnings("DataFlowIssue")
@ExtendWith(MockitoExtension.class)
public class ConnectorS3FileStorageProviderTest {
    @Mock
    private S3Client s3Client;
    @Mock
    private ConnectorS3ProviderProperties s3ProviderProperties;
    @InjectMocks
    private ConnectorS3FileStorageProvider fileStorageProvider;

    @Test
    void should_store_files_into_s3_bucket_successfully() {
        when(s3ProviderProperties.getBucket()).thenReturn("attachments");
        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenReturn(PutObjectResponse.builder().build());

        var inputStream = new InputStream() {
            @Override
            public int read() {
                return 100;
            }
        };
        var attachment = MessageAttachmentTestFixtures.createAttachment();
        var identifier = fileStorageProvider.save(attachment, inputStream);

        assertThat(identifier).endsWith("test_attachment");
    }

    @Test
    void should_throw_null_pointer_exception_when_storing_file_into_s3_if_the_input_stream_is_null() {
        var attachment = MessageAttachmentTestFixtures.createAttachment();
        assertThrows(
                NullPointerException.class,
                () -> fileStorageProvider.save(attachment, null)
        );
    }

    @Test
    void should_throw_null_pointer_exception_when_storing_file_into_s3_if_the_attachment_is_null() {
        var inputStream = new InputStream() {
            @Override
            public int read() {
                return 100;
            }
        };

        assertThrows(
                NullPointerException.class,
                () -> fileStorageProvider.save(null, inputStream)
        );
    }

    @Test
    void should_throw_null_pointer_exception_when_storing_file_into_s3_if_the_attachment_and_input_stream_are_null() {
        assertThrows(
                NullPointerException.class,
                () -> fileStorageProvider.save(null, null)
        );
    }
}
