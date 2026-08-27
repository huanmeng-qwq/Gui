plugins { `java-library` }
dependencies {
    api(project(":bukkit-api"))
    api(project(":common-impl"))
    compileOnly(libs.adventure.api)
    compileOnly(libs.adventure.text.serializer.legacy)
    compileOnly("org.spigotmc:spigot-api:1.19.2-R0.1-SNAPSHOT")
}
