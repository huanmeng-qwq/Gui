plugins { `java-library` }
dependencies {
    api(project(":common-api"))
    compileOnly("org.spigotmc:spigot-api:1.19.2-R0.1-SNAPSHOT")
}
