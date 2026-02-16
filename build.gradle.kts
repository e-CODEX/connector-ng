plugins {
    id("java")
    id("jacoco")
    id("checkstyle")
    id("org.cyclonedx.bom") version "3.1.0"
}

allprojects {
    group = "eu.ecodex"
    version = "1.0.0-SNAPSHOT"

    repositories {
        mavenCentral()
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

tasks.withType<JavaCompile> {
    sourceCompatibility = "21"
    targetCompatibility = "21"
}

jacoco {
    toolVersion = "0.8.14"
}

subprojects {
    apply(plugin = "checkstyle")

    checkstyle {
        toolVersion = "10.17.0"

        val localConfigFile = rootProject.file("config/checkstyle/checkstyle.xml")

        config = resources.text.fromFile(localConfigFile)

        configDirectory.set(rootProject.file("config/checkstyle"))

        isIgnoreFailures = true
        isShowViolations = true
    }

    if (name != "connector-documentation") {
        val testTasks = tasks.withType<Test>()
        val jacocoReportTasks = tasks.withType<JacocoReport>()

        testTasks.configureEach {
            useJUnitPlatform()

            // increase memory for integration tests
            maxHeapSize = "1g"

            testLogging {
                events("passed", "skipped", "failed")
                showStandardStreams = false
                exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
            }

            // set mockito agent to run in all subprojects
            val mockitoAgent by configurations.getting
            jvmArgs("-javaagent:${mockitoAgent.asPath}")

            // This ensures every Test task triggers the JacocoReport tasks in this submodule
            finalizedBy(jacocoReportTasks)
        }

        jacocoReportTasks.configureEach {
            // This ensures reports aren't generated if tests failed to produce execution data
            dependsOn(testTasks)

            reports {
                xml.required.set(true)
                html.required.set(true)
                csv.required.set(true)
            }
        }
    }
}

tasks.withType<Checkstyle>().configureEach {
    reports {
        xml.required.set(true)
        html.required.set(true)
    }
}

tasks.named("build") {
    dependsOn("cyclonedxBom")
}

tasks.named("check") {
    dependsOn(":connector-integrationtest:test")
}

tasks.register<JacocoReport>("jacocoRootReport") {
    group = "verification"
    description = "Generates an aggregate report from all subprojects"

    // Ensure all subproject tests run before generating the root report
    val testTasks = subprojects
        .filter { it.name != "connector-documentation" }
        .map { it.tasks.withType<Test>() }

    dependsOn(testTasks)

    // Configure the aggregated report data
    val subprojectsToAggregate = subprojects.filter { it.name != "connector-documentation" }

    // 1. Collect all execution data (.exec files)
    executionData.setFrom(files(subprojectsToAggregate.map {
        it.layout.buildDirectory.file("jacoco/test.exec")
    }))

    // 2. Collect all source files for the report
    sourceDirectories.setFrom(files(subprojectsToAggregate.map {
        it.layout.projectDirectory.dir("src/main/java")
    }))

    // 3. Collect all compiled class files, excluding generated code if necessary
    classDirectories.setFrom(files(subprojectsToAggregate.map {
        it.layout.buildDirectory.dir("classes/java/main")
    }))

    reports {
        xml.required.set(true)
        html.required.set(true)
        csv.required.set(true)
    }
}
