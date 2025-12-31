plugins {
    id("java")
    id("xyz.jpenilla.run-paper") version "3.0.2"
    id("com.gradleup.shadow") version "9.0.0-beta4"
}

dependencies {
    // Depend on the core GUI library
    implementation(project(":bukkit-gui"))

    // Spigot API
    compileOnly("org.spigotmc:spigot-api:1.19.2-R0.1-SNAPSHOT")

    // Adventure for modern component support
    val usePaper = true
    val adventureLibrary = if (usePaper) configurations.compileOnly else configurations.implementation
    adventureLibrary(libs.adventure.api)
    adventureLibrary(libs.adventure.bukkit)
    adventureLibrary(libs.adventure.text.serializer.legacy)
}

tasks {
    runServer {
        // Configure the Minecraft version for our task.
        // This is the only required configuration besides applying the plugin.
        // Your plugin's jar (or shadowJar if present) will be used automatically.
        minecraftVersion("1.21.11")
        standardInput = System.`in`
    }
}

// Ensure resources are processed
tasks.processResources {
    filesMatching("plugin.yml") {
        expand(
            "version" to project.version,
            "name" to "GuiExample"
        )
    }
}

// Configure shadowJar to build a fat JAR with all dependencies
tasks.shadowJar {
    archiveBaseName.set("GuiExample")
    archiveClassifier.set("")

    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

// Make build task depend on shadowJar
tasks.build {
    dependsOn(tasks.shadowJar)
}
