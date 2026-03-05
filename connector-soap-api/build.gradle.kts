import io.mateo.cxf.codegen.wsdl2java.Wsdl2Java

plugins {
    id("java")
    id("io.mateo.cxf-codegen") version "2.5.0"
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
}

tasks.withType(Wsdl2Java::class).configureEach {
    toolOptions {
        outputDir = file(layout.projectDirectory.dir("build/generated"))
        packageNames = listOf("eu.ecodex.connector.domain.transition")
    }
    allJvmArgs = listOf("-Duser.language=en", "-Duser.country=UK")
}

