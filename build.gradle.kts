import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.10"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.2")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.8.20")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
}