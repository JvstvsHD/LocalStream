import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    application
    id("org.openjfx.javafxplugin") version "0.0.10"
    kotlin("jvm") version "1.5.30"
    id("com.github.johnrengelman.shadow") version "7.0.0"
}

group = "de.jvstvshd.localstream"
version = "1.0-SNAPSHOT"

val log4jVersion = "2.14.1"
val nettyVersion = "4.1.66.Final"

repositories {
    mavenCentral()
    mavenLocal()
}

application {
    mainModule.set("de.jvstvshd.localstream.client.desktop")
    mainClass.set("de.jvstvshd.localstream.client.desktop.LocalStreamClient")
}

javafx {
    version = "17"
    modules = listOf("javafx.controls", "javafx.fxml", "javafx.web")
}

dependencies {
    //logging
    implementation("org.apache.logging.log4j", "log4j-api", log4jVersion)
    implementation("org.apache.logging.log4j", "log4j-core", log4jVersion)

    //network & database
    implementation("io.netty", "netty-all", nettyVersion)
    implementation("de.jvstvshd.localstream:common:1.0.0-alpha")

    //utilities
    implementation("com.google.guava", "guava", "30.1.1-jre")
    implementation("org.apache.commons:commons-lang3:3.12.0")
    implementation("commons-io:commons-io:2.11.0")
    implementation("net.sf.jopt-simple:jopt-simple:6.0-alpha-2")
    implementation("net.lingala.zip4j:zip4j:2.9.0")
    implementation("com.googlecode.soundlibs", "mp3spi", "1.9.5-1")
    implementation("com.googlecode.soundlibs", "jlayer", "1.0.1-1")
    implementation("com.googlecode.soundlibs", "tritonus-share", "0.3.7.4")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.7.2")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.5.30")
}

java {
    sourceCompatibility = JavaVersion.VERSION_16
    targetCompatibility = JavaVersion.VERSION_16
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

tasks.withType<org.gradle.jvm.tasks.Jar>() {
    manifest {
        attributes["Main-Class"] = "de.jvstvshd.localstream.client.desktop.BootstrapKt"
        attributes["Multi-Release"] = true

    }
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}

val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "16"
}
val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "16"
}