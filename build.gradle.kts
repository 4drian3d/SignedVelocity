plugins {
    java
}

allprojects {
    apply<JavaPlugin>()
    tasks {
        withType<JavaCompile> {
            options.encoding = Charsets.UTF_8.name()
            options.release.set(21)
        }
    }
    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(21))
            vendor.set(JvmVendorSpec.AZUL)
        }
    }
}
