import java.time.LocalDate

plugins {
    `java-library`
    id("com.vanniktech.maven.publish") version "0.34.0"
}

dependencies {
    compileOnly(libs.minestom.server) {
        exclude("com.github.MadMartian", "hydrazine-path-finding")
    }
    api(projects.signedvelocityBackendCommon)
    implementation(projects.signedvelocityShared)
}

mavenPublishing {
    publishToMavenCentral()
    signAllPublications()
    coordinates(project.group as String, "signedvelocity-minestom", project.version as String)

    pom {
        name.set(project.name)
        description.set(project.description)
        inceptionYear.set(LocalDate.now().year.toString())
        url.set("https://github.com/4drian3d/SignedVelocity")
        licenses {
            license {
                name.set("GNU General Public License version 3 or later")
                url.set("https://opensource.org/licenses/GPL-3.0")
            }
        }
        developers {
            developer {
                id.set("4drian3d")
                name.set("Adrian Gonzales")
                email.set("adriangonzalesval@gmail.com")
            }
        }
        scm {
            connection.set("scm:git:https://github.com/4drian3d/SignedVelocity.git")
            developerConnection.set("scm:git:ssh://git@github.com/4drian3d/SignedVelocity.git")
            url.set("https://github.com/4drian3d/SignedVelocity")
        }
        ciManagement {
            name.set("GitHub Actions")
            url.set("https://github.com/4drian3d/SignedVelocity")
        }
        issueManagement {
            name.set("GitHub")
            url.set("https://github.com/4drian3d/SignedVelocity/issues")
        }
    }
}
