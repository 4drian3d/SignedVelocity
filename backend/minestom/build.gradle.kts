plugins {
    alias(libs.plugins.shadow)
}

dependencies {
    compileOnly(libs.minestom.server) {
        exclude("com.github.MadMartian", "hydrazine-path-finding")
    }
    compileOnly(libs.minestom.extensions)
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