plugins {
    id("java")
}

group = "eu.ecodex"
version = "1.0.0-SNAPSHOT"

repositories {
    mavenCentral()
}

val mockitoAgent: Configuration = configurations.create("mockitoAgent")

dependencies {
    implementation(project(":connector-domain"))
    implementation(platform(libs.spring.boot.bom))
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation(libs.h2)
    // test
    testImplementation(platform(libs.junit.bom ))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.junit.jupiter:junit-jupiter-params")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation(libs.assertj.core)
    testImplementation(libs.mockito)
    mockitoAgent(libs.mockito.core) { isTransitive = false }
}

tasks.test {
    useJUnitPlatform()
}
