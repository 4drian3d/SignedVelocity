@file:Suppress("UnstableApiUsage")

rootProject.name = "SignedVelocity"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        maven("https://maven.fabricmc.net/")
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        maven("https://papermc.io/repo/repository/maven-public/")
        maven("https://repo.codemc.io/repository/maven-releases/")
        maven("https://repo.william278.net/velocity/")
        maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
        maven("https://maven.fabricmc.net/")
        maven("https://jitpack.io")
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
    id("org.spongepowered.gradle.plugin") version "2.2.0"
    id("fabric-loom") version "1.5.6"
}

include("signedvelocity-backend-common")
project(":signedvelocity-backend-common").projectDir = file("backend/common")
include("signedvelocity-paper")
project(":signedvelocity-paper").projectDir = file("backend/paper")
include("signedvelocity-minestom")
project(":signedvelocity-minestom").projectDir = file("backend/minestom")
include("signedvelocity-fabric")
project(":signedvelocity-fabric").projectDir = file("backend/fabric")

include("signedvelocity-sponge-common")
project(":signedvelocity-sponge-common").projectDir = file("backend/sponge/common")
include("signedvelocity-sponge-8")
project(":signedvelocity-sponge-8").projectDir = file("backend/sponge/API-8")
include("signedvelocity-sponge-10")
project(":signedvelocity-sponge-10").projectDir = file("backend/sponge/API-10")

include("signedvelocity-proxy")
project(":signedvelocity-proxy").projectDir = file("velocity")
