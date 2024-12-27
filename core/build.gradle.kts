plugins {
    id("me.huanmeng.gui.publish-conventions")
}

dependencies {
    implementation("net.kyori:adventure-api:4.14.0")
    implementation("net.kyori:adventure-platform-bukkit:4.3.1")
    implementation("net.kyori:adventure-text-serializer-legacy:4.14.0")
    implementation("org.bstats:bstats-bukkit:3.0.2")
    compileOnly("org.spigotmc:spigot-api:1.19.2-R0.1-SNAPSHOT")
}

tasks.compileJava{
    exclude("**/test/**")
}

tasks.processResources{
    exclude("plugin.yml")
}