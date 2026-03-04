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

import io.minio.ListObjectsArgs;
import io.minio.MinioClient;
import io.minio.RemoveBucketArgs;
import io.minio.RemoveObjectArgs;
import java.time.Duration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MinIOContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.mysql.MySQLContainer;
import software.amazon.awssdk.services.s3.S3Client;

@Testcontainers
@Tag("integration")
@ActiveProfiles("test")
@AutoConfigureRestTestClient
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class AbstractIntegrationTest {
    @Container
    public static MinIOContainer minio = new MinIOContainer(
            "minio/minio:RELEASE.2025-09-07T16-13-09Z")
            .withUserName("testuser")
            .withPassword("testpassword")
            .withStartupTimeout(Duration.ofMinutes(2));

    public static MySQLContainer mysql = new MySQLContainer("mysql:8.0.33")
            .withDatabaseName("connector")
            .withUsername("connector")
            .withPassword("connector");

    private static MinioClient minioClient;
    @Autowired
    protected S3Client s3Client;

    @BeforeAll
    public static void startServer() {
        minio.start();
        mysql.start();
    }

    @DynamicPropertySource
    static void registerPropertiesMain(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.agroal.driver-class-name", () -> "com.mysql.cj.jdbc.MysqlXADataSource");
        registry.add("spring.jpa.database-platform", () -> "org.hibernate.dialect.MySQLDialect");
        registry.add("spring.datasource.url", () -> mysql.getJdbcUrl());
        registry.add("spring.datasource.username", () -> mysql.getUsername());
        registry.add("spring.datasource.password", () -> mysql.getPassword());
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
        registry.add("spring.jpa.defer-datasource-initialization", () -> "true");

        registry.add("connector.file.storage.s3.access-key", () -> minio.getUserName());
        registry.add("connector.file.storage.s3.secret-key", () -> minio.getPassword());
        registry.add("connector.file.storage.s3.bucket", () -> "attachments");
        registry.add("connector.file.storage.s3.region", () -> "us-east-1");
        registry.add("connector.file.storage.s3.endpoint", () -> minio.getS3URL());
    }

    @AfterEach
    void cleanUp() {
        try {
            minioClient = MinioClient.builder()
                                     .endpoint(minio.getS3URL())
                                     .credentials(minio.getUserName(), minio.getPassword())
                                     .build();

            for (var bucket : minioClient.listBuckets()) {
                var items = minioClient.listObjects(
                        ListObjectsArgs.builder().bucket(bucket.name()).recursive(true).build());
                for (var item : items) {
                    minioClient.removeObject(RemoveObjectArgs.builder()
                                                             .bucket(bucket.name())
                                                             .object(item.get().objectName())
                                                             .build());
                }
                minioClient.removeBucket(RemoveBucketArgs.builder().bucket(bucket.name()).build());
            }
        } catch (Exception e) {
            throw new RuntimeException("failed to clean up MinIO buckets", e);
        }
    }
}
