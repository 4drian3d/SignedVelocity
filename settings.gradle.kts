@file:Suppress("UnstableApiUsage")

rootProject.name = "SignedVelocity"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        maven("https://papermc.io/repo/repository/maven-public/")
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.7.0"
    id("org.spongepowered.gradle.plugin") version "2.1.1"
}

include("signedvelocity-backend-common")
project(":signedvelocity-backend-common").projectDir = file("backend/common")
include("signedvelocity-paper")
project(":signedvelocity-paper").projectDir = file("backend/paper")

include("signedvelocity-sponge-common")
project(":signedvelocity-sponge-common").projectDir = file("backend/sponge/common")
include("signedvelocity-sponge-8")
project(":signedvelocity-sponge-8").projectDir = file("backend/sponge/API-8")
include("signedvelocity-sponge-10")
project(":signedvelocity-sponge-10").projectDir = file("backend/sponge/API-10")

include("signedvelocity-proxy")
project(":signedvelocity-proxy").projectDir = file("velocity")
