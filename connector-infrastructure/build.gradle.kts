plugins {
    id("java")
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
    implementation(platform(libs.spring.boot.bom))
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-log4j2")
    implementation(libs.h2)
    implementation(libs.micrometer)
    // test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation(platform(libs.junit.bom ))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.junit.jupiter:junit-jupiter-params")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation(libs.assertj.core)
    testImplementation(libs.mockito)
    mockitoAgent(libs.mockito.core) { isTransitive = false }
}

configurations {
    all {
        exclude(group = "org.springframework.boot", module = "spring-boot-starter-logging")
    }
}

tasks.test {
    useJUnitPlatform()
}
