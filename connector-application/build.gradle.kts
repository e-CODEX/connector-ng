/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

plugins {
    id("java")
    id("jacoco")
    id("java-test-fixtures")
}

val mockitoAgent: Configuration = configurations.create("mockitoAgent")

dependencies {
    implementation(project(":connector-domain"))
    // spring
    implementation(platform(libs.spring.boot.bom))
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework:spring-tx")
    implementation("org.springframework.boot:spring-boot-starter-security")
    // jakarta
    implementation(libs.jakarta.validation)
    // other
    implementation(libs.apache.commons.lang3)
    implementation(libs.lombok)
    annotationProcessor(libs.lombok)
    // test
    testImplementation(testFixtures(project(":connector-domain")))
    testFixturesImplementation(testFixtures(project(":connector-domain")))
    testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
    implementation("org.springframework.boot:spring-boot-starter-restclient-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation(libs.assertj.core)
    testImplementation(libs.mockito)
    mockitoAgent(libs.mockito.core) { isTransitive = false }
}
