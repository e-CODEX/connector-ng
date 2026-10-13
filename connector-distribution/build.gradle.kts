plugins {
    id("base")
    id("maven-publish")
}

val mockitoAgent: Configuration = configurations.create("mockitoAgent")

val standalonePath = "connector-distribution/standalone"

var bootstrapperJar: Provider<RegularFile> =
    project(":connector-bootstrapper")
        .tasks
        .named("bootJar")
        .flatMap { task ->
            @Suppress("UNCHECKED_CAST")
            (task as AbstractArchiveTask).archiveFile
        }


val copyBootJar = tasks.register<Copy>("copyBootJar") {
    description = "Copies the connector jar into the distribution directory"
    from(bootstrapperJar)
    into(layout.buildDirectory.dir("$standalonePath/bin"))
}

val copyConfig = tasks.register<Copy>("copyConfig") {
    description = "Copies the connector configuration into the distribution directory"
    from("src/main/resources/config") {
        include("application.properties", "log4j2.xml", "banner.txt")
    }
    into(layout.buildDirectory.dir("$standalonePath/config"))
}

val copyKeystores = tasks.register<Copy>("copyKeystores") {
    description = "Copies the connector sample keystores into the distribution directory"
    from("src/main/resources/config/keystores") {
        include("*.jks")
    }
    into(layout.buildDirectory.dir("$standalonePath/config/keystores"))
}

val copyScripts = tasks.register<Copy>("copyScripts") {
    description = "Copy the connector startup scripts into the distribution directory"
    from("src/main/resources/bin") {
        include("start.sh", "start.bat")
    }
    eachFile {
        if (name == "start.sh") {
            filePermissions { unix("rwxr-xr-x") }
        }
    }
    into(layout.buildDirectory.dir(standalonePath))
}

val copyDocumentation = tasks.register<Copy>("copyDocumentation") {
    description = "Copy the connector documentation into the distribution directory"
    dependsOn(project(":connector-documentation").tasks.named("build"))
    from(project(":connector-documentation").layout.buildDirectory.dir("docs/asciidoc")) {
        exclude("**/.asciidoctor/**")
    }
    into(layout.buildDirectory.dir("connector-distribution/documentation"))
}

val copyJdbcDrivers = tasks.register<Copy>("copyJdbcDrivers") {
    description = "Copies JDBC drivers into the distribution lib directory"

    val jdbcDriverNames = setOf("mysql", "mariadb", "postgresql", "ojdbc11")

    var runtimeClasspath =
        project(":connector-infrastructure").configurations.named("runtimeClasspath")
    from(
        runtimeClasspath.map { rc ->
            rc.filter { file -> jdbcDriverNames.any { name -> file.name.startsWith(name) } }
        }
    )
    into(layout.buildDirectory.dir("$standalonePath/lib"))
}

val copyDbScripts = tasks.register<Copy>("copyDbScripts") {
    description = "Copy the connector database scripts into the distribution directory"
    from("src/main/resources/dbscripts")
    into(layout.buildDirectory.dir("connector-distribution/dbscripts"))
}

val prepareDistribution = tasks.register("prepareDistribution") {
    description = "Prepares the distribution directory"
    dependsOn(
        copyBootJar,
        copyConfig,
        copyKeystores,
        copyScripts,
        copyDocumentation,
        copyJdbcDrivers,
        copyDbScripts
    )
}

tasks.register<Zip>("distributionZip") {
    description = "Creates a zip file containing the connector distribution"
    dependsOn(prepareDistribution)
    from(layout.buildDirectory.dir("connector-distribution"))
    archiveFileName.set("connector-distribution-${version}.zip")
    destinationDirectory.set(layout.buildDirectory)
}

tasks.named("assemble") {
    dependsOn("distributionZip")
}

publishing {
    publications {
        create<MavenPublication>("mavenDistribution") {
            artifactId = "connector-distribution"
            artifact(tasks.named<Zip>("distributionZip"))
        }
    }
    repositories {
        val releaseRepo = providers.gradleProperty("artifactory.url.release").get()
        val snapshotRepo = providers.gradleProperty("artifactory.url.snapshot").get()
        val repoId = providers.gradleProperty("artifactory.repo.id")
            .orElse(providers.environmentVariable("MAVEN_REPO_ID"))
            .get()

        maven {
            name = repoId
            url = uri(if (version.toString().endsWith("-SNAPSHOT")) snapshotRepo else releaseRepo)
            credentials {
                username = System.getenv("MAVEN_REPO_USERNAME")
                password = System.getenv("MAVEN_REPO_PASSWORD")
            }
        }
    }
}
