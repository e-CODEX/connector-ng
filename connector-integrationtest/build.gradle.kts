plugins {
    id("java")
    id("jacoco")
    id("checkstyle")
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
    testImplementation(project(":connector-infrastructure"))
    // testImplementation(project(":connector-bootstrapper"))
    // test
    testImplementation(testFixtures(project(":connector-infrastructure")))
    testFixturesImplementation(testFixtures(project(":connector-infrastructure")))
    // spring boot
    testImplementation(platform(libs.spring.boot.bom))
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
    testImplementation("org.springframework.boot:spring-boot-starter-data-jpa-test")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.testcontainers:testcontainers-junit-jupiter")
    testImplementation(libs.jackson.databind)
    testImplementation(libs.okhttp)
    testImplementation(libs.minio)
    testImplementation(libs.minio.container)
    testFixturesImplementation(platform(libs.spring.boot.bom))
    testFixturesImplementation("org.springframework:spring-core")
    testFixturesImplementation("org.springframework.boot:spring-boot-starter-web")
    // other
    testImplementation(libs.s3)
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation(libs.assertj.core)

    mockitoAgent(libs.mockito.core) { isTransitive = false }
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

// Separate task for integration tests
tasks.register<Test>("integrationTest") {
    useJUnitPlatform {
        includeTags("integration")
    }
    group = "verification"
    description = "Runs integration tests requiring Testcontainers (Linux only)"

    maxParallelForks = 1
    forkEvery = 0

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
