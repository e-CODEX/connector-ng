import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    id("java")
    id("org.springframework.boot") version "4.0.2"
    id("io.spring.dependency-management") version "1.1.0"
}

springBoot {
    mainClass.set("eu.ecodex.connector.ConnectorApplication")
}

val mockitoAgent: Configuration = configurations.create("mockitoAgent")

dependencies {
    implementation(project(":connector-infrastructure"))
    implementation(project(":connector-application"))
    // spring boot
    implementation(platform(libs.spring.boot.bom))
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-log4j2")
    mockitoAgent(libs.mockito.core) { isTransitive = false }
}

configurations {
    all {
        exclude(group = "org.springframework.boot", module = "spring-boot-starter-logging")
    }
}


tasks.named<BootJar>("bootJar") {
    archiveFileName.set("connector-ng.jar")
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

}

tasks.named("build") {
    dependsOn("bootJar")
}
