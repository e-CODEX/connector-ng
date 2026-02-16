plugins {
    id("java")
    id("jacoco")
    id("checkstyle")
    id("java-test-fixtures")
}

group = "eu.ecodex"
version = "1.0.0-SNAPSHOT"

repositories {
    mavenCentral()
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
    // test
    testImplementation(testFixtures(project(":connector-infrastructure")))
    testFixturesImplementation(testFixtures(project(":connector-infrastructure")))
    // spring boot
    testImplementation(platform(libs.spring.boot.bom))
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
    testImplementation("org.springframework.boot:spring-boot-starter-data-jpa-test")
    testFixturesImplementation(platform(libs.spring.boot.bom))
    testFixturesImplementation("org.springframework:spring-core")
    testFixturesImplementation("org.springframework.boot:spring-boot-starter-web")
    // other
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation(libs.assertj.core)

    mockitoAgent(libs.mockito.core) { isTransitive = false }
}

tasks.named<Jar>("jar") {
    enabled = false
}
tasks.test {
    description = "Runs integration tests."
    group = "verification"

    useJUnitPlatform()

    maxParallelForks = 1
    forkEvery = 0

    // increase memory for integration tests
    maxHeapSize = "2g"
    minHeapSize = "512m"

    testLogging {
        events("passed", "skipped", "failed")
        showStandardStreams = false
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
    }
}
