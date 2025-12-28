plugins {
    id("me.huanmeng.gui.publish-conventions")
}

dependencies {
    implementation(libs.adventure.api)
    implementation(libs.adventure.bukkit)
    implementation(libs.adventure.text.serializer.legacy)
    compileOnly("org.spigotmc:spigot-api:1.19.2-R0.1-SNAPSHOT")
}

tasks.compileJava {
    exclude("**/test/**")
}

tasks.processResources {
    exclude("plugin.yml")
}