/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    id("java")
    id("org.springframework.boot") version "4.1.2"
    id("io.spring.dependency-management") version "1.1.7"
}

springBoot {
    mainClass.set("eu.ecodex.connector.ConnectorApplication")
    buildInfo()
}

val mockitoAgent: Configuration = configurations.create("mockitoAgent")

dependencies {
    implementation(project(":connector-infrastructure"))
    implementation(project(":connector-application"))
    // spring boot
    implementation(platform(libs.spring.boot.bom))
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-log4j2")
    mockitoAgent(libs.mockito.core) { isTransitive = false }
}

configurations {
    all {
        exclude(group = "org.springframework.boot", module = "spring-boot-starter-logging")
    }
}

tasks.named<BootJar>("bootJar") {
    archiveFileName.set("connector-ng.jar")
}

tasks.named("build") {
    dependsOn("bootJar")
}
