import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("me.huanmeng.gui.publish-conventions")
    kotlin("jvm")
}

dependencies {
    implementation(project(":bukkit-gui"))
    compileOnly("org.spigotmc:spigot-api:1.19.2-R0.1-SNAPSHOT")
}

kotlin {
    compilerOptions.jvmTarget = JvmTarget.JVM_1_8
}