import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.21"
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
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    implementation("io.github.cdsap:geapi-data:0.2.7")
    implementation("org.slf4j:slf4j-simple:1.7.36")
    implementation("com.jakewharton.picnic:picnic:0.6.0")
    implementation("com.github.ajalt.clikt:clikt:3.5.4")
    implementation("org.nield:kotlin-statistics:1.2.1")
    implementation("com.google.code.gson:gson:2.8.9")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}
