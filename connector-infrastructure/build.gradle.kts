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
    // dss tool
    implementation(platform(libs.dss.tool))
    implementation("eu.europa.ec.joinup.sd-dss:dss-document")
    implementation("eu.europa.ec.joinup.sd-dss:dss-xades")
    implementation("eu.europa.ec.joinup.sd-dss:dss-cades")
    implementation("eu.europa.ec.joinup.sd-dss:dss-pades")
    implementation("eu.europa.ec.joinup.sd-dss:dss-asic-xades")
    implementation("eu.europa.ec.joinup.sd-dss:dss-asic-cades")
    implementation("eu.europa.ec.joinup.sd-dss:dss-token")
    implementation("eu.europa.ec.joinup.sd-dss:dss-tsl-validation")
    implementation("eu.europa.ec.joinup.sd-dss:dss-service")
    implementation("eu.europa.ec.joinup.sd-dss:dss-policy-jaxb")
    implementation("eu.europa.ec.joinup.sd-dss:dss-validation")
    implementation("eu.europa.ec.joinup.sd-dss:dss-tsl-validation")
    implementation("eu.europa.ec.joinup.sd-dss:dss-certificate-validation-common")
    implementation("eu.europa.ec.joinup.sd-dss:dss-utils-apache-commons")
    implementation("eu.europa.ec.joinup.sd-dss:dss-pades-openpdf")
    // spring boot
    implementation(platform(libs.spring.boot.bom))
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-activemq")
    // databases
    implementation(libs.h2)
    runtimeOnly(libs.mysql)
    // jakarta
    implementation(libs.jakarta.annotation)
    implementation(libs.jakarta.validation)
    // activemq
    implementation("org.apache.activemq:activemq-client")
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
