@file:Suppress("UnstableApiUsage")
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "SignedVelocity"

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        maven("https://maven.fabricmc.net/")
    }
}

dependencyResolutionManagement {
    repositories {
        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://repo.codemc.io/repository/maven-releases/")
        maven("https://repo.william278.net/velocity/")
        maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
    id("org.spongepowered.gradle.plugin") version "2.3.0"
    id("fabric-loom") version "1.15.4"
}

// Module Name to Module Folder
mapOf(
    "signedvelocity-backend-common" to "backend/common",
    "signedvelocity-paper" to "backend/paper",
    "signedvelocity-minestom" to "backend/minestom",
    "signedvelocity-fabric" to "backend/fabric",
    "signedvelocity-sponge-common" to "backend/sponge/common",
    "signedvelocity-sponge-10" to "backend/sponge/API-10",
    "signedvelocity-sponge-12" to "backend/sponge/API-12",
    "signedvelocity-proxy" to "velocity",
    "signedvelocity-shared" to "shared"
).forEach { (module, path) ->
    include(module)
    project(":$module").projectDir = file(path)
}
