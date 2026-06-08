plugins {
    id("java")
    id("jacoco")
    id("java-test-fixtures")
}

sourceSets {
    main {
        java.setSrcDirs(emptyList<String>())
        resources.setSrcDirs(emptyList<String>())
    }
    test {
        java.srcDirs("src/test/java")
        resources.srcDirs("src/test/resources")
    }
}

val mockitoAgent: Configuration = configurations.create("mockitoAgent")
dependencies {
    // app modules
    testImplementation(project(":connector-domain"))
    testImplementation(project(":connector-application"))
    testImplementation(project(":connector-soap-api"))
    testImplementation(project(":connector-infrastructure"))
    testImplementation(project(":connector-bootstrapper"))

    // fixtures
    testImplementation(testFixtures(project(":connector-infrastructure")))
    testFixturesImplementation(project(":connector-domain"))
    testFixturesImplementation(testFixtures(project(":connector-infrastructure")))

    // spring boot
    testImplementation(platform(libs.spring.boot.bom))
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
    testImplementation("org.springframework.boot:spring-boot-starter-data-jpa-test")

    // apache
    // apache cxf
    testImplementation(platform(libs.apache.cxf.bom))
    testImplementation("org.apache.cxf:cxf-spring-boot-starter-jaxws")
    testImplementation("org.apache.cxf:cxf-rt-frontend-jaxws")
    testImplementation("org.apache.cxf:cxf-rt-ws-policy")
    testImplementation("org.apache.cxf:cxf-rt-ws-security")
    testImplementation("org.apache.cxf:cxf-rt-features-logging")
    // apache web service security
    implementation(libs.apache.wss4j.common) {
        exclude(group = "org.apache.santuario", module = "xmlsec")
    }
    implementation(libs.apache.wss4j.dom)
    // xml security
    implementation(libs.apache.xmlsec)

    // testcontainers
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.testcontainers:testcontainers-junit-jupiter")
    testImplementation("org.testcontainers:testcontainers-mysql")

    // other
    testImplementation(platform(libs.apache.cxf.bom))
    testImplementation("org.apache.cxf:cxf-rt-frontend-jaxws")
    testImplementation(libs.assertj.core)
    testImplementation(libs.jackson.databind)
    testImplementation(libs.okhttp)
    testImplementation(libs.minio)
    testImplementation(libs.minio.container)
    testImplementation(libs.s3)
    testImplementation("org.apache.activemq:activemq-broker")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    // other fixtures
    testFixturesImplementation(platform(libs.spring.boot.bom))
    testFixturesImplementation("org.springframework:spring-core")
    testFixturesImplementation("org.springframework.boot:spring-boot-starter-web")


    mockitoAgent(libs.mockito.core) { isTransitive = false }
}

configurations {
    all {
        exclude(group = "org.springframework.boot", module = "spring-boot-starter-logging")
    }
}

tasks.named<ProcessResources>("processTestResources") {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.named<Jar>("jar") {
    enabled = false
}

tasks.test {
    useJUnitPlatform {
        excludeTags("integration")
    }
}

// separate task for integration tests
tasks.register<Test>("integrationTest") {
    group = "verification"
    description = "Runs integration tests requiring Testcontainers (Linux only)"

    testClassesDirs = sourceSets["test"].output.classesDirs
    classpath = sourceSets["test"].runtimeClasspath

    dependsOn(tasks.testClasses)

    useJUnitPlatform {
        includeTags("integration")
    }

    maxParallelForks = 1
    systemProperty("junit.jupiter.execution.parallel.enabled", "false")

    // increase memory for integration tests
    maxHeapSize = "2g"
    minHeapSize = "512m"

    // Testcontainers needs Docker — give it more time
    testLogging {
        events("passed", "skipped", "failed")
        showStandardStreams = false
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
    }
}
