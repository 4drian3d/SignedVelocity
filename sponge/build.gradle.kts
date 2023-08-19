import org.spongepowered.gradle.plugin.config.PluginLoaders
import org.spongepowered.plugin.metadata.model.PluginDependency

plugins {
    id("org.spongepowered.gradle.plugin")
    alias(libs.plugins.shadow)
}

dependencies {
    implementation(projects.signedvelocityCommonBackend)
}

sponge {
    apiVersion("8.1.0")
    license("GPL-3")
    loader {
        name(PluginLoaders.JAVA_PLAIN)
        version("1.0")
    }
    plugin("signedvelocity") {
        displayName("SignedVelocity")
        entrypoint("io.github._4drian3d.signedvelocity.sponge.SignedVelocity")
        description(project.description)
        links {
            homepage("https://github.com/4drian3d/SignedVelocity")
            source("https://github.com/4drian3d/SignedVelocity")
            issues("https://github.com/4drian3d/SignedVelocity/issues")
        }
        contributor("4drian3d") {
            description("Lead Developer")
        }
        dependency("spongeapi") {
            loadOrder(PluginDependency.LoadOrder.AFTER)
            optional(false)
        }
    }
}

tasks.build {
    dependsOn(tasks.shadowJar)
}