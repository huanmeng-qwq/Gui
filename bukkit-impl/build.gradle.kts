plugins { `java-library` }
dependencies {
    api(project(":bukkit-api"))
    implementation(project(":common-impl"))
    compileOnly("org.spigotmc:spigot-api:1.19.2-R0.1-SNAPSHOT")
}
