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

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

public class SeaweedFSContainer extends GenericContainer<SeaweedFSContainer> {
    private static final int S3_PORT = 8333;
    private String userName;
    private String password;
    private String bucketName;

    public SeaweedFSContainer(String dockerImageName) {
        super(DockerImageName.parse(dockerImageName));
        withExposedPorts(S3_PORT);
        waitingFor(Wait.forListeningPort());
    }

    @Override
    protected void configure() {
        super.configure();
        withCommand("server", "-dir=/data", "-s3", "-s3.port=" + S3_PORT);
        withEnv("AWS_ACCESS_KEY_ID", userName);
        withEnv("AWS_SECRET_ACCESS_KEY", password);
        if (bucketName != null) {
            withEnv("S3_BUCKET", bucketName);
        }
    }

    public SeaweedFSContainer withUserName(String userName) {
        this.userName = userName;
        return this;
    }

    public SeaweedFSContainer withPassword(String password) {
        this.password = password;
        return this;
    }

    public SeaweedFSContainer withBucket(String bucketName) {
        this.bucketName = bucketName;
        return this;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public String getS3URL() {
        return String.format("http://%s:%d", getHost(), getMappedPort(S3_PORT));
    }
}