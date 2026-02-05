plugins {
    id("java")
    id("jacoco")
    id("java-test-fixtures")
}

group = "eu.ecodex"
version = "1.0.0-SNAPSHOT"

repositories {
    mavenCentral()
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
