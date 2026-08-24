import net.fabricmc.loom.api.LoomGradleExtensionAPI
import net.fabricmc.loom.api.fabricapi.FabricApiExtension

plugins {
    id("architectury-plugin") version "3.5-SNAPSHOT"
    // Both "apply false": the actual plugin is chosen below via apply(plugin = ...), based on no_remap.
    id("dev.architectury.loom") version "1.17-SNAPSHOT" apply false
    id("dev.architectury.loom-no-remap") version "1.17-SNAPSHOT" apply false
    id("java")
    id("maven-publish")
    id("com.modrinth.minotaur") version "2.9.0"
}

// "loom.platform" comes from the node's gradle.properties; absent means fabric by default.
val loader = (project.findProperty("loom.platform") as String? ?: "fabric").lowercase()

// From Minecraft 26.1 on, the game jar is no longer obfuscated, so those nodes use the
// dev.architectury.loom-no-remap plugin instead and set no_remap=true.
val noRemap = (project.findProperty("no_remap") as String?)?.toBoolean() ?: false
apply(plugin = if (noRemap) "dev.architectury.loom-no-remap" else "dev.architectury.loom")

// Declares the //? if fabric / //? if neoforge constants used in shared code.
stonecutter.constants.match(loader, "fabric", "neoforge")

base {
    archivesName.set("${rootProject.property("archives_base_name")}-$loader")
}

version = "${rootProject.property("mod_version")}+${project.property("minecraft_version")}"
group = rootProject.property("maven_group") as String

// Depends on the Minecraft version, not the loader: 1.20.1 needs Java 17, 1.20.5+ needs Java 21.
val javaVersion = (project.findProperty("java_version") as String? ?: "21").toInt()

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.release.set(javaVersion)
}

java {
    withSourcesJar()
}

architectury {
    minecraft = project.property("minecraft_version") as String
    platformSetupLoomIde()
    if (loader == "fabric") fabric() else neoForge()
}

// See the plugins{} block above for why the typed loom{} accessor isn't available here.
val loomExtension = project.extensions.getByType(LoomGradleExtensionAPI::class.java)
loomExtension.apply {
    // No splitEnvironmentSourceSets(): a single main sourceSet is shared between common and
    // client-only code.
    if (loader == "fabric") {
        mods {
            create(rootProject.property("archives_base_name").toString().lowercase()) {
                sourceSet(sourceSets["main"])
            }
        }
    }

    runs {
        named("client") {
            runDir("run/client")
        }
        named("server") {
            runDir("run/server")
        }
    }
}

if (loader == "fabric") {
    project.extensions.getByType(FabricApiExtension::class.java).apply {
        configureDataGeneration {
            client = true
        }
    }
}

// Classes specific to the other loader don't compile here; excluded from both the
// SourceDirectorySet and stonecutter.filters.
val excludedPlatform = if (loader == "fabric") "neoforge" else "fabric"
val basePackagePath = (rootProject.property("maven_group") as String).replace(".", "/") +
    "/" + rootProject.property("archives_base_name").toString().lowercase()
sourceSets["main"].java.exclude("$basePackagePath/platforms/$excludedPlatform/**")
stonecutter.filters.exclude("java/$basePackagePath/platforms/$excludedPlatform/**")

// Same problem on the resources side: only one of fabric.mod.json / neoforge.mods.toml should end
// up in this node's jar.
sourceSets["main"].resources.exclude(
    if (loader == "fabric") "META-INF/neoforge.mods.toml" else "fabric.mod.json"
)

// Without this call, Stonecutter creates no task to process //? if markers for this sourceSet.
stonecutter.tasks.configureSource(sourceSets["main"])

repositories {
    maven("https://maven.fabricmc.net/")
    maven("https://maven.architectury.dev/")
    maven("https://maven.neoforged.net/releases/")
    // Some of Create's transitive dependencies (e.g. night-config) only live on Maven Central.
    mavenCentral()

    // Cloth Config (both loaders).
    maven("https://maven.shedaniel.me/")

    if (loader == "fabric") {
        maven("https://maven.createmod.net") // Create Fabric, Flywheel, Ponder
        maven("https://mvn.devos.one/releases") // Porting Lib
        maven("https://mvn.devos.one/snapshots") // Registrate, Milk Lib
        maven("https://maven.jamieswhiteshirt.com/libs-release") // Reach Entity Attributes
        maven("https://raw.githubusercontent.com/Fuzss/modresources/main/maven") // Forge Config API Port
        maven("https://maven.terraformersmc.com/releases/") // ModMenu
    } else {
        maven("https://maven.createmod.net") // Create, Ponder, Flywheel (NeoForge)
        maven("https://maven.ithundxr.dev/snapshots") // Registrate
    }
}

// Under loom-no-remap, modImplementation isn't registered, so plain "implementation" is used instead.
val modConfig = if (noRemap) "implementation" else "modImplementation"

