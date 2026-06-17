import org.asciidoctor.gradle.jvm.AbstractAsciidoctorTask
import org.asciidoctor.gradle.jvm.AsciidoctorTask

plugins {
    id("org.asciidoctor.jvm.convert") version "4.0.5"
}

asciidoctorj {
    modules {
        diagram.use()
    }
}

val generatedDiagramsDir = layout.buildDirectory.dir("docs/html/resources")

// common asciidoctor configuration
fun AbstractAsciidoctorTask.commonConfig(version: String) {
    baseDirFollowsSourceDir()
    // point to the actual directory from your project structure
    setSourceDir(layout.projectDirectory.dir("src/docs/asciidoc"))
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
        from("src/docs/resources/images")
        into("resources")
    }
    attributes(
        mapOf(
            "project-version" to version,
            "toc" to "left",
            "toclevels" to "4",
            "icons" to "font",
            "sectnums" to "",
            "numbered" to "",
            "source-highlighter" to "rouge",
            "rouge-style" to "github",
            "basepath" to sourceDir.absolutePath,
            "imagesdir" to "resources",
            // pin where PlantUML/diagram module writes generated SVGs
            "imagesoutdir" to generatedDiagramsDir.get().asFile.absolutePath
        )
    )
}

val copyDiagramsToResources by tasks.registering(Copy::class) {
    description = "Copies generated diagrams to the resources directory"
    dependsOn("asciidoctor")
    from(generatedDiagramsDir)
    into(layout.buildDirectory.dir("docs/html/resources/images"))
}

tasks.withType<AsciidoctorTask>().configureEach {
    commonConfig(project.version.toString())
    setOutputDir(layout.buildDirectory.dir("docs/html"))
    notCompatibleWithConfigurationCache("Asciidoctor does not support configuration caching yet.")
}

tasks.named("build") {
    dependsOn("asciidoctor")
}
