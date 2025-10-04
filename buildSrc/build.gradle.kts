plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
}

dependencies {
    implementation(libs.indra)
    implementation("cl.franciscosolis.sonatype-central-upload:cl.franciscosolis.sonatype-central-upload.gradle.plugin:1.0.3")
    compileOnly(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
}