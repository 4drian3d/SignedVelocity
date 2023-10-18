plugins {
    alias(libs.plugins.shadow)
}

dependencies {
    compileOnly(libs.minestom)
    implementation(projects.signedvelocityBackendCommon)
}

tasks {
    build {
        dependsOn(shadowJar)
    }
    shadowJar {
        archiveBaseName.set("${rootProject.name}-Minestom")
        archiveClassifier.set("")
        doLast {
            copy {
                from(archiveFile)
                into("${rootProject.projectDir}/build")
            }
        }
    }
    processResources {
        filesMatching("extension.json") {
            expand("version" to project.version)
        }
    }
}