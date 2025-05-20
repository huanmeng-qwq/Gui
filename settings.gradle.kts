plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "Gui"
include(":bukkit-gui")
include(":bukkit-gui-kotlin-dsl")
project(":bukkit-gui").apply {
    projectDir = file("core")
}
project(":bukkit-gui-kotlin-dsl").apply {
    projectDir = file("kotlin-dsl")
}
