plugins {
    id("application")
    id("org.openjfx.javafxplugin") version "0.1.0"
    kotlin("jvm") version "2.0.20"
}

group = "shmax"
version = "a1.0.0"

java.sourceCompatibility = JavaVersion.VERSION_21


repositories {
    mavenCentral()
}

dependencies {}

javafx {
    version = "22.0.1"
    modules = mutableListOf("javafx.controls")
}

tasks.named("compileKotlin", org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask::class.java) {
    compilerOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
    }
}

sourceSets {
    main {
        java.srcDir("src")
        kotlin.srcDirs("src/main/java", "src/main/kotlin")
        resources.srcDir("src/main/resources")
    }
}

application {
    mainClass = "MainKt"
}