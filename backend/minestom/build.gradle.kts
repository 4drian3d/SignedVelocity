plugins {
    alias(libs.plugins.shadow)
}

dependencies {
    compileOnly(libs.minestom.server) {
        exclude("com.github.MadMartian", "hydrazine-path-finding")
    }
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
}