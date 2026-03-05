plugins {
    id("java")
    id("jacoco")
    id("java-test-fixtures")
}

val mockitoAgent: Configuration = configurations.create("mockitoAgent")

dependencies {
    implementation(project(":connector-domain"))
    implementation(project(":connector-soap-api"))
    implementation(project(":connector-application"))
    // apache
    implementation(libs.apache.cxf.spring.boot)
    implementation(libs.apache.commons.lang3)
    // spring boot
    implementation(platform(libs.spring.boot.bom))
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-artemis")
    // databases
    implementation(libs.h2)
    runtimeOnly(libs.mysql)
    // jakarta
    implementation(libs.jakarta.annotation)
    implementation(libs.jakarta.validation)
    // artemis
    implementation("org.apache.activemq:artemis-jakarta-server")
    // jta
    implementation(libs.narayana)
    implementation(libs.agroal)
    implementation(libs.messaginghub)
    // doc
    implementation(libs.spring.doc)
    // other
    implementation(libs.apache.cxf)
    implementation(libs.lombok)
    annotationProcessor(libs.lombok)
    implementation(libs.micrometer)
    implementation(libs.s3)
    // test
    testImplementation(testFixtures(project(":connector-domain")))
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-data-jpa-test")
    testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
    testFixturesImplementation(project(":connector-soap-api"))
    testFixturesImplementation(platform(libs.spring.boot.bom))
    testFixturesImplementation("org.springframework:spring-core")
    testFixturesImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
    testFixturesImplementation(libs.jakarta.activation)
    testFixturesImplementation(libs.jakarta.mail)
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation(libs.assertj.core)
    testImplementation(libs.mockito)
    mockitoAgent(libs.mockito.core) { isTransitive = false }
}
