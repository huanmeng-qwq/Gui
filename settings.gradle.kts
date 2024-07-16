rootProject.name = "Bukkit-Gui-pom"
include(":Bukkit-Gui")
include(":Bukkit-Gui-kotlin-dsl")
project(":Bukkit-Gui").projectDir = file("core")
project(":Bukkit-Gui-kotlin-dsl").projectDir = file("kotlin-dsl")
