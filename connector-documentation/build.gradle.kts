import org.asciidoctor.gradle.jvm.AsciidoctorTask

plugins {
    id("org.asciidoctor.jvm.convert") version "4.0.5"
}

// common asciidoctor configuration
fun org.asciidoctor.gradle.jvm.AbstractAsciidoctorTask.commonConfig() {
    baseDirFollowsSourceDir()
    // point to the actual directory from your project structure
    setSourceDir(file("src/docs/asciidoc"))
    // define which files to process (e.g. only index.adoc)
    sources {
        include(
            "architecture/index.adoc",
            "configuration/index.adoc",
            "installation/index.adoc",
            "changelog/index.adoc"
        )
    }
    // configure resources (images, etc.)
    resources {
        from("src/docs/resources")
        into("resources/**")
    }
    attributes(
        mapOf(
            "project-version" to project.version,
            "toc" to "left",
            "toclevels" to "4",
            "icons" to "font",
            "sectnums" to "",
            "numbered" to "",
            "source-highlighter" to "rouge",
            "rouge-style" to "github",
            "imagesdir" to "resources",
            "rootdir" to sourceDir.absolutePath
        )
    )
}

tasks.withType<AsciidoctorTask>().configureEach {
    commonConfig()
    setOutputDir(layout.buildDirectory.dir("docs/html"))
    notCompatibleWithConfigurationCache("Asciidoctor does not support configuration caching yet.")
}

tasks.named("build") {
    dependsOn("asciidoctor")
}
