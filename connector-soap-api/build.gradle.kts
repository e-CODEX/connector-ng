/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

import io.mateo.cxf.codegen.wsdl2java.Wsdl2Java

plugins {
    id("java")
    id("io.mateo.cxf-codegen") version "3.0.0"
}

val mockitoAgent: Configuration = configurations.create("mockitoAgent")

dependencies {
    implementation(libs.jakarta.jws.api)
    implementation(libs.jakarta.xml.bind.api)
    implementation(libs.jakarta.xml.ws.api)
    mockitoAgent(libs.mockito.core) { isTransitive = false }
}

cxfCodegen {
    cxfVersion.set("4.1.0")
}

sourceSets {
    main {
        java {
            srcDir(tasks.withType(Wsdl2Java::class))
        }
    }
}

tasks {
    register("DomibusConnectorBackendWebService", Wsdl2Java::class) {
        toolOptions {
            wsdl = file("src/main/resources/wsdl/v1/DomibusConnectorBackendWebService.wsdl")
                .toPath().toRealPath().toString()
        }
    }
    register("DomibusConnectorBackendDeliveryWebService", Wsdl2Java::class) {
        toolOptions {
            wsdl = file("src/main/resources/wsdl/v1/DomibusConnectorBackendDeliveryWebService.wsdl")
                .toPath().toRealPath().toString()
        }
    }
}

tasks.withType(Wsdl2Java::class).configureEach {
    toolOptions {
        outputDir = file(layout.projectDirectory.dir("build/generated"))
        packageNames = listOf("eu.ecodex.connector.domain.transition")
    }
    allJvmArgs = listOf("-Duser.language=en", "-Duser.country=UK")
}

