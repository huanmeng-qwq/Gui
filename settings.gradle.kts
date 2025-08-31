plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.10.0"
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
