plugins {
    id("com.huanmeng-qwq.java-conventions")
}

dependencies {
    api("net.kyori:adventure-api:4.14.0")
    api("net.kyori:adventure-platform-bukkit:4.3.1")
    api("net.kyori:adventure-text-serializer-legacy:4.14.0")
    api("org.bstats:bstats-bukkit:3.0.2")
    compileOnly("org.spigotmc:spigot-api:1.19.2-R0.1-SNAPSHOT")
    compileOnly("org.jetbrains:annotations:24.1.0")
}

description = "Bukkit-Gui"