plugins {
    id("base")
}

val mockitoAgent: Configuration = configurations.create("mockitoAgent")

var bootstrapperJar = project(":connector-bootstrapper").tasks.named("bootJar")

val prepareDistribution by tasks.registering(Copy::class) {
    dependsOn(bootstrapperJar)

    from(bootstrapperJar.map { it.outputs.files }) {
        include("*.jar")
        into("bin")
    }
    // copy application.properties and log4j2.xml externally
    from(project(":connector-bootstrapper").file("src/main/resources")) {
        include("application.properties", "log4j2.xml", "banner.txt")
        into("config")
    }
    from("src/main/resources/bin") {
        include("start.sh", "start.bat")
        eachFile {
            if (name == "start.sh") {
                filePermissions {
                    unix("rwxr-xr-x")
                }
            }
        }
    }

    into(layout.buildDirectory.dir("connector-distribution"))
}

// Task: Build a zip distribution
tasks.register<Zip>("distributionZip") {
    dependsOn(prepareDistribution)
    from(layout.buildDirectory.dir("connector-distribution"))
    archiveFileName.set("connector-distribution-${version}.zip")
    destinationDirectory.set(layout.buildDirectory)
}

tasks.named("assemble") {
    dependsOn("distributionZip")
}
