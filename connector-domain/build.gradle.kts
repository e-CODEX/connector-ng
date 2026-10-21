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
    implementation(libs.apache.commons.lang3)
    implementation(libs.jakarta.annotation)
    implementation(libs.jakarta.validation)
    implementation(libs.jakarta.jms.api)
    implementation(libs.lombok)
    annotationProcessor(libs.lombok)
    implementation(libs.slf4j.api)
    // tests
    testImplementation(platform(libs.junit.bom ))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.junit.jupiter:junit-jupiter-params")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation(libs.assertj.core)
    testImplementation(libs.mockito)
    mockitoAgent(libs.mockito.core) { isTransitive = false }
}
