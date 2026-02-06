plugins {
    id("java")
    id("jacoco")
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
    // implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    // implementation("org.springframework.boot:spring-boot-starter-log4j2")
    // databases
    implementation(libs.h2)
    // other
    implementation(libs.lombok)
    annotationProcessor(libs.lombok)
    implementation(libs.micrometer)
    // test
    testImplementation(testFixtures(project(":connector-domain")))
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-data-jpa-test")
    testImplementation(platform(libs.junit.bom ))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.junit.jupiter:junit-jupiter-params")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation(libs.assertj.core)
    testImplementation(libs.mockito)
    mockitoAgent(libs.mockito.core) { isTransitive = false }
}
