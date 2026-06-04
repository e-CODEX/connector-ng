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

import eu.ecodex.connector.infrastructure.messaging.listener.inbound.ConnectorGatewayMessageAcknowledgementListener;
import eu.ecodex.connector.infrastructure.messaging.listener.inbound.ConnectorGatewayMessageListener;
import eu.ecodex.connector.infrastructure.messaging.listener.inbound.ConnectorInboundMessagePipelineListener;
import eu.ecodex.connector.infrastructure.messaging.listener.outbound.ConnectorBackendMessageDeliveryListener;
import eu.ecodex.connector.infrastructure.messaging.listener.outbound.ConnectorOutboundMessagePipelineListener;
import eu.ecodex.connector.infrastructure.messaging.listener.outbound.ConnectorOutboundMessageStagingListener;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import java.time.Duration;
import org.junit.jupiter.api.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.testcontainers.containers.MinIOContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.lifecycle.Startables;
import org.testcontainers.mysql.MySQLContainer;

@Testcontainers
@Tag("integration")
@ActiveProfiles("test")
@AutoConfigureRestTestClient
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class AbstractIntegrationTest {
    public static final MinIOContainer minio;
    public static final MySQLContainer mysql;
    private static final MinioClient minioClient;

    static {
        minio = new MinIOContainer("minio/minio:RELEASE.2025-09-07T16-13-09Z")
                .withUserName("testuser")
                .withPassword("testpassword")
                .withStartupTimeout(Duration.ofMinutes(2));

        mysql = new MySQLContainer("mysql:8.0.33")
                .withDatabaseName("connector")
                .withUsername("connector")
                .withPassword("connector");

        Startables.deepStart(minio, mysql).join();

        try {
            minioClient = MinioClient.builder()
                                     .endpoint(minio.getS3URL())
                                     .credentials(minio.getUserName(), minio.getPassword())
                                     .build();
            createBucketIfNotExists();
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize MinIO client", e);
        }
    }

    @MockitoBean
    ConnectorBackendMessageDeliveryListener backendMessageDeliveryListener;
    @MockitoBean
    ConnectorGatewayMessageAcknowledgementListener gatewayMessageAcknowledgementListener;
    @MockitoBean
    ConnectorGatewayMessageListener gatewayMessageListener;
    @MockitoBean
    ConnectorInboundMessagePipelineListener inboundMessagePipelineListener;
    @MockitoBean
    ConnectorOutboundMessagePipelineListener outboundMessagePipelineListener;
    @MockitoBean
    ConnectorOutboundMessageStagingListener outboundMessageStagingListener;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @DynamicPropertySource
    static void registerPropertiesMain(DynamicPropertyRegistry registry) {
        registry.add(
                "spring.datasource.agroal.driver-class-name",
                () -> "com.mysql.cj.jdbc.MysqlXADataSource"
        );
        registry.add("spring.jpa.database-platform", () -> "org.hibernate.dialect.MySQLDialect");
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
        registry.add("spring.jpa.defer-datasource-initialization", () -> "true");

        registry.add("connector.file.storage.s3.access-key", minio::getUserName);
        registry.add("connector.file.storage.s3.secret-key", minio::getPassword);
        registry.add("connector.file.storage.s3.bucket", () -> "attachments");
        registry.add("connector.file.storage.s3.region", () -> "us-east-1");
        registry.add("connector.file.storage.s3.endpoint", minio::getS3URL);
    }

    private static void createBucketIfNotExists() throws Exception {
        boolean exists = minioClient.bucketExists(
                BucketExistsArgs.builder().bucket("attachments").build()
        );
        if (!exists) {
            minioClient.makeBucket(
                    MakeBucketArgs.builder().bucket("attachments").build()
            );
        }
    }

    protected MultiValueMap<String, Object> produceAttachmentPart(
            MediaType mediaType,
            int fileSize) {
        var parts = new LinkedMultiValueMap<String, Object>();

        parts.add(
                "attachments",
                FilePartTestFixtures.filePart(
                        "fake_file.pdf",
                        FileTestFixtures.generateFakeFile(fileSize),
                        mediaType
                )
        );

        return parts;
    }

    protected void cleanDb() {
        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 0");
        jdbcTemplate.execute("TRUNCATE TABLE connector_message_transport_step_statuses");
        jdbcTemplate.execute("TRUNCATE TABLE connector_message_transport_steps");
        jdbcTemplate.execute("TRUNCATE TABLE connector_message_business_document_signatures");
        jdbcTemplate.execute("TRUNCATE TABLE connector_message_business_documents");
        jdbcTemplate.execute("TRUNCATE TABLE connector_message_business_contents");
        jdbcTemplate.execute("TRUNCATE TABLE connector_message_as4_properties");
        jdbcTemplate.execute("TRUNCATE TABLE connector_message_attachments");
        jdbcTemplate.execute("TRUNCATE TABLE connector_messages");
        jdbcTemplate.execute("TRUNCATE TABLE connector_parties");
        jdbcTemplate.execute("TRUNCATE TABLE connector_services");
        jdbcTemplate.execute("TRUNCATE TABLE connector_actions");
        jdbcTemplate.execute("TRUNCATE TABLE connector_processing_modes");
        jdbcTemplate.execute("TRUNCATE TABLE connector_keystores");
        jdbcTemplate.execute("TRUNCATE TABLE connector_business_domains");
        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 1");
    }
}
