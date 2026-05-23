plugins {
    java
}

allprojects {
    apply<JavaPlugin>()
    tasks {
        withType<JavaCompile> {
            options.encoding = Charsets.UTF_8.name()
            options.release.set(25)
        }
    }
    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(25))
            vendor.set(JvmVendorSpec.AZUL)
        }
    }
}
