pluginManagement {
    repositories {
        maven("https://maven.fabricmc.net/")
        maven("https://maven.architectury.dev/")
        maven("https://maven.minecraftforge.net/")
        maven("https://maven.neoforged.net/releases/")
        gradlePluginPortal()
    }
}

plugins {
    id("dev.kikugie.stonecutter") version "0.9.7"
    // Auto-provisions missing JDKs (e.g. Java 25, required by MC 26.1+) via the Foojay Disco API.
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

// Flat Architectury layout: no separate :common subproject. Each node (Minecraft version + loader)
// is a standalone Gradle project; shared and loader-specific code live in the same source tree,
// distinguished by //? if fabric/neoforge markers.
//
// Only two nodes for now: Create isn't published for 1.21.1-fabric or 26.1.2 yet. Add those back
// once Create ships a build for them.
stonecutter {
    create(rootProject) {
        versions(
            "1.20.1-fabric" to "1.20.1",
            "1.21.1-neoforge" to "1.21.1",
        )
    }
}

rootProject.name = "CreateImmediateDeparture"
