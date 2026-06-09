plugins {
    id("base")
}

val mockitoAgent: Configuration = configurations.create("mockitoAgent")

var bootstrapperJar: Provider<RegularFile> =
    project(":connector-bootstrapper")
        .tasks
        .named("bootJar")
        .flatMap { task ->
            @Suppress("UNCHECKED_CAST")
            (task as org.gradle.api.tasks.bundling.AbstractArchiveTask).archiveFile
        }

val copyBootJar by tasks.registering(Copy::class) {
    from(bootstrapperJar)
    into(layout.buildDirectory.dir("connector-distribution/bin"))
}

val copyConfig by tasks.registering(Copy::class) {
    from("src/main/resources/config") {
        include("application.properties", "log4j2.xml", "banner.txt")
    }
    into(layout.buildDirectory.dir("connector-distribution/config"))
}

val copyKeystores by tasks.registering(Copy::class) {
    from("src/main/resources/config/keystores") {
        include("*.jks")
    }
    into(layout.buildDirectory.dir("connector-distribution/config/keystores"))
}

val copyScripts by tasks.registering(Copy::class) {
    from("src/main/resources/bin") {
        include("start.sh", "start.bat")
    }
    eachFile {
        if (name == "start.sh") {
            filePermissions { unix("rwxr-xr-x") }
        }
    }
    into(layout.buildDirectory.dir("connector-distribution"))
}

val prepareDistribution by tasks.registering {
    dependsOn(copyBootJar, copyConfig, copyKeystores, copyScripts)
}

tasks.register<Zip>("distributionZip") {
    dependsOn(prepareDistribution)
    from(layout.buildDirectory.dir("connector-distribution"))
    archiveFileName.set("connector-distribution-${version}.zip")
    destinationDirectory.set(layout.buildDirectory)
}

tasks.named("assemble") {
    dependsOn("distributionZip")
}