dependencies {
    "minecraft"("com.mojang:minecraft:${project.property("minecraft_version")}")

    // Official Mojang mappings are used instead of Yarn on the still-obfuscated nodes, so shared
    // source code uses one naming vocabulary across every Minecraft version.
    if (!noRemap) {
        "mappings"(loomExtension.officialMojangMappings())
    }

    if (loader == "fabric") {
        add(modConfig, "net.fabricmc:fabric-loader:${project.property("loader_version")}")
        add(modConfig, "net.fabricmc.fabric-api:fabric-api:${project.property("fabric_version")}")
        add(modConfig, "dev.architectury:architectury-fabric:${project.property("architectury_version")}")

        // Guarded by create_version: Create isn't published for every node yet.
        if (project.hasProperty("create_version")) {
            // Create Fabric pulls its own transitive dependencies (Porting Lib, Flywheel, Milk
            // Lib, ...) through this single modImplementation.
            add(modConfig, "com.simibubi.create:create-fabric:${project.property("create_version")}")
        }

        // Cloth Config + ModMenu (in-game config screen), required.
        add(modConfig, "me.shedaniel.cloth:cloth-config-fabric:${project.property("cloth_config_version")}") {
            // Cloth Config bundles its own copy of a few Fabric API modules; fabric-api above
            // already provides them.
            exclude(group = "net.fabricmc.fabric-api")
        }
        add(modConfig, "com.terraformersmc:modmenu:${project.property("modmenu_version")}")
    } else {
        "neoForge"("net.neoforged:neoforge:${project.property("neoforge_version")}")
        add(modConfig, "dev.architectury:architectury-neoforge:${project.property("architectury_version")}")

        // Same create_version guard as the Fabric branch above.
        if (project.hasProperty("create_version")) {
            // Unlike Create Fabric, Create NeoForge does not pull its dependencies transitively;
            // Ponder, Flywheel, and Registrate need their own explicit lines.
            "implementation"("com.simibubi.create:create-${project.property("minecraft_version")}:${project.property("create_version")}:slim") {
                isTransitive = false
            }
            "implementation"("net.createmod.ponder:ponder-neoforge:${project.property("ponder_version")}+mc${project.property("minecraft_version")}")
            "compileOnly"("dev.engine-room.flywheel:flywheel-neoforge-api-${project.property("minecraft_version")}:${project.property("flywheel_version")}")
            "runtimeOnly"("dev.engine-room.flywheel:flywheel-neoforge-${project.property("minecraft_version")}:${project.property("flywheel_version")}")
            "implementation"("com.tterrag.registrate:Registrate:${project.property("registrate_version")}")
        }

        // Cloth Config (in-game config screen), required. No ModMenu equivalent needed: NeoForge
        // exposes the config screen natively via IConfigScreenFactory.
        "implementation"("me.shedaniel.cloth:cloth-config-neoforge:${project.property("cloth_config_version")}")
    }
}

// fabric.mod.json / neoforge.mods.toml are shared between nodes but contain ${...} tokens whose
// value depends on the node, substituted here via plain Gradle expand().
tasks.processResources {
    if (loader == "fabric") {
        filesMatching("fabric.mod.json") {
            expand(
                mapOf(
                    "version" to project.version,
                    "minecraft_version_range" to project.property("minecraft_version_range"),
                    "loader_version" to project.property("loader_version")
                )
            )
        }
    } else {
        filesMatching("META-INF/neoforge.mods.toml") {
            expand(
                mapOf(
                    "version" to project.version,
                    "mc_range_lower" to project.property("mc_range_lower"),
                    "mc_range_upper" to project.property("mc_range_upper"),
                    "neoforge_version" to project.property("neoforge_version"),
                    "neo_loader_version_range" to project.property("neo_loader_version_range"),
                    "cloth_config_version" to project.property("cloth_config_version")
                )
            )
        }
    }
}

publishing {
    repositories {
        // Add a Maven repository here if you also want to publish somewhere other than Modrinth.
    }
    publications {
        create<MavenPublication>("maven${loader.replaceFirstChar { it.uppercase() }}") {
            artifactId = base.archivesName.get()
            from(components["java"])
        }
    }
}

// Modrinth publishing, runs from .github/workflows/gradle-publish.yml on every GitHub Release.
// Needs a MODRINTH_TOKEN repository secret.
modrinth {
    token.set(System.getenv("MODRINTH_TOKEN"))
    projectId.set("create-immediate-departure")
    // versionNumber must stay unique per node (mc+loader) to avoid collisions between two nodes
    // sharing the same minecraft_version (e.g. 1.21.1-fabric and 1.21.1-neoforge).
    versionNumber.set("${rootProject.property("mod_version")}+${project.property("minecraft_version")}-$loader")
    // versionName is what Modrinth DISPLAYS: loader and MC version already have their own columns
    // in the version list, no need to repeat them here.
    versionName.set(rootProject.property("mod_version") as String)
    versionType.set("release")
    changelog.set(System.getenv("CHANGELOG") ?: "See the commits for changelog details.")
    // "remapJar" doesn't exist under loom-no-remap; the plain "jar" is the right artifact in that mode.
    uploadFile.set(tasks.named(if (noRemap) "jar" else "remapJar"))
    gameVersions.set(listOf(project.property("minecraft_version") as String))
    loaders.set(listOf(loader))
    dependencies {
        required.project("architectury-api")
        required.project("cloth-config")
        if (loader == "fabric") {
            required.project("fabric-api")
            required.project("create-fabric")
            required.project("modmenu")
        } else {
            required.project("create")
        }
    }
}
