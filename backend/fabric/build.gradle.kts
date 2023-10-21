plugins {
    id("fabric-loom")
    alias(libs.plugins.shadow)
}

val shade: Configuration by configurations.creating

dependencies {
    minecraft(libs.minecraft)
    mappings(loom.officialMojangMappings())
    modImplementation(libs.fabric.loader)
    modImplementation(libs.fabric.api)
    include(libs.mixinsextras)
    implementation(libs.mixinsextras)
    annotationProcessor(libs.mixinsextras)

    shadeModule(projects.signedvelocityBackendCommon)
}

fun DependencyHandlerScope.shadeModule(module: ProjectDependency) {
    shade(module) {
        isTransitive = false
    }
    implementation(module) {
        isTransitive = false
    }
}

tasks {
    shadowJar {
        configurations = listOf(shade)
    }
    processResources {
        filesMatching("fabric.mod.json") {
            expand("version" to project.version)
        }
    }
    remapJar {
        inputFile.set(shadowJar.get().archiveFile)
        archiveFileName.set("${rootProject.name}-Fabric-${project.version}.jar")
        destinationDirectory.set(file("${rootProject.projectDir}/build"))
    }
}

java {
    withSourcesJar()
}