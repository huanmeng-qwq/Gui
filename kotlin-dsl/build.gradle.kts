plugins {
    id("com.huanmeng-qwq.java-conventions")
    `kotlin-dsl`
}

dependencies {
    api("org.jetbrains.kotlin:kotlin-stdlib:2.0.0")
    api(project(":Bukkit-Gui"))
    compileOnly("org.spigotmc:spigot-api:1.19.2-R0.1-SNAPSHOT")
}

description = "Bukkit-Gui-kotlin-dsl"
