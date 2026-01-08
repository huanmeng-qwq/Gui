plugins {
    id("me.huanmeng.gui.publish-conventions")
}

dependencies {
    compileOnly(libs.adventure.api)
    compileOnly(libs.adventure.text.serializer.legacy)
    compileOnly(libs.adventure.text.serializer.gson)
    compileOnly(libs.adventure.platform.api)
    compileOnly("org.spigotmc:spigot-api:1.19.2-R0.1-SNAPSHOT")
}

tasks.compileJava {
    exclude("**/test/**")
}

tasks.processResources {
    exclude("plugin.yml")
}