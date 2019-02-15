/*
 * This file was generated by the Gradle 'init' task.
 *
 * This generated file contains a sample Kotlin application project to get you started.
 */

plugins {
    // Apply the Kotlin JVM plugin to add support for Kotlin on the JVM.
    id("org.jetbrains.kotlin.jvm").version("1.3.20")

    // Apply the application plugin to add support for building a CLI application.
    application
}

repositories {
    // Use jcenter for resolving your dependencies.
    // You can declare any Maven/Ivy/file repository here.
    jcenter()
}

val jacksonVersion = "2.9.4"

dependencies {
    // Use the Kotlin JDK 8 standard library.
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("com.dc2f:dc2f:0.0.1-SNAPSHOT")

    implementation("io.github.microutils:kotlin-logging:1.4.9")

    implementation("org.eclipse.lsp4j:org.eclipse.lsp4j:0.6.0")

    implementation("org.jetbrains.kotlin:kotlin-reflect")

    // utils
    implementation("org.apache.commons:commons-lang3:3.8.1")
    implementation("org.reflections:reflections:0.9.11")

    // yaml deserialize
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:$jacksonVersion")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:$jacksonVersion")



    // Use the Kotlin test library.
    testImplementation("org.jetbrains.kotlin:kotlin-test")

    // Use the Kotlin JUnit integration.
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.4.0")
}

application {
    // Define the main class for the application.
    mainClassName = "com.dc2f.lsp.Dc2fLspServer"
}