plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "Gui"
include(":common-api")
include(":common-impl")
include(":bukkit-api")
include(":bukkit-impl")
include(":bukkit-gui")
include(":bukkit-gui-kotlin-dsl")
include(":bukkit-gui-example")
project(":common-api").apply {
    projectDir = file("common-api")
}
project(":common-impl").apply {
    projectDir = file("common-impl")
}
project(":bukkit-api").apply {
    projectDir = file("bukkit-api")
}
project(":bukkit-impl").apply {
    projectDir = file("bukkit-impl")
}
project(":bukkit-gui").apply {
    projectDir = file("core")
}
project(":bukkit-gui-kotlin-dsl").apply {
    projectDir = file("kotlin-dsl")
}
project(":bukkit-gui-example").apply {
    projectDir = file("test")
}
