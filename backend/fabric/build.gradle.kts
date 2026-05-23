plugins {
    id("net.fabricmc.fabric-loom")
    alias(libs.plugins.shadow)
}

val shade: Configuration by configurations.creating

dependencies {
    minecraft(libs.minecraft)
    implementation(libs.fabric.loader)
    implementation(libs.fabric.api)

    shadeModule(projects.signedvelocityBackendCommon)
    shadeModule(projects.signedvelocityShared)
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
    jar {
//        inputFile.set(shadowJar.get().archiveFile)
        archiveFileName.set("${rootProject.name}-Fabric-${project.version}.jar")
        destinationDirectory.set(file("${rootProject.projectDir}/build"))
    }
}

java {
    withSourcesJar()
}