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
    mavenLocal()
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
    implementation("io.github.cdsap:geapi-data:0.1.6")
    implementation("org.slf4j:slf4j-simple:1.6.1")
    implementation("com.jakewharton.picnic:picnic:0.6.0")
    implementation("com.github.ajalt.clikt:clikt:3.5.0")
    implementation("org.nield:kotlin-statistics:1.2.1")
    implementation("com.google.code.gson:gson:2.8.5")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

//
//Timestamp        S0C    S1C      S0U    S1U      EC        EU        OC         OU       MC         MU      CCSC   CCSU   YGC        YGCT      FGC      FGCT    CGC        CGCT     GCT
//    963.5         0.0   94208.0  0.0   94208.0 4612096.0 3125248.0 2957312.0  2181973.9  829704.0 781441.8 96640.0 82434.7    302    9.117   0      0.000  18      0.317    9.434
