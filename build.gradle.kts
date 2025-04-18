import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.9.25"
    application
    id("io.github.cdsap.fatbinary") version "1.0"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}
application {
    mainClass.set("io.github.cdsap.taskreport.MainKt")
}

fatBinary {
    mainClass = "io.github.cdsap.taskreport.Main"
    name = "taskreport"
}

dependencies {
    implementation("org.jetbrains.lets-plot:lets-plot-kotlin-jvm:4.1.1")
    implementation( "org.jetbrains.lets-plot:lets-plot-image-export:2.5.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
    implementation("io.github.cdsap:geapi-data:0.3.3")
    implementation("org.slf4j:slf4j-simple:2.0.16")
    implementation("com.jakewharton.picnic:picnic:0.7.0")
    implementation("com.github.ajalt.clikt:clikt:4.4.0")
    implementation("org.nield:kotlin-statistics:1.2.1")
    implementation("com.google.code.gson:gson:2.11.0")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
}
