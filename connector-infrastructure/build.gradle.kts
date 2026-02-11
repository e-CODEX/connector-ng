plugins {
    id("java")
    id("jacoco")
    id("java-test-fixtures")
    id("org.springframework.boot") version "4.0.2"
}

group = "eu.ecodex"
version = "1.0.0-SNAPSHOT"

repositories {
    mavenCentral()
}

// TODO might potentially be incorporated into a dedicated starter module
springBoot {
    mainClass.set("eu.ecodex.connector.ConnectorApplication")
}

val mockitoAgent: Configuration = configurations.create("mockitoAgent")

dependencies {
    implementation(project(":connector-domain"))
    // spring boot
    implementation(platform(libs.spring.boot.bom))
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    // databases
    implementation(libs.h2)
    // jakarta
    implementation(libs.jakarta.annotation)
    implementation(libs.jakarta.validation)
    // other
    implementation(libs.lombok)
    annotationProcessor(libs.lombok)
    implementation(libs.micrometer)
    // test
    testImplementation(testFixtures(project(":connector-domain")))
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-data-jpa-test")
    testFixturesImplementation(platform(libs.spring.boot.bom))
    testFixturesImplementation("org.springframework:spring-core")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation(libs.assertj.core)
    testImplementation(libs.mockito)
    mockitoAgent(libs.mockito.core) { isTransitive = false }
}
