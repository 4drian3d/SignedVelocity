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
}

arrayOf(
        "paper",
        "velocity"
).forEach {
    include("signedvelocity-$it")
    project(":signedvelocity-$it").projectDir = file(it)
}